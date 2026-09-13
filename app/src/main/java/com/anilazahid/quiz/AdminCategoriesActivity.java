package com.anilazahid.quiz;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminCategoriesActivity extends AdminBaseActivity {
    private final List<DataSnapshot> categories = new ArrayList<>();
    private CategoryAdapter adapter;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_admin_list);
        TextView title = findViewById(R.id.adminListTitle);
        title.setText("CATEGORY MANAGEMENT");
        ((android.widget.Button) findViewById(R.id.adminListAdd)).setText("ADD CATEGORY");
        findViewById(R.id.adminListAdd).setOnClickListener(view -> editCategory(null));
        findViewById(R.id.adminListSearch).setVisibility(View.GONE);
        findViewById(R.id.adminListBack).setOnClickListener(view -> finish());
        RecyclerView list = findViewById(R.id.adminRecycler);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter();
        list.setAdapter(adapter);
        requireAdmin(this::loadCategories);
    }

    private void loadCategories() {
        db.child("quick_categories").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                categories.clear();
                for (DataSnapshot category : snapshot.getChildren()) categories.add(category);
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(DatabaseError error) { toast("Could not load categories."); }
        });
    }

    private void editCategory(DataSnapshot old) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(30, 10, 30, 0);
        EditText name = new EditText(this);
        name.setHint("Category name *");
        EditText icon = new EditText(this);
        icon.setHint("Icon (optional)");
        box.addView(name);
        box.addView(icon);
        if (old != null) {
            name.setText(old.child("title").getValue(String.class));
            icon.setText(old.child("icon").getValue(String.class));
        }
        new AlertDialog.Builder(this)
                .setTitle(old == null ? "ADD CATEGORY" : "EDIT CATEGORY")
                .setView(box)
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("SAVE", (dialog, which) -> {
                    String title = name.getText().toString().trim();
                    if (title.isEmpty()) { toast("Category name is required."); return; }
                    DatabaseReference reference = old == null
                            ? db.child("quick_categories").push()
                            : db.child("quick_categories").child(old.getKey());
                    reference.child("title").setValue(title);
                    reference.child("icon").setValue(icon.getText().toString().trim());
                }).show();
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Holder> {
        @Override public Holder onCreateViewHolder(android.view.ViewGroup parent, int type) {
            TextView row = new TextView(AdminCategoriesActivity.this);
            row.setTextColor(0xffffffff);
            row.setTextSize(16);
            row.setPadding(18, 18, 18, 18);
            row.setBackgroundResource(R.drawable.bg_card);
            return new Holder(row);
        }
        @Override public void onBindViewHolder(Holder holder, int position) {
            DataSnapshot category = categories.get(position);
            String icon = category.child("icon").getValue(String.class);
            String title = category.child("title").getValue(String.class);
            long count = category.child("questions").getChildrenCount();
            holder.row.setText((icon == null || icon.isEmpty() ? "🧩" : icon) + "  " + title + "\n" + count + " questions");
            holder.row.setOnClickListener(view -> new AlertDialog.Builder(AdminCategoriesActivity.this)
                    .setItems(new String[]{"EDIT CATEGORY", "DELETE CATEGORY"}, (dialog, choice) -> {
                        if (choice == 0) editCategory(category);
                        else confirmDelete(category);
                    }).show());
        }
        @Override public int getItemCount() { return categories.size(); }
        class Holder extends RecyclerView.ViewHolder { TextView row; Holder(TextView view) { super(view); row = view; } }
    }

    private void confirmDelete(DataSnapshot category) {
        String title = category.child("title").getValue(String.class);
        new AlertDialog.Builder(this)
                .setTitle("DELETE CATEGORY?")
                .setMessage("Delete " + title + " and its questions? This cannot be undone.")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DELETE", (dialog, which) -> db.child("quick_categories").child(category.getKey()).removeValue())
                .show();
    }
}
