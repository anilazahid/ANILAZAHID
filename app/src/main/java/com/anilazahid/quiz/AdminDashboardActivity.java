package com.anilazahid.quiz;

import android.content.Intent;
import android.os.Bundle;

public class AdminDashboardActivity extends AdminBaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_admin_dashboard); requireAdmin(this::bindActions);
    }
    private void bindActions() {
        count("users", R.id.adminUsersCount);
        count("quick_categories", R.id.adminCategoriesCount);
        countQuestions();
        count("quiz_results", R.id.adminResultsCount);
        findViewById(R.id.adminUsersCard).setOnClickListener(v -> open(AdminUsersActivity.class));
        findViewById(R.id.adminCategoriesCard).setOnClickListener(v -> open(AdminCategoriesActivity.class));
        findViewById(R.id.adminQuestionsCard).setOnClickListener(v -> open(AdminQuestionsActivity.class));
        findViewById(R.id.adminResultsCard).setOnClickListener(v -> open(AdminResultsActivity.class));
        findViewById(R.id.adminTournamentsCard).setOnClickListener(v -> open(AdminTournamentsActivity.class));
        findViewById(R.id.adminSettingsCard).setOnClickListener(v -> open(AdminSettingsActivity.class));
        findViewById(R.id.adminLogout).setOnClickListener(v -> logoutAdmin());
    }
    private void count(String path, int viewId) {
        db.child(path).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override public void onDataChange(com.google.firebase.database.DataSnapshot s) { ((android.widget.TextView) findViewById(viewId)).setText(String.valueOf(s.getChildrenCount())); }
            @Override public void onCancelled(com.google.firebase.database.DatabaseError e) { }
        });
    }
    private void countQuestions() {
        db.child("quick_categories").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override public void onDataChange(com.google.firebase.database.DataSnapshot s) { long total = 0; for (com.google.firebase.database.DataSnapshot c : s.getChildren()) total += c.child("questions").getChildrenCount(); ((android.widget.TextView) findViewById(R.id.adminQuestionsCount)).setText(String.valueOf(total)); }
            @Override public void onCancelled(com.google.firebase.database.DatabaseError e) { }
        });
    }
    private void open(Class<?> screen) { startActivity(new Intent(this, screen)); }
}