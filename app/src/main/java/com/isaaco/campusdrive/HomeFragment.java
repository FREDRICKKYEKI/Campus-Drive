package com.isaaco.campusdrive;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private static final String TAG = "regCode";
    private static final String TAG2 = "internetCheck";


    RecyclerView recyclerView;
    FolderAdapter adapter;
    FirebaseAuth mAuth;
    MaterialToolbar toolbarHome;
    MaterialToolbar toolbarHome2;
    MaterialButton shareBtn;
    MaterialButton changeBtn;
    EditText etFolderCode;
    LinearLayout info;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;

    TextView username;
    TextView Email;
    CircleImageView profPic;
    FirebaseStorage storage;
    Query query;
    FirestoreRecyclerOptions<FirestoreModel> options;
    LinearProgressIndicator progress;
    SharedPreferences prefs, prefs2;
    AdView mAdView;
    String code;
    FloatingActionButton messageFab;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View v = inflater.inflate(R.layout.fragment_home, container,false);
        mAuth = FirebaseAuth.getInstance();

        //get class code from shared prefs
        prefs = this.requireActivity().getSharedPreferences("REG_CODE", Context.MODE_PRIVATE);
        prefs2 = this.requireActivity().getSharedPreferences("DATES", Context.MODE_PRIVATE);

        code = prefs.getString("codeKey","codeValue");

//        if(prefs2.contains("endDate")){}

        info = v.findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), AboutActivity.class));
            }
        });
        toolbarHome = v.findViewById(R.id.toolbarHome);
        toolbarHome2 = v.findViewById(R.id.toolbarHome2);
        toolbarHome2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolder();
            }
        });


        progress = v.findViewById(R.id.progressHorizontal);
        navigationView =v.findViewById(R.id.navview);
        drawerLayout = v.findViewById(R.id.drawer);
        username = navigationView.getHeaderView(0).findViewById(R.id.nav_username);
        Email = navigationView.getHeaderView(0).findViewById(R.id.nav_email);
        profPic = navigationView.getHeaderView(0).findViewById(R.id.dnav_prof);
        messageFab = v.findViewById(R.id.fab_message);

        if(CheckNetwork.isInternetAvailable(requireContext())){
            Log.d(TAG2,"internet connection");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setMessage(getResources().getString(R.string.internet_check));
            builder.setTitle("No internet");
            builder.setIcon(R.drawable.icon_no_internet_connection);
            builder.setCancelable(true);
            AlertDialog alert = builder.create();
            alert.show();
        }

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                adapter.stopListening();
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                displayNotes();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                displayNotes();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        storage = FirebaseStorage.getInstance();

        recyclerView = v.findViewById(R.id.recycler_view);
        displayNotes();
        MobileAds.initialize(this.requireActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

//        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
        actionBarDrawerToggle = new ActionBarDrawerToggle(requireActivity() , drawerLayout,toolbarHome,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        setProfileDetails();



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.nav_web:
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle("Campus Drive");
                    builder.setIcon(R.drawable.logo);
                    builder.setMessage("Note: This feature is for Admins (class representatives, lecturers etc.) only! " +
                            "Do you wish to Proceed?");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(requireActivity(),CampWeb.class);
                           startActivity(intent);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    //close drawer
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.nav_profile:
                    startActivity(new Intent(requireActivity(), AccountActivity.class));
                   // Toast.makeText(requireActivity(),"Profile",Toast.LENGTH_SHORT).show();
                    //close drawer
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.nav_offline:
                    startActivity(new Intent(requireActivity(), SavedDocsActivity.class));
                    //Toast.makeText(requireActivity(),"",Toast.LENGTH_SHORT).show();
                    //close drawer
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.nav_share:
                    Toast.makeText(requireActivity(),"Share",Toast.LENGTH_SHORT).show();
                    //close drawer
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.nav_about:
                    startActivity(new Intent(requireActivity(), AboutActivity.class));
                    //Toast.makeText(requireActivity(),"About",Toast.LENGTH_SHORT).show();
                    //close drawer
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.nav_logout:
                    mAuth.signOut();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    Toast.makeText(requireActivity(),"Logging Out",Toast.LENGTH_SHORT).show();
                    //close drawer
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
            }
            return true;
        }
    });

        messageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), ClassmatesActivity.class));
            }
        });

        return v;
    }

    private void openFolder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View customLayout = LayoutInflater.from(requireActivity()).inflate(R.layout.folder_code, null);
        builder.setView(customLayout);
        shareBtn = customLayout.findViewById(R.id.btnCopy);
        changeBtn = customLayout.findViewById(R.id.btnOpenFolder);
        etFolderCode = customLayout.findViewById(R.id.folderCode);
        AlertDialog alert = builder.create();
        etFolderCode.setText(code);
        etFolderCode.requestFocus();

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("folder_code", code);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireActivity(), "Copied...", Toast.LENGTH_SHORT).show();
                alert.dismiss();

            }
        });
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foldercode = Objects.requireNonNull(etFolderCode.getText()).toString();
                FirebaseFirestore profRef = FirebaseFirestore.getInstance();

                if (TextUtils.isEmpty(foldercode)){
                    etFolderCode.requestFocus();
                    etFolderCode.setError("Cannot be Empty");
                } else{
                    SharedPreferences sharedPref = requireActivity().getSharedPreferences("REG_CODE", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("codeKey", foldercode);
                    profRef.collection("user_profiles").document(Objects.requireNonNull(mAuth.getCurrentUser().getEmail())).update("folderCode",foldercode);
                    editor.apply();
                    alert.dismiss();
                    startActivity(new Intent(requireContext(), MainActivity.class));
                }
            }
        });
        alert.show();
     }

    //set drawer details
    private void setProfileDetails() {

        if(mAuth.getCurrentUser()==null){
            startActivity(new Intent(getActivity(),RegisterActivity.class));
            requireActivity().finish();
        }

        FirebaseFirestore profRef = FirebaseFirestore.getInstance();
        DocumentReference docRef= profRef.collection("user_profiles").document(mAuth.getCurrentUser().getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    DocumentSnapshot document = task.getResult();
                String Username = document.getString("name");
                String email = mAuth.getCurrentUser().getEmail();
                username.setText(Username);
                Email.setText(email);
                } catch (Exception e){
                    Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireActivity(),"Update Profile",Toast.LENGTH_SHORT).show();
            }
        });

        StorageReference storageRef = storage.getReference();
        StorageReference ref = storageRef.child("userProfiles/" +"/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "." + "jpg");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                Picasso.get().load(url).into(profPic);
            }

        });
    }


    //display notes list
    public void displayNotes() {
        toolbarHome2.setSubtitle("Folder Code: " + code);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        query = FirebaseFirestore.getInstance().collection("folders").orderBy("createdAt").limit(50).whereIn("parentId", Collections.singletonList(code));
        options = new FirestoreRecyclerOptions.Builder<FirestoreModel>().setQuery(query, FirestoreModel.class).build();
        adapter = new FolderAdapter(options,this);
        progress.setVisibility(View.VISIBLE);
        adapter.startListening();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                recyclerView.setAdapter(adapter);
                progress.setVisibility(View.GONE);
            }
        }, 500);   //5 seconds

    }



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();

    }
}