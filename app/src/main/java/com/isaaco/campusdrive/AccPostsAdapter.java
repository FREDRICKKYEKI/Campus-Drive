package com.isaaco.campusdrive;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.isaaco.campusdrive.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AccPostsAdapter extends FirestoreRecyclerAdapter<FirestoreModel, AccPostsAdapter.MyViewHolder> {
    PostsFragement postsFragement;
    FirebaseAuth mAuth;
    String name, school;
    FirebaseFirestore profRef;


    public AccPostsAdapter(@NonNull FirestoreRecyclerOptions<FirestoreModel> options4, PostsFragement postsFragement) {
        super(options4);
        this.postsFragement = postsFragement;
    }

    @NonNull
    @Override
    public AccPostsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.acc_post_item, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull AccPostsAdapter.MyViewHolder holder, int position, @NonNull FirestoreModel model) {

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(postsFragement.getContext());
                builder.setTitle("Delete Item");
                builder.setIcon(R.drawable.ic_delete);
                builder.setMessage("Are you sure you want to delete item?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                                    //Delete firestore data
                        FirebaseFirestore profRef = FirebaseFirestore.getInstance();
                        profRef.collection("camp_meet_posts").document(model.getItem_id()).delete();
                        //Delete storage data
                        FirebaseAuth mAuth =  FirebaseAuth.getInstance();
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference ref = storageRef.child("campMeetPosts/" +"/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "/" + model.getItem_id() + "." +"jpg");
                        ref.delete();
                        Toast.makeText(postsFragement.getContext(), "Deleted Successfully...", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) postsFragement.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);
                AlertDialog.Builder builder = new AlertDialog.Builder(postsFragement.getContext());
                builder.setTitle("Delete Item");
                builder.setIcon(R.drawable.ic_delete);
                builder.setMessage("Are you sure you want to delete item?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete firestore data
                        FirebaseFirestore profRef = FirebaseFirestore.getInstance();
                        profRef.collection("camp_meet_posts").document(model.getItem_id()).delete();
                        //Delete storage data
                        FirebaseAuth mAuth =  FirebaseAuth.getInstance();
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference ref = storageRef.child("campMeetPosts/" +"/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "/" + model.getItem_id() + "." +"jpg");
                        ref.delete();
                        Toast.makeText(postsFragement.getContext(), "Deleted Successfully...", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });

        profRef = FirebaseFirestore.getInstance();
        profRef.collection("user_profiles").document(Objects.requireNonNull(mAuth.getCurrentUser().getEmail())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                name = document.getString("name");
                school = document.getString("school");
                holder.post_name.setText(name);
                holder.post_school.setText(school);
            }
        });

        if (model.getPost_url().isEmpty()){
            holder.post_timestamp.setText(model.getTimestamp());
            holder.post_caption.setText(model.getCaption());
            holder.imagePost.setVisibility(View.GONE);
        }
        else {
            holder.post_timestamp.setText(model.getTimestamp());
            holder.post_caption.setText(model.getCaption());
            Picasso.get().load(model.getPost_url()).into(holder.imagePost);
        }

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference ref;
        ref = storageRef.child("userProfiles/" +"/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "." + "jpg");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                Picasso.get().load(url).into(holder.post_profile);
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView post_name;
        TextView post_school;
        TextView post_timestamp;
        TextView post_caption;
        ImageView imagePost;
        LinearLayout btn_delete;
        de.hdodenhof.circleimageview.CircleImageView post_profile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            post_name = itemView.findViewById(R.id.tv_acc_user_name);
            post_school = itemView.findViewById(R.id.tv_acc_school);
            post_timestamp = itemView.findViewById(R.id.tv_acc_timestamp);
            post_caption = itemView.findViewById(R.id.acc_caption_post);
            imagePost = itemView.findViewById(R.id.acc_mediaPost);
            post_profile = itemView.findViewById(R.id.acc_profile_image);
        }
    }
}
