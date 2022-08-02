package com.isaaco.campusdrive;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ClassmatesAdapter extends FirestoreRecyclerAdapter<FirestoreModel, ClassmatesAdapter.MyViewHolder> {
    private final Context context;
    FirebaseFirestore profRef;
    FirebaseAuth mAuth;
    String name,school,url;


    public ClassmatesAdapter(@NonNull FirestoreRecyclerOptions<FirestoreModel> options, ClassmatesActivity context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = (LayoutInflater.from(context).inflate(R.layout.classmates_item, parent, false));
        mAuth = FirebaseAuth.getInstance();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassmatesAdapter.MyViewHolder holder, int position,@NonNull FirestoreModel model2) {
        holder.tv_username.setText(model2.getName());
        holder.tv_school.setText(model2.getSchool());
        Picasso.get().load(model2.getUrl()).into(holder.iv_profile);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView tv_username,tv_school;
        ImageView iv_profile;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_username = itemView.findViewById(R.id.cm_username);
            tv_school = itemView.findViewById(R.id.cm_school);
            iv_profile = itemView.findViewById(R.id.cm_profile);
        }
    }
}
