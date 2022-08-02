package com.isaaco.campusdrive;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isaaco.campusdrive.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

public class CampMarketAdapter extends FirestoreRecyclerAdapter<FirestoreModel, CampMarketAdapter.MyViewHolder> {
    CampMeetFragment campMeetFragment;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options4
     */
    public CampMarketAdapter(@NonNull FirestoreRecyclerOptions<FirestoreModel> options4, CampMeetFragment campMeetFragment) {
        super(options4);
        this.campMeetFragment = campMeetFragment;
    }

    @Override
    protected void onBindViewHolder(@NonNull CampMarketAdapter.MyViewHolder holder, int position, @NonNull FirestoreModel model) {
        Picasso.get().load(model.getUrl()).into(holder.itemImage);
        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            private static final String TAG = "ID";

            @Override
            public void onClick(View v) {
                View customLayout = LayoutInflater.from(campMeetFragment.getContext()).inflate(R.layout.dialog_market, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(campMeetFragment.getContext());
                builder.setView(customLayout);

                MaterialButton btn_call = customLayout.findViewById(R.id.btn_call);
                ZoomageView itemimage = customLayout.findViewById(R.id.item_image);
                MaterialTextView itemname = customLayout.findViewById(R.id.tv_item_name);
                MaterialTextView price=customLayout.findViewById(R.id.tv_price);
                MaterialTextView phoneNumber=customLayout.findViewById(R.id.tv_phone_number);
                MaterialTextView description = customLayout.findViewById(R.id.tv_description);
                MaterialTextView date = customLayout.findViewById(R.id.tv_date);


                FirebaseFirestore profRef = FirebaseFirestore.getInstance();
                DocumentReference docRef= profRef.collection("market_items").document(model.getItem_id());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentSnapshot document = task.getResult();
                        String imageurl = document.getString("url");
                        Picasso.get().load(imageurl).into(itemimage);
                        itemname.setText(document.getString("item_name"));
                        price.setText(document.getString("price"));
                        phoneNumber.setText(document.getString("phonenumber"));
                        description.setText(document.getString("description"));
                        date.setText(document.getString("timestamp"));
                    }
                });
                btn_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                String phone = document.getString("phonenumber");
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                String temp = "tel:" + phone;
                                intent.setData(Uri.parse(temp));

                                campMeetFragment.startActivity(intent);
                            }
                        });


                    }
                });


                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }


    @NonNull
    @Override
    public CampMarketAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item, parent, false);
        return  new MyViewHolder(view);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder  {
        ImageView itemImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.adItem);
        }
    }
}
