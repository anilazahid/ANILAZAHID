package com.anilazahid.quiz;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AdminBaseActivity {
    private android.widget.EditText email, password;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_admin_login);
        email = findViewById(R.id.adminEmail); password = findViewById(R.id.adminPassword);
        findViewById(R.id.adminLoginButton).setOnClickListener(v -> login());
        findViewById(R.id.adminBackButton).setOnClickListener(v -> finish());
        if (auth.getCurrentUser() != null) verifyCurrentUser(auth.getCurrentUser());
    }

    private void login() {
        String e = email.getText().toString().trim(), p = password.getText().toString();
        if (e.isEmpty() || p.isEmpty()) { toast("Enter email and password."); return; }
        auth.signInWithEmailAndPassword(e, p).addOnSuccessListener(result -> verifyCurrentUser(result.getUser()))
            .addOnFailureListener(error -> toast("Login failed: " + error.getMessage()));
    }

    private void verifyCurrentUser(FirebaseUser user) {
        if (user == null) return;
        db.child("admin_users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                if (Boolean.TRUE.equals(snapshot.getValue(Boolean.class))) {
                    startActivity(new Intent(AdminLoginActivity.this, AdminDashboardActivity.class)); finish();
                } else { auth.signOut(); toast("This Firebase account is not an admin."); }
            }
            @Override public void onCancelled(DatabaseError error) { auth.signOut(); toast("Authorization check failed."); }
        });
    }
}