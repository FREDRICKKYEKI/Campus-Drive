package com.isaaco.campusdrive;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.isaaco.campusdrive.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.List;

public class DocAdapter extends FirestoreRecyclerAdapter<FirestoreModel, DocAdapter.MyViewHolder> {


    public DocAdapter(@NonNull FirestoreRecyclerOptions<FirestoreModel> options2) {
        super(options2);
    }



    @NonNull
    @Override
    public DocAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_list, parent, false);
        return new MyViewHolder(view);
    }
    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull FirestoreModel model2) {
        holder.doc_title.setText(model2.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            View bottomSheetView;
           BottomSheetDialog bottomSheetDialog;
            @Override
            public void onClick(View v) {
                bottomSheetDialog = new BottomSheetDialog(v.getContext());
                 bottomSheetView = LayoutInflater.from(v.getContext()).inflate(R.layout.modal_doc_bottom_sheet,v.findViewById(R.id.modalBottomSheetContainer2));

                bottomSheetView.findViewById(R.id.download_doc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadFile();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.view_online).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(holder.doc_title.getContext(), ViewDocActivity.class);
                        intent.putExtra("docName", model2.getName());
                        intent.putExtra("docUrl",model2.getUrl());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        holder.doc_title.getContext().startActivity(intent);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.view_offline).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String docName = model2.getName();
                        AlertDialog.Builder alert1 = new AlertDialog.Builder(holder.doc_title.getContext());
                        alert1.setTitle("Done!");
                        alert1.setMessage("Open" + docName + "?");
                        alert1.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        alert1.setPositiveButton( "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        File tempFile = new File(holder.doc_title.getContext().getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ),docName );
                                        openPDF(FileProvider.getUriForFile(holder.doc_title.getContext(), holder.doc_title.getContext().getPackageName() + ".provider", tempFile));
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertD = alert1.create();



                        requestPermissions();
                        File tempFile = new File(holder.doc_title.getContext().getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ),docName );
                        if (tempFile.exists()) {
                            // If we have downloaded the file before, just go ahead and show it.
                            openPDF(FileProvider.getUriForFile(holder.doc_title.getContext(), holder.doc_title.getContext().getPackageName() + ".provider", tempFile));

                        }else {
                            bottomSheetDialog.dismiss();
                            downloadFile();

                         }

                    }
                });

                if (bottomSheetView.getParent() != null){
                    ((ViewGroup) bottomSheetView.getParent()).removeView(bottomSheetView);
                }
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
            private void downloadFile() {
                String docName = model2.getName();
                String docUrl =model2.getUrl();
                AlertDialog.Builder alert1 = new AlertDialog.Builder(holder.doc_title.getContext());
                alert1.setTitle("Done!");
                alert1.setMessage("Open "+docName +" ?");
                alert1.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alert1.setPositiveButton( "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                File tempFile = new File(holder.doc_title.getContext().getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ),docName );
                                openPDF(FileProvider.getUriForFile(holder.doc_title.getContext(), holder.doc_title.getContext().getPackageName() + ".provider", tempFile));
                                dialog.cancel();
                            }
                        });
                AlertDialog alertD = alert1.create();

                ProgressDialog progressDownload = new ProgressDialog(holder.doc_title.getContext());
                progressDownload.setCanceledOnTouchOutside(false);
                progressDownload.setTitle(docName);
                progressDownload.setMessage("Downloading...");
                progressDownload.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Toast.makeText(holder.doc_title.getContext(), "File downloaded.", Toast.LENGTH_SHORT).show();
                        alertD.show();

                    }
                });

                File tempFile = new File(holder.doc_title.getContext().getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ),docName );
                //Log.e(TAG,"File Path:"+tempFile);

                if (tempFile.exists()) {
                    // If we have downloaded the file before, just go ahead and show it.
                    Toast.makeText(holder.doc_title.getContext(), "File already downloaded.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Show progress dialog while downloading
                ProgressDialog progress = new ProgressDialog(holder.doc_title.getContext());
                progress.setTitle("File Download");
                progress.setMessage("Downloading...");

                progressDownload.show();
                Toast.makeText(holder.doc_title.getContext(), "Downloading...", Toast.LENGTH_SHORT).show();
                // Create the download request
                DownloadManager.Request r = new DownloadManager.Request( Uri.parse( docUrl ) );
                r.setDestinationInExternalFilesDir( holder.doc_title.getContext(), Environment.DIRECTORY_DOWNLOADS, docName);
                DownloadManager dm = (DownloadManager) holder.doc_title.getContext().getSystemService( Context.DOWNLOAD_SERVICE );
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if ( !progress.isShowing() ) {
                            progressDownload.dismiss();

                            return;
                        }
                        context.unregisterReceiver( this );

                        progress.dismiss();

                        long downloadId = intent.getLongExtra( DownloadManager.EXTRA_DOWNLOAD_ID, -1 );
                        Cursor c = dm.query( new DownloadManager.Query().setFilterById( downloadId ) );

                        if ( c.moveToFirst() ) {
                            @SuppressLint("Range")
                            int status = c.getInt( c.getColumnIndex( DownloadManager.COLUMN_STATUS ) );
                            int totalSizeIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                            int downloadedIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                            long totalSize = c.getInt(totalSizeIndex);
                            long downloaded = c.getInt(downloadedIndex);
                            int progress = 0;
                                progress = (int) (downloaded * 100 / totalSize);
                                progressDownload.setProgress(progress);

                        }
                        c.close();
                        alertD.show();
                    }

                };
                holder.doc_title.getContext().registerReceiver( onComplete, new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                // Enqueue the request
                dm.enqueue(r);

            }
            private void openPDF(Uri localUri) {
                Intent intent = new Intent( Intent.ACTION_VIEW );
                intent.setDataAndType( localUri, "application/pdf" );
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                holder.doc_title.getContext().startActivity(intent);
            }
            private void requestPermissions() {
                // below line is use to request
                // permission in the current activity.
                Dexter.withContext(holder.doc_title.getContext())
                        // below line is use to request the number of
                        // permissions which are required in our app.
                        .withPermissions(
                                // below is the list of permissions
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                        // after adding permissions we are
                        // calling an with listener method.
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                // this method is called when all permissions are granted
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    // do your1 work now
                                    Toast.makeText(holder.doc_title.getContext(), "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                                }
                                // check for permanent denial of any permission
//                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
//                            // permission is denied permanently,
//                            // we will show user a dialog message.
//                           showSettingsDialog();
//                        }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                // this method is called when user grants some
                                // permission and denies some of them.
                                permissionToken.continuePermissionRequest();
                            }
                        }).withErrorListener(new PermissionRequestErrorListener() {
                    // this method is use to handle error
                    // in runtime permissions
                    @Override
                    public void onError(DexterError error) {
                        // we are displaying a toast message for error message.
                        Toast.makeText(holder.doc_title.getContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                        // below line is use to run the permissions
                        // on same thread and to check the permissions
                        .onSameThread().check();
            }
        });
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView doc_title, doc_timestamp;
        ImageView doc_icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            doc_title = itemView.findViewById(R.id.DocTitle);
            doc_icon =itemView.findViewById(R.id.DocIcon);
        }
    }



}
