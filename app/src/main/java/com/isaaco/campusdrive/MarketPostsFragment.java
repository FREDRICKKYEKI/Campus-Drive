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

public class MarketPostsFragment extends Fragment {
    RecyclerView marketRecycl;
    FirebaseAuth mAuth;
    AccMarketAdapter acc_market_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View v = inflater.inflate(R.layout.fragment_market_posts, container, false);
         marketRecycl = v.findViewById(R.id.tab_market_posts_recyclerview);
         mAuth  = FirebaseAuth.getInstance();
        displayMarketItems();
        return v;
    }

    private void displayMarketItems() {

        marketRecycl.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));
        marketRecycl.setItemAnimator(new DefaultItemAnimator());

        Query query = FirebaseFirestore.getInstance().collection("market_items").orderBy("timestamp",Query.Direction.DESCENDING).limit(50).whereIn("email", Collections.singletonList(mAuth.getCurrentUser().getEmail()));
        FirestoreRecyclerOptions<FirestoreModel> options5 = new FirestoreRecyclerOptions.Builder<FirestoreModel>()
                .setQuery(query, FirestoreModel.class).build();

        acc_market_adapter = new AccMarketAdapter(options5, this);
        acc_market_adapter.startListening();
        marketRecycl.setAdapter(acc_market_adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        acc_market_adapter.startListening();
    }

}