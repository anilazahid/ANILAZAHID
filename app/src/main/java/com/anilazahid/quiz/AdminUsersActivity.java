package com.anilazahid.quiz;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.*;

public class AdminUsersActivity extends AdminBaseActivity {
    private final List<DataSnapshot> all = new ArrayList<>(), shown = new ArrayList<>();
    private UserAdapter adapter;
    @Override protected void onCreate(Bundle state) { super.onCreate(state); setContentView(R.layout.activity_admin_list); ((TextView)findViewById(R.id.adminListTitle)).setText("USERS"); findViewById(R.id.adminListAdd).setVisibility(android.view.View.GONE); findViewById(R.id.adminListBack).setOnClickListener(v -> finish()); RecyclerView r=findViewById(R.id.adminRecycler); r.setLayoutManager(new LinearLayoutManager(this)); adapter=new UserAdapter(); r.setAdapter(adapter); findViewById(R.id.adminListSearch).setOnClickListener(v -> {}); ((android.widget.EditText)findViewById(R.id.adminListSearch)).addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int d){} public void onTextChanged(CharSequence s,int a,int b,int c){filter(s.toString());} public void afterTextChanged(Editable e){}}); requireAdmin(this::load); }
    private void load(){ db.child("users").addValueEventListener(new ValueEventListener(){public void onDataChange(DataSnapshot s){all.clear(); for(DataSnapshot x:s.getChildren())all.add(x); filter("");} public void onCancelled(DatabaseError e){toast("Could not load users.");}}); }
    private void filter(String q){shown.clear(); for(DataSnapshot s:all){String text=(s.child("username").getValue(String.class)+" "+s.child("email").getValue(String.class)+" "+s.getKey()).toLowerCase(); if(text.contains(q.toLowerCase()))shown.add(s);} if(adapter!=null)adapter.notifyDataSetChanged();}
        private class UserAdapter extends RecyclerView.Adapter<UserAdapter.H>{public H onCreateViewHolder(ViewGroup p,int t){TextView v=new TextView(AdminUsersActivity.this);v.setTextColor(getColor(R.color.text_primary));v.setTextSize(15);v.setPadding(18,18,18,18);v.setBackgroundResource(R.drawable.bg_card);return new H(v);} public void onBindViewHolder(H h,int i){DataSnapshot s=shown.get(i);h.v.setText((s.child("username").getValue(String.class)==null?"Player":s.child("username").getValue(String.class))+"\n"+s.child("email").getValue(String.class)+"\nUID: "+s.getKey()+"   Points: "+s.child("points").getValue());}public int getItemCount(){return shown.size();}class H extends RecyclerView.ViewHolder{TextView v;H(TextView x){super(x);v=x;}}}
}