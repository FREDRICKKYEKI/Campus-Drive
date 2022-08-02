package com.isaaco.campusdrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG2 = "internet";
    EditText etLoginEmail;
    EditText etLoginPassword;
    EditText etClassCode;

    TextView tvRegisterHere;
    Button btnLogin;
    de.hdodenhof.circleimageview.CircleImageView logo;
    LinearLayout layoutlogin;
    CardView card;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPass);
        etClassCode = findViewById(R.id.RegCode);
        card = findViewById(R.id.cardLogin);

        tvRegisterHere = findViewById(R.id.tvRegisterHere);
        btnLogin = findViewById(R.id.btnLogin);
        logo = findViewById(R.id.profile_image);
        layoutlogin = findViewById(R.id.loginActivity);

        mAuth = FirebaseAuth.getInstance();

        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.lefttoright);
        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                card.setVisibility(View.VISIBLE);
                card.startAnimation(animation2);
            }
        }, 0);

        btnLogin.setOnClickListener(view -> {
            loginUser();
        });
        tvRegisterHere.setOnClickListener(view ->{
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

        });

        Window window = LoginActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(LoginActivity.this, R.color.black));

        getWindow().setBackgroundDrawableResource(R.drawable.background_gradient);

        if(CheckNetwork.isInternetAvailable(this)){
            Log.d(TAG2,"internet connection");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.internet_check));
            builder.setTitle("No internet");
            builder.setIcon(R.drawable.icon_no_internet_connection);
            builder.setCancelable(true);
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    private void loginUser(){
        String email = Objects.requireNonNull(etLoginEmail.getText()).toString();
        String password = Objects.requireNonNull(etLoginPassword.getText()).toString();
        String code = Objects.requireNonNull(etClassCode.getText()).toString();
        if (TextUtils.isEmpty(email)){
            etLoginEmail.setError("Email cannot be empty");
            etLoginEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            etLoginPassword.setError("Password cannot be empty");
            etLoginPassword.requestFocus();
        }else if(TextUtils.isEmpty(code)){
            etClassCode.setError("Code cannot be empty");
            etClassCode.requestFocus();}
        else{
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        //class code

                        Toast.makeText(LoginActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }else{
                        Toast.makeText(LoginActivity.this, "Log in Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //save code
        SharedPreferences sharedPref = getSharedPreferences("REG_CODE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("codeKey", code);
        editor.apply();
    }

}