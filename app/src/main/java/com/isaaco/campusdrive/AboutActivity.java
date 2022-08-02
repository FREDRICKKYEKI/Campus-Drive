package com.isaaco.campusdrive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.isaaco.campusdrive.R;
import com.google.android.material.appbar.MaterialToolbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutActivity extends AppCompatActivity {
    MaterialToolbar toolbar;
    CircleImageView instagram;
    CircleImageView facebook;
    CircleImageView gmail;
    CardView desc;
    CardView logoCard;
    LinearLayout socials;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        instagram= findViewById(R.id.instagram);
        desc = findViewById(R.id.aboutDesc);
        socials = findViewById(R.id.socials);
        logoCard = findViewById(R.id.logoCard);
        logo = findViewById(R.id.iv_logo);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.lefttoright);
        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fadein);
        Animation animation4 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink_anim);

        Handler handler3 = new Handler();
        handler3.postDelayed(new Runnable() {
            public void run() {
                logoCard.setVisibility(View.VISIBLE);
                logoCard.startAnimation(animation3);
            }
        }, 200);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                socials.setVisibility(View.VISIBLE);
                socials.startAnimation(animation);
            }
        }, 300);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                desc.setVisibility(View.VISIBLE);
                desc.startAnimation(animation2);
            }
        }, 400);

        Handler handler4 = new Handler();
        handler4.postDelayed(new Runnable() {
            public void run() {
                logo.startAnimation(animation4);
            }
        }, 5000);





        facebook= findViewById(R.id.facebook);
        gmail= findViewById(R.id.gmail);
        toolbar = findViewById(R.id.toolbarAboutUs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, MainActivity.class));
            }
        });
        Window window = AboutActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(AboutActivity.this, R.color.black));

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("http://instagram.com/_u/campusdrive1");
                Intent i= new Intent(Intent.ACTION_VIEW,uri);
                i.setPackage("com.instagram.android");
                Toast.makeText(getApplicationContext(), "Opening Instagram...Please wait", Toast.LENGTH_SHORT).show();
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/campusdrive1")));
                }
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Opening Facebook...Please wait", Toast.LENGTH_SHORT).show();

                try {
                    getApplicationContext().getPackageManager()
                            .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100081627219551")));  //Trys to make intent with FB's URI
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=100081627219551"))); //catches and opens a url to the desired page
                }

            }
        });
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Opening GMail...Please wait", Toast.LENGTH_SHORT).show();

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "campusdrive142@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "");
                email.putExtra(Intent.EXTRA_TEXT, "");
                //need this to prompts email client only
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));

            }
        });


    }
}