package com.anilazahid.quiz;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.recyclerview.widget.*;
import com.google.firebase.database.*;
import java.util.*;

public class AdminResultsActivity extends AdminBaseActivity {
    private final List<DataSnapshot> all=new ArrayList<>(), shown=new ArrayList<>(); private ResultAdapter adapter;
    private TextView empty;
    private boolean loading;
    private String loadError;
    @Override protected void onCreate(Bundle s){super.onCreate(s);setContentView(R.layout.activity_admin_list);((TextView)findViewById(R.id.adminListTitle)).setText("QUIZ ATTEMPTS");findViewById(R.id.adminListAdd).setVisibility(android.view.View.GONE);findViewById(R.id.adminListBack).setOnClickListener(v->finish());empty=findViewById(R.id.adminListEmpty);RecyclerView r=findViewById(R.id.adminRecycler);r.setLayoutManager(new LinearLayoutManager(this));adapter=new ResultAdapter();r.setAdapter(adapter);EditText search=findViewById(R.id.adminListSearch);search.setHint("Search user or category");search.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int b,int c){}public void onTextChanged(CharSequence s,int a,int b,int c){filter(s.toString());}public void afterTextChanged(android.text.Editable e){}});requireAdmin(this::load);}
    private void load(){all.clear();loading=true;loadError=null;if(empty!=null){empty.setText("Loading quiz attempts...");empty.setVisibility(View.VISIBLE);}final int[] pending={2};ValueEventListener listener=new ValueEventListener(){public void onDataChange(DataSnapshot s){for(DataSnapshot x:s.getChildren())all.add(x);if(--pending[0]==0){loading=false;filter("");}}public void onCancelled(DatabaseError e){loadError="Could not load attempts from "+safelyName(e)+": "+e.getMessage();pending[0]--;if(empty!=null){empty.setText(loadError);empty.setVisibility(View.VISIBLE);}toast(loadError);if(pending[0]==0)loading=false;}};db.child("quiz_results").addListenerForSingleValueEvent(listener);db.child("tournament_attempts").addListenerForSingleValueEvent(listener);}
    private String safelyName(DatabaseError error){return error==null||error.getDetails()==null?"Firebase":"Firebase";}
    private void filter(String query){shown.clear();if(loading){if(empty!=null){empty.setText("Loading quiz attempts...");empty.setVisibility(View.VISIBLE);}return;}if(loadError!=null){if(empty!=null){empty.setText(loadError);empty.setVisibility(View.VISIBLE);}return;}for(DataSnapshot s:all){String text=String.valueOf(s.getValue()).toLowerCase();if(text.contains(query.toLowerCase()))shown.add(s);}if(adapter!=null)adapter.notifyDataSetChanged();if(empty!=null){empty.setText("No quiz attempts yet.");empty.setVisibility(shown.isEmpty()?View.VISIBLE:View.GONE);}}
        private class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.H>{public H onCreateViewHolder(ViewGroup p,int t){TextView v=new TextView(AdminResultsActivity.this);v.setTextColor(getColor(R.color.text_primary));v.setTextSize(15);v.setPadding(18,18,18,18);v.setBackgroundResource(R.drawable.bg_card);return new H(v);}public void onBindViewHolder(H h,int i){DataSnapshot s=shown.get(i);Boolean published=s.child("published").getValue(Boolean.class);h.v.setText("User: "+s.child("userName").getValue(String.class)+"\nCategory: "+s.child("category").getValue(String.class)+"\nScore: "+s.child("score").getValue()+" | Correct: "+s.child("correctAnswers").getValue()+"/"+s.child("totalQuestions").getValue()+"\nPublished: "+Boolean.TRUE.equals(published));h.v.setOnClickListener(v->db.child(s.getRef().getParent().getKey()).child(s.getKey()).child("published").setValue(!Boolean.TRUE.equals(published)));}public int getItemCount(){return shown.size();}class H extends RecyclerView.ViewHolder{TextView v;H(TextView x){super(x);v=x;}}}
}