package com.anilazahid.quiz;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class AdminTournamentsActivity extends AdminBaseActivity {
    private final List<DataSnapshot> tournaments = new ArrayList<>();
    private TournamentAdapter adapter;
    private android.widget.Button seedButton;
    private boolean seeding;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_admin_list);
        ((TextView) findViewById(R.id.adminListTitle)).setText("TOURNAMENT MANAGEMENT");
        ((android.widget.Button) findViewById(R.id.adminListAdd)).setText("CREATE");
        findViewById(R.id.adminListAdd).setOnClickListener(v -> editTournament(null));
        seedButton = new android.widget.Button(this);
        seedButton.setText("SEED 120 TOURNAMENTS");
        seedButton.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("SEED TOURNAMENTS?").setMessage("Create or repair 100 ongoing and 20 upcoming free tournaments across the 15 official categories.").setNegativeButton("CANCEL", null).setPositiveButton("SEED", (d, w) -> seedInitialTournaments()).show());
        ((ViewGroup) findViewById(R.id.adminListTitle).getParent()).addView(seedButton, 1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        findViewById(R.id.adminListSearch).setVisibility(View.GONE);
        findViewById(R.id.adminListBack).setOnClickListener(v -> finish());
        RecyclerView list = findViewById(R.id.adminRecycler);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TournamentAdapter();
        list.setAdapter(adapter);
        requireAdmin(this::load);
    }

    private void load() {
        db.child("tournaments").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                tournaments.clear();
                for (DataSnapshot item : snapshot.getChildren()) tournaments.add(item);
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(DatabaseError error) { toast("Could not load tournaments."); }
        });
    }

    private EditText field(LinearLayout form, String hint) {
        EditText input = new EditText(this);
        input.setHint(hint);
        form.addView(input);
        return input;
    }

    private void editTournament(DataSnapshot old) {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(24, 0, 24, 0);
        EditText title = field(form, "Title *");
        EditText description = field(form, "Description");
        EditText category = field(form, "Category *");
        EditText totalQuestions = field(form, "Question count *");
        EditText startTime = field(form, "Start time (epoch milliseconds) *");
        EditText endTime = field(form, "End time (epoch milliseconds) *");
        if (old != null) {
            title.setText(old.child("title").getValue(String.class));
            description.setText(old.child("description").getValue(String.class));
            category.setText(old.child("category").getValue(String.class));
            totalQuestions.setText(String.valueOf(value(old, "totalQuestions", 10)));
            startTime.setText(String.valueOf(value(old, "startTime", 0)));
            endTime.setText(String.valueOf(value(old, "endTime", 0)));
        }
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(old == null ? "CREATE TOURNAMENT" : "EDIT TOURNAMENT")
                .setView(form).setNegativeButton("CANCEL", null).setPositiveButton("SAVE", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try {
                String titleText = title.getText().toString().trim();
                String categoryText = category.getText().toString().trim();
                int questions = Integer.parseInt(totalQuestions.getText().toString().trim());
                long start = Long.parseLong(startTime.getText().toString().trim());
                long end = Long.parseLong(endTime.getText().toString().trim());
                if (titleText.isEmpty() || categoryText.isEmpty() || questions <= 0 || end <= start) throw new IllegalArgumentException();
                Map<String, Object> data = new HashMap<>();
                data.put("title", titleText); data.put("description", description.getText().toString().trim());
                data.put("category", categoryText); data.put("totalQuestions", questions);
                data.put("startTime", start); data.put("endTime", end); data.put("status", status(start, end));
                data.put("published", old != null && Boolean.TRUE.equals(old.child("published").getValue(Boolean.class)));
                data.put("createdBy", auth.getUid()); data.put("createdAt", ServerValue.TIMESTAMP);
                db.child("tournaments").child(old == null ? db.child("tournaments").push().getKey() : old.getKey()).updateChildren(data)
                        .addOnSuccessListener(done -> { toast("Tournament saved."); dialog.dismiss(); })
                        .addOnFailureListener(error -> toast("Could not save tournament."));
            } catch (RuntimeException error) { toast("Use valid values. End time must be after start time."); }
        }));
        dialog.show();
    }

    private int value(DataSnapshot snapshot, String child, int fallback) {
        Long value = snapshot.child(child).getValue(Long.class);
        return value == null ? fallback : value.intValue();
    }

    private String status(long start, long end) {
        long now = System.currentTimeMillis();
        return now < start ? "UPCOMING" : now < end ? "ONGOING" : "ENDED";
    }

    private void seedInitialTournaments() {
        if (seeding) return;
        seeding = true;
        seedButton.setEnabled(false);
        seedButton.setText("SEEDING...");
        List<QuizBank.StarterCategory> categories = QuizBank.categories();
        db.child("tournaments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> updates = new HashMap<>();
                long now = System.currentTimeMillis();
                for (int index = 1; index <= 100; index++) {
                    QuizBank.StarterCategory category = categories.get((index - 1) % categories.size());
                    String id = String.format(Locale.US, "championship-%03d", index);
                    updates.put(id, tournament(id, "Quiz Championship #" + String.format(Locale.US, "%03d", index), category.title, 10, now - 86400000L, now + 30L * 86400000L, true));
                }
                for (int index = 1; index <= 20; index++) {
                    QuizBank.StarterCategory category = categories.get((index - 1) % categories.size());
                    String id = String.format(Locale.US, "challenge-upcoming-%03d", index);
                    updates.put(id, tournament(id, "Quiz Challenge #" + String.format(Locale.US, "%03d", index), category.title, 10, now + index * 86400000L, now + (index + 2L) * 86400000L, true));
                }
                db.child("tournaments").updateChildren(updates).addOnSuccessListener(done -> {
                    verifySeedResult();
                }).addOnFailureListener(error -> finishSeedWithError(error.getMessage()));
            }
            @Override public void onCancelled(DatabaseError error) { finishSeedWithError(error.getMessage()); }
        });
    }

    private void finishSeedWithError(String message) {
        seeding = false;
        seedButton.setEnabled(true);
        seedButton.setText("SEED 120 TOURNAMENTS");
        toast("Seeding failed: " + (message == null ? "Firebase permission denied." : message));
    }

    private void verifySeedResult() {
        db.child("tournaments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                long now = System.currentTimeMillis();
                int ongoing = 0;
                int upcoming = 0;
                Set<String> ids = new HashSet<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    String id = item.getKey();
                    if (id == null || !ids.add(id)) continue;
                    Boolean published = item.child("published").getValue(Boolean.class);
                    Long start = item.child("startTime").getValue(Long.class);
                    Long end = item.child("endTime").getValue(Long.class);
                    if (!Boolean.TRUE.equals(published) || start == null || end == null) continue;
                    if (start <= now && end > now) ongoing++;
                    else if (start > now) upcoming++;
                }
                seeding = false;
                seedButton.setEnabled(true);
                seedButton.setText("SEED 120 TOURNAMENTS");
                toast(ongoing + " ongoing + " + upcoming + " upcoming tournaments ready.");
            }
            @Override public void onCancelled(DatabaseError error) { finishSeedWithError(error.getMessage()); }
        });
    }

    private Map<String, Object> tournament(String id, String title, String category, int questions, long start, long end, boolean published) {
        Map<String, Object> data = new HashMap<>();
        data.put("tournamentId", id); data.put("title", title); data.put("description", "Free quiz competition for points and rankings.");
        data.put("category", category); data.put("totalQuestions", questions); data.put("startTime", start); data.put("endTime", end);
        data.put("status", status(start, end)); data.put("published", published); data.put("entry_points", 0); data.put("createdBy", auth.getUid()); data.put("createdAt", ServerValue.TIMESTAMP);
        return data;
    }

    private class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.Holder> {
        @Override public Holder onCreateViewHolder(ViewGroup parent, int type) {
            TextView row = new TextView(AdminTournamentsActivity.this);
            row.setTextColor(0xffffffff); row.setTextSize(15); row.setPadding(18, 18, 18, 18); row.setBackgroundResource(R.drawable.bg_card);
            return new Holder(row);
        }
        @Override public void onBindViewHolder(Holder holder, int position) {
            DataSnapshot item = tournaments.get(position);
            boolean published = Boolean.TRUE.equals(item.child("published").getValue(Boolean.class));
            holder.row.setText(item.child("title").getValue(String.class) + "\n" + item.child("category").getValue(String.class) + " | " + item.child("status").getValue(String.class) + " | " + (published ? "PUBLISHED" : "UNPUBLISHED"));
            holder.row.setOnClickListener(v -> new AlertDialog.Builder(AdminTournamentsActivity.this).setItems(
                    new String[]{"EDIT", published ? "UNPUBLISH" : "PUBLISH", "DELETE"}, (dialog, choice) -> {
                        if (choice == 0) editTournament(item);
                        else if (choice == 1) db.child("tournaments").child(item.getKey()).child("published").setValue(!published);
                        else new AlertDialog.Builder(AdminTournamentsActivity.this).setTitle("DELETE TOURNAMENT?").setMessage("This cannot be undone.").setNegativeButton("CANCEL", null).setPositiveButton("DELETE", (d, w) -> db.child("tournaments").child(item.getKey()).removeValue()).show();
                    }).show());
        }
        @Override public int getItemCount() { return tournaments.size(); }
        class Holder extends RecyclerView.ViewHolder { TextView row; Holder(TextView view) { super(view); row = view; } }
    }
}
