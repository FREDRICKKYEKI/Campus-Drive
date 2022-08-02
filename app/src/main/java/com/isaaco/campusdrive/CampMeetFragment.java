package com.isaaco.campusdrive;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.isaaco.campusdrive.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class CampMeetFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 22;
    private static final String TAG = "market";
    de.hdodenhof.circleimageview.CircleImageView profpic;
    FirebaseAuth mAuth;

    LinearLayout linearLayoutTitle;
    LinearLayout linearLayoutItems;
    LinearProgressIndicator postProgress;

    CircularProgressIndicator postingProgress;

    FirebaseStorage storage;
    FirebaseFirestore profRef;
    DocumentReference docRef;

    ImageView iv_image_post;
    de.hdodenhof.circleimageview.CircleImageView btn_addAdvert;
    RecyclerView ad_recyclView;
    com.google.android.material.floatingactionbutton.FloatingActionButton post_btn;
    RecyclerView post_recyclView;
    StorageReference ref;
    de.hdodenhof.circleimageview.CircleImageView modalProfpic;
    String url;
    ImageView arrowDown;
    TextView tv_userName;
    TextView tv_school;
    com.google.android.material.textfield.TextInputEditText caption;
    private Uri filePath;

    CampPostsAdapter adapter;
    CampMarketAdapter adapter2;
    BottomSheetDialog bottomSheetDialog;
    MaterialToolbar modal_nav;
    LinearLayout btn_cancel;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_camp_meet, container,false);
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.modal_post_bottom_sheet,v.findViewById(R.id.modalBottomSheetContainer3));

        //Linear layouts
        linearLayoutTitle = v.findViewById(R.id.ll_campMarketTittle);
        linearLayoutItems = v.findViewById(R.id.ll_items);

        //progress
        postProgress = v.findViewById(R.id.progressCM);


        // Bottom sheet modal details
        tv_userName = bottomSheetView.findViewById(R.id.user_name);
        tv_school = bottomSheetView.findViewById(R.id.schoolModal);
        modalProfpic = bottomSheetView.findViewById(R.id.prof_picModal);
        caption = bottomSheetView.findViewById(R.id.post_caption);
        iv_image_post = bottomSheetView.findViewById(R.id.ivImage_post);
        postingProgress = bottomSheetView.findViewById(R.id.postingProgress);
        modal_nav = bottomSheetView.findViewById(R.id.modal_nav);
        btn_cancel = bottomSheetView.findViewById(R.id.btnCancel);

        // firebase details
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        arrowDown = v.findViewById(R.id.downArrow);
        StorageReference storageRef = storage.getReference();
        ref = storageRef.child("userProfiles/" +"/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "." + "jpg");
        arrowDown.setImageResource(R.drawable.ic_arrow_down);

        linearLayoutTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linearLayoutItems.getVisibility() == View.VISIBLE){
                    linearLayoutItems.setVisibility(View.GONE);
                    adapter2.stopListening();
                    arrowDown.setImageResource(R.drawable.ic_arrow_down);
                } else if(linearLayoutItems.getVisibility() == View.GONE){
                    linearLayoutItems.setVisibility(View.VISIBLE);
                    adapter2.startListening();
                    arrowDown.setImageResource(R.drawable.ic_arrow_up);
                }
            }
        });


        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url = uri.toString();
            } }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireActivity(),"Update your profile details", Toast.LENGTH_SHORT).show();
            }
        });

        profRef = FirebaseFirestore.getInstance();
        docRef = profRef.collection("user_profiles").document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser().getEmail())));

        // Campus meet details
        profpic = v.findViewById(R.id.dp);
        btn_addAdvert = v.findViewById(R.id.add_ad_item);
        ad_recyclView = v.findViewById(R.id.ad_recyc_view);
        post_btn = v.findViewById(R.id.btn_post);
        post_recyclView = v.findViewById(R.id.post_recyc_view);

        // Attach Profile pic
        getProfilePicture();

        // Add buy/sell item
        btn_addAdvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(),Ad_Item_Activity.class);
                startActivity(intent);
            }
        });

        // Open Modal to add new post
        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv_image_post.getDrawable()==null){iv_image_post.setVisibility(View.GONE);}

                Picasso.get().load(url).into(modalProfpic);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            String Username = document.getString("name");
                            String School = document.getString("school");
                            tv_userName.setText(Username);
                            tv_school.setText(School);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireActivity(),"Update your profile details", Toast.LENGTH_SHORT).show();
                    }
                });
                bottomSheetDialog = new BottomSheetDialog(requireActivity());
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                        }
                    });
                    bottomSheetView.findViewById(R.id.btn_add_image).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectImage();
                        }
                    });
                    bottomSheetView.findViewById(R.id.btn_postModal).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String Caption = Objects.requireNonNull(caption.getText()).toString();
                            if(TextUtils.isEmpty(Caption)){
                                caption.setError("Caption cannot be empty");
                                caption.requestFocus();
                            } else{
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot document = task.getResult();
                                        String Username = document.getString("name");
                                        String School = document.getString("school");
                                        if(Username==null&& School==null){
                                            bottomSheetDialog.dismiss();
                                        } else {
                                            addPost(Username, School, Caption);
                                        }
//                                        bottomSheetDialog.dismiss();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(requireActivity(),"Update your profile details", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                if (bottomSheetView.getParent() != null){
                    ((ViewGroup) bottomSheetView.getParent()).removeView(bottomSheetView);
                }
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

            }
        });

        //Show Market Items
        displayMarketItems();
        // Show posts
        displayPosts();

        return v;
    }


    private void displayMarketItems(){
        ad_recyclView.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ad_recyclView.setItemAnimator(new DefaultItemAnimator());

        Query query = FirebaseFirestore.getInstance().collection("market_items").orderBy("timestamp",Query.Direction.DESCENDING).limit(50);
        FirestoreRecyclerOptions<FirestoreModel> options4 = new FirestoreRecyclerOptions.Builder<FirestoreModel>()
                .setQuery(query, FirestoreModel.class).build();
        adapter2 = new CampMarketAdapter(options4, this);
        ad_recyclView.setAdapter(adapter2);
    }

    private void displayPosts() {
        postProgress.setVisibility(View.VISIBLE);
        post_recyclView.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));
        post_recyclView.setItemAnimator(new DefaultItemAnimator());

        Query query = FirebaseFirestore.getInstance().collection("camp_meet_posts").orderBy("timestamp",Query.Direction.DESCENDING).limit(50);
        FirestoreRecyclerOptions<FirestoreModel> options3 = new FirestoreRecyclerOptions.Builder<FirestoreModel>()
                .setQuery(query, FirestoreModel.class)
                .build();
        adapter = new CampPostsAdapter(options3, this);

        adapter.startListening();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                post_recyclView.setAdapter(adapter);
                postProgress.setVisibility(View.GONE);
            }
        }, 1500);   //5 seconds

    }

    private void addPost(String Username,String School, String Caption ) {
        UUID uuid = UUID.randomUUID();
        String pattern = "dd/MM/yy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        String timestamp = simpleDateFormat.format(date);
        if (filePath != null){

            // Get the Uri of data
            StorageReference storageRef = storage.getReference();
            // Defining the child of storageReference
            StorageReference ref = storageRef.child("campMeetPosts/" +"/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "/" + uuid.toString() + "." +"jpg");
            ref.putFile(filePath).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            postingProgress.setVisibility(View.VISIBLE);
                            postProgress.setIndeterminate(false);
                            int progress = (int) (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            Toast.makeText(getActivity(), "Posting...Please wait "+ progress+"%", Toast.LENGTH_SHORT).show();
                            post_btn.setClickable(false);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference())).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            postingProgress.setVisibility(View.GONE);
                            CampMeetPosts newPost = new CampMeetPosts(mAuth.getUid(),uuid.toString(),Username, School, uri.toString(), Caption, timestamp);
                            FirebaseFirestore profRef = FirebaseFirestore.getInstance();
                            profRef.collection("camp_meet_posts").document(uuid.toString()).set(newPost);
                            bottomSheetDialog.dismiss();
                            Toast.makeText(getActivity(),"Done!!!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        else {
            CampMeetPosts newPost = new CampMeetPosts(mAuth.getUid(),uuid.toString(),Username, School, "", Caption, timestamp);
            FirebaseFirestore profRef = FirebaseFirestore.getInstance();
            profRef.collection("camp_meet_posts").document(uuid.toString()).set(newPost);
            bottomSheetDialog.dismiss();
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

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), filePath);
                iv_image_post.setVisibility(View.VISIBLE);
                iv_image_post.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }

    }

    private void getProfilePicture() {
       ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                Picasso.get().load(url).into(profpic);
            }
            }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(requireActivity(),"Update your profile details", Toast.LENGTH_SHORT).show();
           }
       });

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
        ad_recyclView.setAdapter(null);
    }
}
