package com.isaaco.campusdrive;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isaaco.campusdrive.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AccMarketAdapter extends FirestoreRecyclerAdapter<FirestoreModel, AccMarketAdapter.MyViewHolder> {
    MarketPostsFragment marketPostsFragment;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options5
     */
    public AccMarketAdapter(@NonNull FirestoreRecyclerOptions<FirestoreModel> options5, MarketPostsFragment marketPostsFragment) {
        super(options5);
        this.marketPostsFragment = marketPostsFragment;
    }

    @Override
    protected void onBindViewHolder(@NonNull AccMarketAdapter.MyViewHolder holder, int position, @NonNull FirestoreModel model) {
        Picasso.get().load(model.getUrl()).into(holder.itemImage);
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(marketPostsFragment.getContext());
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
                        profRef.collection("market_items").document(model.getItem_id()).delete();

                        //Delete storage data
                        FirebaseAuth mAuth =  FirebaseAuth.getInstance();
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference ref = storageRef.child("marketItems/" +"/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()+"/" +model.getItem_id()+ "." + "jpg");
                        ref.delete();
                        Toast.makeText(marketPostsFragment.getContext(), "Deleted Successfully...", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        holder.itemImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) marketPostsFragment.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);
                AlertDialog.Builder builder = new AlertDialog.Builder(marketPostsFragment.getContext());
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
                        profRef.collection("market_items").document(model.getItem_id()).delete();

                        //Delete storage data
                        FirebaseAuth mAuth =  FirebaseAuth.getInstance();
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference ref = storageRef.child("marketItems/" +"/"+ Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()+"/" +model.getItem_id()+ "." + "jpg");
                        ref.delete();
                        Toast.makeText(marketPostsFragment.getContext(), "Deleted Successfully...", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });
    }


    @NonNull
    @Override
    public AccMarketAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.acc_ad_item, parent, false);
        return  new MyViewHolder(view);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder  {
        ImageView itemImage;
        LinearLayout btn_delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.adItem2);
            btn_delete = itemView.findViewById(R.id.btn_delete2);
        }
    }
}
