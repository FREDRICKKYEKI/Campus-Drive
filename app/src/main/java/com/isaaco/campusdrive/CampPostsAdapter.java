package com.isaaco.campusdrive;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.isaaco.campusdrive.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class CampPostsAdapter extends FirestoreRecyclerAdapter<FirestoreModel, CampPostsAdapter.MyViewHolder> {
        CampMeetFragment campMeetFragment;
        FirebaseAuth mAuth;
        String name, school;
        FirebaseFirestore profRef;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options3
     */
    public CampPostsAdapter(@NonNull FirestoreRecyclerOptions<FirestoreModel> options3, CampMeetFragment campMeetFragment) {
        super(options3);
        this.campMeetFragment = campMeetFragment;
    }

    @NonNull
    @Override
    public CampPostsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CampPostsAdapter.MyViewHolder holder, int position, @NonNull FirestoreModel model) {

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



        FirebaseStorage storage;
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference ref;
        ref = storageRef.child("userProfiles/" + "/" + model.getUser_id() + "." + "jpg");
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
        ZoomageView imagePost;
        de.hdodenhof.circleimageview.CircleImageView post_profile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            post_name = itemView.findViewById(R.id.tv_user_name);
            post_school = itemView.findViewById(R.id.tv_school);
            post_timestamp = itemView.findViewById(R.id.tvtime_stamp);
            post_caption = itemView.findViewById(R.id.caption_post);
            imagePost = itemView.findViewById(R.id.mediaPost);
            post_profile = itemView.findViewById(R.id.profile_image);
        }
    }
}
