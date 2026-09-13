package com.anilazahid.quiz;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.*;
import androidx.recyclerview.widget.*;
import com.google.firebase.database.*;
import java.util.*;

public class AdminResultsActivity extends AdminBaseActivity {
    private final List<DataSnapshot> all=new ArrayList<>(), shown=new ArrayList<>(); private ResultAdapter adapter;
    @Override protected void onCreate(Bundle s){super.onCreate(s);setContentView(R.layout.activity_admin_list);((TextView)findViewById(R.id.adminListTitle)).setText("QUIZ RESULTS");findViewById(R.id.adminListAdd).setVisibility(android.view.View.GONE);findViewById(R.id.adminListBack).setOnClickListener(v->finish());RecyclerView r=findViewById(R.id.adminRecycler);r.setLayoutManager(new LinearLayoutManager(this));adapter=new ResultAdapter();r.setAdapter(adapter);EditText search=findViewById(R.id.adminListSearch);search.setHint("Search user or category");search.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int b,int c){}public void onTextChanged(CharSequence s,int a,int b,int c){filter(s.toString());}public void afterTextChanged(android.text.Editable e){}});requireAdmin(this::load);}
    private void load(){db.child("quiz_results").addValueEventListener(new ValueEventListener(){public void onDataChange(DataSnapshot s){all.clear();for(DataSnapshot x:s.getChildren())all.add(x);filter("");}public void onCancelled(DatabaseError e){toast("Could not load results.");}});}
    private void filter(String query){shown.clear();for(DataSnapshot s:all){String text=s.getValue().toString().toLowerCase();if(text.contains(query.toLowerCase()))shown.add(s);}if(adapter!=null)adapter.notifyDataSetChanged();}
    private class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.H>{public H onCreateViewHolder(ViewGroup p,int t){TextView v=new TextView(AdminResultsActivity.this);v.setTextColor(0xffffffff);v.setTextSize(15);v.setPadding(18,18,18,18);v.setBackgroundResource(R.drawable.bg_card);return new H(v);}public void onBindViewHolder(H h,int i){DataSnapshot s=shown.get(i);h.v.setText("User: "+s.child("userName").getValue(String.class)+"\nCategory: "+s.child("category").getValue(String.class)+"\nScore: "+s.child("score").getValue()+" | Correct: "+s.child("correctAnswers").getValue()+"/"+s.child("totalQuestions").getValue()+"\nDate: "+s.child("timestamp").getValue());}public int getItemCount(){return shown.size();}class H extends RecyclerView.ViewHolder{TextView v;H(TextView x){super(x);v=x;}}}
}