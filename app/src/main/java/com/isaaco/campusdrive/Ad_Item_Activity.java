package com.isaaco.campusdrive;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.isaaco.campusdrive.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Ad_Item_Activity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 22;
    com.google.android.material.textfield.TextInputEditText itemName;
    com.google.android.material.textfield.TextInputEditText price;
    com.google.android.material.textfield.TextInputEditText phoneNumber;
    com.google.android.material.textfield.TextInputEditText description;
    com.google.android.material.button.MaterialButton addImage;
    com.google.android.material.button.MaterialButton postItem;
    com.google.android.material.appbar.MaterialToolbar toolbar;

    private Uri filePath;
    ImageView itemImage;
    FirebaseStorage storage;
    com.google.android.material.progressindicator.LinearProgressIndicator uploadProgress;
    FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_item);

        itemName = findViewById(R.id.et_itemName);
        price = findViewById(R.id.et_itemPrice);
        phoneNumber = findViewById(R.id.et_phoneNumber);
        description = findViewById(R.id.et_description);

        toolbar = findViewById(R.id.marketToolbar);
        addImage = findViewById(R.id.btn_addImage);
        postItem = findViewById(R.id.btn_post_item);
        itemImage = findViewById(R.id.iv_marketItem);
        storage = FirebaseStorage.getInstance();
        uploadProgress = findViewById(R.id.progressUpload);
        StorageReference storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        Window window = Ad_Item_Activity.this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Ad_Item_Activity.this, R.color.market));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ad_Item_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        if (itemImage.getDrawable()==null){itemImage.setVisibility(View.GONE);}

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        postItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String Itemname = Objects.requireNonNull(itemName.getText()).toString();
                String Price = Objects.requireNonNull(price.getText()).toString();
                String Phonenumber = Objects.requireNonNull(phoneNumber.getText()).toString();
                String Description = Objects.requireNonNull(description.getText()).toString();

                if(TextUtils.isEmpty(Itemname)){
                    itemName.setError("Name required");
                    itemName.requestFocus();
                } else if(TextUtils.isEmpty(Price)){
                    price.setError("Price required");
                    price.requestFocus();
                } else if(TextUtils.isEmpty(Phonenumber)){
                    phoneNumber.setError("Phone Number required");
                    phoneNumber.requestFocus();
                } else if(TextUtils.isEmpty(Description)){
                    description.setError("Add brief description");
                    description.requestFocus();
                } else if (filePath == null){
                    addImage.setError("Press here to add image");
                    addImage.requestFocus();
                    //Toast.makeText(getApplicationContext(),"Please Add Item Image...", Toast.LENGTH_SHORT).show();
                } else{
                    uploadDetails();
                    postItem.setClickable(false);
                }

            }
        });

    }

    private void uploadDetails() {
        String Itemname = Objects.requireNonNull(itemName.getText()).toString();
        String Price = Objects.requireNonNull(price.getText()).toString();
        String Phonenumber = Objects.requireNonNull(phoneNumber.getText()).toString();
        String Description = Objects.requireNonNull(description.getText()).toString();
        String pattern = "dd/MM/yy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        String timestamp = simpleDateFormat.format(date);
        UUID uuid =UUID.randomUUID();

        if (filePath!=null){
            Toast.makeText(getApplicationContext(),"Uploading...Please Wait", Toast.LENGTH_SHORT).show();

            StorageReference storageRef = storage.getReference();
            StorageReference ref = storageRef.child("marketItems/" +"/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()+"/" +uuid.toString()+ "." + "jpg");
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadProgress.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Done!", Toast.LENGTH_SHORT).show();
                   taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           MarketItems items = new MarketItems(uuid.toString(),mAuth.getCurrentUser().getEmail(),Itemname,Price,Phonenumber,Description,timestamp,uri.toString());
                           FirebaseFirestore profRef = FirebaseFirestore.getInstance();
                           profRef.collection("market_items").document(uuid.toString()).set(items);
                       }
                   });
                }

            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    uploadProgress.setVisibility(View.VISIBLE);
                    uploadProgress.setProgress((int) progress);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Failed to upload...", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Code for showing progressDialog while uploading
//        ProgressDialog progressDialog = new ProgressDialog(requireActivity());
//        progressDialog.setTitle("Uploading...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//        if (filePath==null){progressDialog.dismiss();}

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();

            // Setting image on image view using Bitmap
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
//                itemImage.setImageBitmap(bitmap);
            itemImage.setVisibility(View.VISIBLE);
            Picasso.get().load(filePath).into(itemImage);
        }
    }

}