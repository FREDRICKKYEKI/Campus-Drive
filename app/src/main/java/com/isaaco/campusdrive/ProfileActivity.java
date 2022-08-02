package com.isaaco.campusdrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.isaaco.campusdrive.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity  {

    private static final int PICK_IMAGE_REQUEST = 22;
    EditText username;
    EditText etCourse;
    CircularProgressIndicator progressCircular;
    Spinner spinSchools;
    CircleImageView profPic;
    MaterialToolbar profileToolbar;
    MaterialButton updateBtn;
    TextView school;
    Uri filePath;
    String url;
    String exception;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    SharedPreferences prefs;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Window window2 = ProfileActivity.this.getWindow();
        window2.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window2.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window2.setStatusBarColor(ContextCompat.getColor(ProfileActivity.this, R.color.black));

        //get class code from shared prefs
        prefs = this.getSharedPreferences("REG_CODE", Context.MODE_PRIVATE);
        code = prefs.getString("codeKey","codeValue");


        progressCircular = findViewById(R.id.progressCircular);
        profileToolbar = findViewById(R.id.profileToolbar);
        username = findViewById(R.id.etName);
        etCourse = findViewById(R.id.etCourse);

        ArrayAdapter<CharSequence> ad =  ArrayAdapter.createFromResource(this, R.array.schools, android.R.layout.simple_spinner_item );
        spinSchools = findViewById(R.id.spinSchools);
        spinSchools.setAdapter(ad);

        profPic = findViewById(R.id.profile_image);
        updateBtn = findViewById(R.id.updatebtn);
        //school = findViewById(R.id.tvschool);
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        StorageReference storageRef = storage.getReference();
        StorageReference ref = storageRef.child("userProfiles/" +"/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "." + "jpg");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url = uri.toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exception = e.getMessage();
            }
        });

        profileToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, AccountActivity.class));
                finish();
            }
        });

        profPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore profRef = FirebaseFirestore.getInstance();
                String name = Objects.requireNonNull(username.getText()).toString();
                String course = Objects.requireNonNull(etCourse.getText()).toString();
                String school = spinSchools.getSelectedItem().toString();
                if (TextUtils.isEmpty(name)){
                    username.setError("Name cannot be empty");
                    username.requestFocus();
                }else if (TextUtils.isEmpty(course)){
                    etCourse.setError("Course cannot be empty");
                    etCourse.requestFocus();
                } else if (filePath == null){
                    if (url ==null){
                        url ="https://media.istockphoto.com/vectors/user-icon-flat-isolated-on-white-background-user-symbol-vector-vector-id1300845620?k=20&m=1300845620&s=612x612&w=0&h=f4XTZDAv7NPuZbG0habSpU0sNgECM0X7nbKzTUta3n8=";
                        CampDriveUsers users = new CampDriveUsers(mAuth.getUid(),name,course,school,mAuth.getCurrentUser().getEmail(),Objects.requireNonNull(url), code);
                        profRef.collection("user_profiles").document(Objects.requireNonNull(mAuth.getCurrentUser().getEmail())).set(users);
                        Toast.makeText(ProfileActivity.this, "Done!!! ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ProfileActivity.this, AccountActivity.class));
                        finish();
                    } else if (url!=null){
                        CampDriveUsers users = new CampDriveUsers(mAuth.getUid(),name,course,school,mAuth.getCurrentUser().getEmail(),Objects.requireNonNull(url), code);
                        profRef.collection("user_profiles").document(Objects.requireNonNull(mAuth.getCurrentUser().getEmail())).set(users);
                        Toast.makeText(ProfileActivity.this, "Done!!! ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ProfileActivity.this, AccountActivity.class));
                        finish();
                    }
                }
                else if (filePath!=null){
                    ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    CampDriveUsers users = new CampDriveUsers(mAuth.getUid(),name,course,school,mAuth.getCurrentUser().getEmail(),uri.toString(), code);
                                    FirebaseFirestore profRef = FirebaseFirestore.getInstance();
                                    profRef.collection("user_profiles").document(Objects.requireNonNull(mAuth.getCurrentUser().getEmail())).set(users);
                                    Toast.makeText(ProfileActivity.this, "Done!!! ", Toast.LENGTH_SHORT).show();
                                    progressCircular.setVisibility(View.GONE);
                                    startActivity(new Intent(ProfileActivity.this, AccountActivity.class));
                                    finish();
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressCircular.setProgress((int) progress);
                            Toast.makeText(ProfileActivity.this, "Uploading...wait " + (int)progress + "%", Toast.LENGTH_SHORT).show();
                            updateBtn.setClickable(false);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        setProfileDetails();
    }

    private void setProfileDetails() {
        FirebaseFirestore profRef = FirebaseFirestore.getInstance();
        DocumentReference docRef= profRef.collection("user_profiles").document(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    DocumentSnapshot document = task.getResult();
                    String Username = document.getString("name");
                    String course = document.getString("course");
//                    String School = document.getString("school");
                    String url = document.getString("url");
                    username.setText(Username);
                    etCourse.setText(course);
                    Picasso.get().load(url).into(profPic);
                } catch (Exception e){
                      Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this,"Update Profile",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(ProfileActivity.this.getContentResolver(), filePath);
                profPic.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


}