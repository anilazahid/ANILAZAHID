package com.anilazahid.quiz;

import android.content.Intent;
import android.os.Bundle;

public class AdminDashboardActivity extends AdminBaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_admin_dashboard); requireAdmin(this::bindActions);
    }
    private void bindActions() {
        count("users", R.id.adminUsersCount);
        ((android.widget.TextView) findViewById(R.id.adminCategoriesCount)).setText("15");
        countQuestions();
        countAttempts();
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
        ((android.widget.TextView) findViewById(R.id.adminQuestionsCount)).setText("300");
    }
    private void countAttempts() {
        final long[] counts = {0, 0};
        com.google.firebase.database.ValueEventListener listener = new com.google.firebase.database.ValueEventListener() {
            @Override public void onDataChange(com.google.firebase.database.DataSnapshot s) {
                if ("quiz_results".equals(s.getRef().getKey())) counts[0] = s.getChildrenCount(); else counts[1] = s.getChildrenCount();
                ((android.widget.TextView) findViewById(R.id.adminResultsCount)).setText(String.valueOf(counts[0] + counts[1]));
            }
            @Override public void onCancelled(com.google.firebase.database.DatabaseError e) { ((android.widget.TextView) findViewById(R.id.adminResultsCount)).setText("ERROR"); }
        };
        db.child("quiz_results").addListenerForSingleValueEvent(listener);
        db.child("tournament_attempts").addListenerForSingleValueEvent(listener);
    }
    private void open(Class<?> screen) { startActivity(new Intent(this, screen)); }
}