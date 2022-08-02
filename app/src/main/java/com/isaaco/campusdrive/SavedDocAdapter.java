package com.isaaco.campusdrive;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.isaaco.campusdrive.R;

import java.io.File;
import java.util.List;

public class SavedDocAdapter extends RecyclerView.Adapter<SavedDocAdapter.MyViewHolder> {
   private Context context;
   private List<File> pdfFiles;

   public SavedDocAdapter(Context context, List<File> pdfFiles){
       this.context= context;
       this.pdfFiles = pdfFiles;
   }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = (LayoutInflater.from(context).inflate(R.layout.doc_saved_list, parent, false));
        return new MyViewHolder(view);
   }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        File tempFile = new File(holder.doc_title.getContext().getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ),pdfFiles.get(position).getName() );

        holder.doc_title.setText(pdfFiles.get(position).getName());
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openPdf (FileProvider.getUriForFile(holder.doc_title.getContext(), holder.doc_title.getContext().getPackageName() + ".provider", tempFile));

        }

        private void openPdf(Uri localUri) {
            Intent intent = new Intent( Intent.ACTION_VIEW );
            intent.setDataAndType( localUri, "application/pdf" );
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            holder.doc_title.getContext().startActivity(intent);
        }
    });
    }


    @Override
    public int getItemCount() {
        return pdfFiles.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView doc_title;
        ImageView doc_icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            doc_title = itemView.findViewById(R.id.DocTitle);
            doc_icon =itemView.findViewById(R.id.DocIcon);
        }
}
}
