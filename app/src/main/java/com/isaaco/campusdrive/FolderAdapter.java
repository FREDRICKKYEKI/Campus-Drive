package com.isaaco.campusdrive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.isaaco.campusdrive.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;


public class FolderAdapter extends FirestoreRecyclerAdapter<FirestoreModel, FolderAdapter.viewHolder> {
    Fragment openFolderFrag = null;
    HomeFragment homeFragment;

    public FolderAdapter(FirestoreRecyclerOptions<FirestoreModel> options, HomeFragment homeFragment) {
        super(options);
       this.homeFragment = homeFragment;
    }


    @NonNull
    @Override
    public FolderAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_list, parent, false);
        return new viewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull FirestoreModel model) {
        holder.title.setText(model.getName());
        String folderId = FirebaseFirestore.getInstance().collection("folders").document(model.getName()).getId();
        holder.folder_id.setText(folderId);
        holder.itemView.setFocusable(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openFolderFrag = new OpenFolderFragment();
                Bundle folderBundle = new Bundle();
                folderBundle.putString("folder_name", model.getName());
                openFolderFrag.setArguments(folderBundle);
                FragmentTransaction transaction = homeFragment.requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, openFolderFrag);
                transaction.commit();
            }
        });


    }



    public static class viewHolder extends RecyclerView.ViewHolder  {
        TextView title, folder_id;
        ImageView folder_icon;  //Add image later
        public viewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            folder_id = itemView.findViewById(R.id.folderId);
            folder_icon =itemView.findViewById(R.id.folderIcon);
        }
    }


}