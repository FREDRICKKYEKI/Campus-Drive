package com.isaaco.campusdrive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.isaaco.campusdrive.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collections;

public class OpenFolderFragment extends Fragment {

    private static final String TAG = "titleValue";
    RecyclerView docRecyclerView;
    DocAdapter adapter2;
    String titleValue;
    Toolbar actionbar;
    MaterialToolbar actionbar1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v =  inflater.inflate(R.layout.fragment_open_folder, container,false);

        actionbar = v.findViewById(R.id.action_Bar);
        actionbar1 = v.findViewById(R.id.action_Bar1);
        Bundle folderBundle = getArguments();
        assert folderBundle != null;
        titleValue = folderBundle.getString("folder_name");
        docRecyclerView = v.findViewById(R.id.doc_recycler_view);
        Log.d(TAG, "onCreateView: "+ titleValue);
        displayDocs();

        actionbar.setSubtitle(titleValue);
        actionbar1.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        actionbar1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        homeFragment).commit();

            }
        });



        return v;
    }


    private void displayDocs() {
        docRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        docRecyclerView.setItemAnimator(new DefaultItemAnimator());

        Query query2 = FirebaseFirestore.getInstance().collection("files").orderBy("createdAt").limit(50).whereIn("parentFolder", Collections.singletonList(titleValue));

        FirestoreRecyclerOptions<FirestoreModel> options2 = new FirestoreRecyclerOptions.Builder<FirestoreModel>()
                .setQuery(query2, FirestoreModel.class)
                .build();

        adapter2 = new DocAdapter(options2);
        docRecyclerView.setAdapter(adapter2);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter2.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter2.stopListening();
    }
}