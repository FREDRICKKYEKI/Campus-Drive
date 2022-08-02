package com.isaaco.campusdrive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.isaaco.campusdrive.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavedDocsActivity extends AppCompatActivity {

    private SavedDocAdapter adapter;
    private List<File> pdfList;
    RecyclerView recyclerView;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_docs);
        toolbar = findViewById(R.id.savedDocToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SavedDocsActivity.this, MainActivity.class));
            }
        });

    displayFiles();
    }



    private ArrayList<File> findPdf(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for(File singleFile : files){
            if(singleFile.isDirectory()&&!singleFile.isHidden()){
                arrayList.addAll(findPdf((singleFile)));
            }else {
                if (singleFile.getName().endsWith(".pdf")){
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }
    private void displayFiles() {
        recyclerView = findViewById(R.id.savedDocRecycView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        pdfList = new ArrayList<>();
        pdfList.addAll(findPdf(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)));
        adapter = new SavedDocAdapter(this, pdfList);
        recyclerView.setAdapter(adapter);
    }
}