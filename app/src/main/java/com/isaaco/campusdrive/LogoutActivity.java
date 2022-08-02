package com.isaaco.campusdrive;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.isaaco.campusdrive.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogoutActivity extends AppCompatActivity {
    Button btnLogOut;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        mAuth = FirebaseAuth.getInstance();
        btnLogOut = findViewById(R.id.btnLogout);


        btnLogOut.setOnClickListener(view ->{
            mAuth.signOut();
            startActivity(new Intent(LogoutActivity.this, LoginActivity.class));
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(LogoutActivity.this, LoginActivity.class));
        }
    }
}