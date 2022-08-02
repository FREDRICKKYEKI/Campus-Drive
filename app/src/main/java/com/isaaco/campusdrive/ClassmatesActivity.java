 package com.isaaco.campusdrive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collections;

 public class ClassmatesActivity extends AppCompatActivity {
    RecyclerView rv_classmates;
    SharedPreferences prefs;
    String folder_code;
    ClassmatesAdapter classmatesAdapter;
    MaterialToolbar cm_toolbar;



     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classmates);

        rv_classmates = findViewById(R.id.rv_classmates);
        cm_toolbar = findViewById(R.id.cm_toolbar);
         prefs =ClassmatesActivity.this.getSharedPreferences("REG_CODE", Context.MODE_PRIVATE);
         folder_code = prefs.getString("codeKey","codeValue");

        Window window = ClassmatesActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ClassmatesActivity.this, R.color.black));



        cm_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClassmatesActivity.this,MainActivity.class));
                finish();
            }
        });

        cm_toolbar.setSubtitle("Folder code: "+folder_code);
        displayClassmates();
    }

     private void displayClassmates() {


         rv_classmates.setLayoutManager(new LinearLayoutManager(ClassmatesActivity.this, LinearLayoutManager.VERTICAL, false));
         rv_classmates.setItemAnimator(new DefaultItemAnimator());

         Query query = FirebaseFirestore.getInstance().collection("user_profiles").whereIn("folderCode", Collections.singletonList(folder_code)).orderBy("name",Query.Direction.DESCENDING);
         FirestoreRecyclerOptions<FirestoreModel> options = new FirestoreRecyclerOptions.Builder<FirestoreModel>()
                 .setQuery(query, FirestoreModel.class).build();
         classmatesAdapter = new ClassmatesAdapter(options, this);
         rv_classmates.setAdapter(classmatesAdapter);
     }

     @Override
     public void onStart() {
         super.onStart();
         classmatesAdapter.startListening();
     }

     @Override
     public void onStop() {
         super.onStop();
         classmatesAdapter.stopListening();

     }
 }