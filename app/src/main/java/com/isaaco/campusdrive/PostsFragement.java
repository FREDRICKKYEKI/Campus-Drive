package com.isaaco.campusdrive;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isaaco.campusdrive.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collections;

public class PostsFragement extends Fragment {
    RecyclerView postsRecycl;
    AccPostsAdapter acc_adapter;
    FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_posts_fragement, container, false);

        postsRecycl = v.findViewById(R.id.tab_posts_recyclerview);
        mAuth  = FirebaseAuth.getInstance();

        displayPosts();

        return v;

    }

    private void displayPosts() {
        postsRecycl.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));
        postsRecycl.setItemAnimator(new DefaultItemAnimator());

        Query query = FirebaseFirestore.getInstance().collection("camp_meet_posts").orderBy("timestamp",Query.Direction.DESCENDING).limit(50).whereIn("user_id", Collections.singletonList(mAuth.getUid()));
        FirestoreRecyclerOptions<FirestoreModel> options4 = new FirestoreRecyclerOptions.Builder<FirestoreModel>()
                .setQuery(query, FirestoreModel.class)
                .build();
        acc_adapter = new AccPostsAdapter(options4, this);
        postsRecycl.setAdapter(acc_adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        acc_adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        acc_adapter.stopListening();
    }
}