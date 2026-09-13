package com.anilazahid.quiz;

import android.os.Bundle;
import android.widget.*;
import com.google.firebase.database.*;
import java.util.*;

public class AdminSettingsActivity extends AdminBaseActivity {
    private EditText name,timer,message; private Switch maintenance;
    @Override protected void onCreate(Bundle s){super.onCreate(s);setContentView(R.layout.activity_admin_settings);name=findViewById(R.id.settingAppName);timer=findViewById(R.id.settingTimer);message=findViewById(R.id.settingMessage);maintenance=findViewById(R.id.settingMaintenance);findViewById(R.id.settingBack).setOnClickListener(v->finish());findViewById(R.id.settingSave).setOnClickListener(v->save());requireAdmin(this::load);}
    private void load(){db.child("admin_settings").addListenerForSingleValueEvent(new ValueEventListener(){public void onDataChange(DataSnapshot s){name.setText(s.child("appName").getValue(String.class));timer.setText(String.valueOf(s.child("defaultTimerSeconds").getValue()==null?15:s.child("defaultTimerSeconds").getValue()));message.setText(s.child("maintenanceMessage").getValue(String.class));Boolean m=s.child("maintenanceEnabled").getValue(Boolean.class);maintenance.setChecked(Boolean.TRUE.equals(m));}public void onCancelled(DatabaseError e){}});}
    private void save(){Map<String,Object> values=new HashMap<>();values.put("appName",name.getText().toString().trim());try{values.put("defaultTimerSeconds",Integer.parseInt(timer.getText().toString().trim()));}catch(Exception e){toast("Timer must be a number.");return;}values.put("maintenanceMessage",message.getText().toString().trim());values.put("maintenanceEnabled",maintenance.isChecked());db.child("admin_settings").updateChildren(values).addOnSuccessListener(v->toast("Settings saved."));}
}