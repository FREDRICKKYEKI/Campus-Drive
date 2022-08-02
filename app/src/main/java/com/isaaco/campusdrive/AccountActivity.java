package com.isaaco.campusdrive;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.isaaco.campusdrive.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.Objects;

public class AccountActivity extends AppCompatActivity   {

    TabLayout tabLayout;
    ViewPager viewPager;

    de.hdodenhof.circleimageview.CircleImageView profPic;
    SwipeRefreshLayout swipedl;
    FloatingActionButton fab;

    TextView tvname;
    TextView tvCourse;
    TextView tvEmail;
    TextView tvSchool;

    FirebaseAuth mAuth;
    private Uri filePath;
    FirebaseStorage storage;
    BottomSheetDialog bottomSheetDialog;
    String url;
    StorageReference ref;
    com.google.android.material.progressindicator.CircularProgressIndicator setDetailsProgress;
    com.google.android.material.appbar.MaterialToolbar toolbarAcc;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

         bottomSheetDialog = new BottomSheetDialog(AccountActivity.this);
         filePath = null;

        ArrayAdapter<CharSequence> ad =  ArrayAdapter.createFromResource(this, R.array.schools, android.R.layout.simple_spinner_item );

        //tabLayout
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tabLayout.addTab(tabLayout.newTab().setText("Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Market Items"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final TabAdapter tabadapter = new TabAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabadapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        relativeLayout = findViewById(R.id.rl_acc);
        setDetailsProgress = findViewById(R.id.progressAcc);
        swipedl = findViewById(R.id.swipeDL);
        profPic = findViewById(R.id.profile_pic);
        fab = findViewById(R.id.fab_update_profile);
        mAuth = FirebaseAuth.getInstance();
        tvSchool = findViewById(R.id.tvSchool);
        tvname = findViewById(R.id.tvName);
        tvCourse = findViewById(R.id.tvCourse);
        tvEmail = findViewById(R.id.tvEmail);
        storage = FirebaseStorage.getInstance();
        toolbarAcc = findViewById(R.id.toolbarAcc);
        StorageReference storageRef = storage.getReference();
        StorageReference ref = storageRef.child("userProfiles/" +"/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "." + "jpg");

        toolbarAcc.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.Logout) {
                    mAuth.signOut();
                    startActivity(new Intent(AccountActivity.this, LoginActivity.class));
                    Toast.makeText(AccountActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        toolbarAcc.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, MainActivity.class));
                finish();
            }
        });

        Window window2 = AccountActivity.this.getWindow();
        window2.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window2.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window2.setStatusBarColor(ContextCompat.getColor(AccountActivity.this, R.color.black));

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
               url = uri.toString();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountActivity.this,"Update your profile image",Toast.LENGTH_SHORT).show();

            }
        });
        setProfileDetails();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, ProfileActivity.class));
                finish();
             }
        });
        swipedl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setProfileDetails();
            }
        });
    }




    private void setProfileDetails() {
            FirebaseFirestore profRef = FirebaseFirestore.getInstance();
            DocumentReference docRef= profRef.collection("user_profiles").document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser().getEmail())));
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    try{
                        DocumentSnapshot document = task.getResult();
                        String Username = document.getString("name");
                        String Course = document.getString("course");
                        String School = document.getString("school");
                        String email = mAuth.getCurrentUser().getEmail();
                        tvname.setText(Username);
                        tvSchool.setText(School);
                        tvCourse.setText(Course);
                        tvEmail.setText(email);
                        toolbarAcc.setTitle(Username);
                    } catch (Exception e) {
                        Toast.makeText(AccountActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AccountActivity.this,"Update Profile",Toast.LENGTH_SHORT).show();
                }
            });
            StorageReference storageRef = storage.getReference();
            StorageReference ref = storageRef.child("userProfiles/" +"/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "." + "jpg");
            setDetailsProgress.setVisibility(View.VISIBLE);

            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String url = uri.toString();
                    Picasso.get().load(url).into(profPic);
                    setDetailsProgress.setVisibility(View.GONE);
                    swipedl.setRefreshing(false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    setDetailsProgress.setVisibility(View.GONE);
                    swipedl.setRefreshing(false);
                }
            });
    }

            @Override
            protected void onStart() {
                super.onStart();
                setProfileDetails();
            }
}