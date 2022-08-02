package com.isaaco.campusdrive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.isaaco.campusdrive.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CampWeb extends AppCompatActivity {

    WebView campWebView;
    String webUrl;
    FloatingActionButton homeFab;
    ProgressDialog pD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camp_web);

        pD = new ProgressDialog(this);

        Window window = CampWeb.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(CampWeb.this, R.color.teal_700));

        homeFab = findViewById(R.id.homeFab);
        campWebView = findViewById(R.id.campWebView);
        webUrl = "https://cd-beta.vercel.app/";

        homeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CampWeb.this, MainActivity.class));
            }
        });

        campWebView.getSettings().setJavaScriptEnabled(true);
        pD.setTitle("Campus Drive Online");
        pD.setIcon(R.drawable.logo);
        pD.setMessage("Opening....");
        campWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pD.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pD.dismiss();
            }
        });

        campWebView.loadUrl(webUrl);


    }
}
