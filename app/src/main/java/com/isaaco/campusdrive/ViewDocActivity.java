package com.isaaco.campusdrive;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.isaaco.campusdrive.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

public class ViewDocActivity extends AppCompatActivity {
    private static final String TAG = "File path";
    private static final String TAG2 = "docName";
    WebView docView;
    String docName;
    String docUrl;
    ProgressDialog pD;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pD = new ProgressDialog(getApplicationContext());

        docName = getIntent().getStringExtra("docName");
        docUrl= getIntent().getStringExtra("docUrl");

        File tempFile = new File( getApplicationContext().getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ),docName );
        Log.d(TAG2, "onCreate: " +tempFile);

            setContentView(R.layout.activity_view_doc);
            openPDFThroughGoogleDrive();



    }

    private void openPDFThroughGoogleDrive() {
    docView= findViewById(R.id.viewDoc);
        docView.getSettings().setJavaScriptEnabled(true);

        ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle(docName);
        pd.setMessage("Opening....!!!");

        ProgressDialog pd2 = new ProgressDialog(this);
        pd2.setTitle(docName);
        pd.setMessage("Opening....!!!");
        pd2.show();
        pd.setCanceledOnTouchOutside(false);
        pd2.setCanceledOnTouchOutside(false);
        docView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pd.dismiss();
                pd2.dismiss();
            }
        });

        String url="";
        try {
            url= URLEncoder.encode(docUrl,"UTF-8");
        }catch (Exception ex)
        {}

        docView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);

    }





}