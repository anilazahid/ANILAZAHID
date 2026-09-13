package com.anilazahid.quiz;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminQuestionsActivity extends AdminBaseActivity {
    private final List<DataSnapshot> categories = new ArrayList<>();
    private final List<DataSnapshot> questions = new ArrayList<>();
    private Spinner categorySpinner;
    private QuestionAdapter adapter;
    private String selectedCategory;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_admin_list);
        ((TextView) findViewById(R.id.adminListTitle)).setText("QUESTION MANAGEMENT");
        ((android.widget.Button) findViewById(R.id.adminListAdd)).setText("ADD QUESTION");
        findViewById(R.id.adminListAdd).setOnClickListener(view -> editQuestion(null));
        findViewById(R.id.adminListSearch).setVisibility(View.GONE);
        findViewById(R.id.adminListBack).setOnClickListener(view -> finish());
        categorySpinner = new Spinner(this);
        ((LinearLayout) findViewById(R.id.adminListTitle).getParent()).addView(categorySpinner, 0,
                new LinearLayout.LayoutParams(0, 48, 1));
        RecyclerView list = findViewById(R.id.adminRecycler);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionAdapter();
        list.setAdapter(adapter);
        requireAdmin(this::loadCategories);
    }

    private void loadCategories() {
        db.child("quick_categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                categories.clear();
                List<String> labels = new ArrayList<>();
                for (DataSnapshot category : snapshot.getChildren()) {
                    categories.add(category);
                    labels.add(category.child("title").getValue(String.class) + " (" + category.child("questions").getChildrenCount() + ")");
                }
                categorySpinner.setAdapter(new ArrayAdapter<>(AdminQuestionsActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, labels));
                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override public void onNothingSelected(AdapterView<?> parent) { selectedCategory = null; questions.clear(); adapter.notifyDataSetChanged(); }
                    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position < categories.size()) {
                            selectedCategory = categories.get(position).getKey();
                            loadQuestions();
                        }
                    }
                });
            }
            @Override public void onCancelled(DatabaseError error) { toast("Could not load categories."); }
        });
    }

    private void loadQuestions() {
        questions.clear();
        adapter.notifyDataSetChanged();
        if (selectedCategory == null) return;
        db.child("quick_categories").child(selectedCategory).child("questions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot question : snapshot.getChildren()) questions.add(question);
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(DatabaseError error) { toast("Could not load questions."); }
                });
    }

    private EditText field(LinearLayout container, String hint) {
        EditText field = new EditText(this);
        field.setHint(hint);
        container.addView(field);
        return field;
    }

    private void editQuestion(DataSnapshot old) {
        if (selectedCategory == null) { toast("Select a category first."); return; }
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(24, 0, 24, 0);
        EditText question = field(form, "Question text *");
        EditText optionA = field(form, "Option A *");
        EditText optionB = field(form, "Option B *");
        EditText optionC = field(form, "Option C *");
        EditText optionD = field(form, "Option D *");
        Spinner answer = new Spinner(this);
        String[] choices = {"Select correct answer *", "Correct answer: A", "Correct answer: B", "Correct answer: C", "Correct answer: D"};
        answer.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, choices));
        form.addView(answer);
        EditText explanation = field(form, "Explanation (optional)");
        EditText difficulty = field(form, "Difficulty (optional)");
        if (old != null) {
            question.setText(old.child("q").getValue(String.class));
            optionA.setText(old.child("opt1").getValue(String.class));
            optionB.setText(old.child("opt2").getValue(String.class));
            optionC.setText(old.child("opt3").getValue(String.class));
            optionD.setText(old.child("opt4").getValue(String.class));
            explanation.setText(old.child("explanation").getValue(String.class));
            difficulty.setText(old.child("difficulty").getValue(String.class));
            Long index = old.child("ansIdx").getValue(Long.class);
            answer.setSelection(index == null ? 0 : Math.min(4, index.intValue() + 1));
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(old == null ? "ADD QUESTION" : "EDIT QUESTION")
                .setView(form)
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("SAVE", null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String[] values = {question.getText().toString().trim(), optionA.getText().toString().trim(), optionB.getText().toString().trim(), optionC.getText().toString().trim(), optionD.getText().toString().trim()};
            for (String value : values) {
                if (value.isEmpty()) { toast("Question and all four options are required."); return; }
            }
            int answerIndex = answer.getSelectedItemPosition() - 1;
            if (answerIndex < 0 || answerIndex > 3) { toast("Select the correct answer A, B, C, or D."); return; }
            DatabaseReference reference = old == null
                    ? db.child("quick_categories").child(selectedCategory).child("questions").push()
                    : db.child("quick_categories").child(selectedCategory).child("questions").child(old.getKey());
            Map<String, Object> data = new HashMap<>();
            data.put("q", values[0]); data.put("opt1", values[1]); data.put("opt2", values[2]); data.put("opt3", values[3]); data.put("opt4", values[4]);
            data.put("ansIdx", answerIndex); data.put("correctAnswer", String.valueOf((char) ('A' + answerIndex))); data.put("points", 10);
            data.put("explanation", explanation.getText().toString().trim()); data.put("difficulty", difficulty.getText().toString().trim());
            reference.updateChildren(data).addOnCompleteListener(task -> { if (task.isSuccessful()) { toast("Question saved."); dialog.dismiss(); loadQuestions(); } else toast("Could not save question."); });
        }));
        dialog.show();
    }

    private void confirmDelete(DataSnapshot question) {
        new AlertDialog.Builder(this)
                .setTitle("DELETE QUESTION?")
                .setMessage("This question will be permanently deleted.")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DELETE", (dialog, which) -> db.child("quick_categories").child(selectedCategory).child("questions").child(question.getKey()).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) { toast("Question deleted."); loadQuestions(); }
                    else toast("Could not delete question.");
                }))
                .show();
    }

    private class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.Holder> {
        @Override public Holder onCreateViewHolder(ViewGroup parent, int type) {
            TextView row = new TextView(AdminQuestionsActivity.this);
            row.setTextColor(0xffffffff); row.setTextSize(15); row.setPadding(18, 18, 18, 18); row.setBackgroundResource(R.drawable.bg_card);
            return new Holder(row);
        }
        @Override public void onBindViewHolder(Holder holder, int position) {
            DataSnapshot question = questions.get(position);
            String correct = question.child("correctAnswer").getValue(String.class);
            if (correct == null) { Long index = question.child("ansIdx").getValue(Long.class); correct = index == null ? "A" : String.valueOf((char) ('A' + index)); }
            holder.row.setText((position + 1) + ". " + question.child("q").getValue(String.class) + "\nCorrect answer: " + correct);
            holder.row.setOnClickListener(view -> new AlertDialog.Builder(AdminQuestionsActivity.this)
                    .setItems(new String[]{"EDIT QUESTION", "DELETE QUESTION"}, (dialog, choice) -> { if (choice == 0) editQuestion(question); else confirmDelete(question); }).show());
        }
        @Override public int getItemCount() { return questions.size(); }
        class Holder extends RecyclerView.ViewHolder { TextView row; Holder(TextView view) { super(view); row = view; } }
    }
}
