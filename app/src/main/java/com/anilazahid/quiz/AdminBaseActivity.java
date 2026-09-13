package com.anilazahid.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public abstract class AdminBaseActivity extends AppCompatActivity {
    protected FirebaseAuth auth;
    protected DatabaseReference db;

    @Override protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
    }

    protected void requireAdmin(Runnable authorized) {
        if (auth.getCurrentUser() == null) { goToLogin(); return; }
        db.child("admin_users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                Boolean isAdmin = snapshot.getValue(Boolean.class);
                if (Boolean.TRUE.equals(isAdmin)) authorized.run();
                else { toast("This account is not authorized for admin access."); auth.signOut(); goToLogin(); }
            }
            @Override public void onCancelled(DatabaseError error) { toast("Could not verify admin access."); goToLogin(); }
        });
    }

    protected void goToLogin() { startActivity(new Intent(this, AdminLoginActivity.class)); finish(); }
    protected void logoutAdmin() { auth.signOut(); goToLogin(); }
    protected void toast(String message) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); }
}