package org.arb.Nextgen.ePharma.adapter;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.arb.Nextgen.ePharma.Model.DocumentListModel;
import org.arb.Nextgen.ePharma.R;
import org.arb.Nextgen.ePharma.config.Snackbar;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomDocumentListAdapter extends RecyclerView.Adapter<CustomDocumentListAdapter.MyViewHolder> {
    public LayoutInflater inflater;
    public static ArrayList<DocumentListModel> documentListModelArrayList;
    private Context context;
    public boolean downloading = true;
    public boolean flag = true;
    public static long idDownLoad = 0;
    DownloadManager dm;
    public static ProgressDialog loading;
//    public static TextView tv_download;


    public CustomDocumentListAdapter(Context ctx, ArrayList<DocumentListModel> documentListModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.documentListModelArrayList = documentListModelArrayList;
    }
    @Override
    public CustomDocumentListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_document, parent, false);
        CustomDocumentListAdapter.MyViewHolder holder = new CustomDocumentListAdapter.MyViewHolder(view);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(CustomDocumentListAdapter.MyViewHolder holder, int position) {
        holder.itemView.setTag(documentListModelArrayList.get(position));
        holder.tv_doc_name.setText(documentListModelArrayList.get(position).getDoc_name());
//        holder.tv_upload_date_size.setText(documentListModelArrayList.get(position).getSize());
        holder.tv_desc.setText(documentListModelArrayList.get(position).getDescription());
        if(documentListModelArrayList.get(position).getExtension().contentEquals("xlsx")){
            holder.img_fileimage.setBackgroundResource(R.drawable.excel);
        }else if(documentListModelArrayList.get(position).getExtension().contentEquals("docx")){
            holder.img_fileimage.setBackgroundResource(R.drawable.word);
        }else if(documentListModelArrayList.get(position).getExtension().contentEquals("ppt")){
            holder.img_fileimage.setBackgroundResource(R.drawable.ppt);
        }else if(documentListModelArrayList.get(position).getExtension().contentEquals("pdf")){
            holder.img_fileimage.setBackgroundResource(R.drawable.pdf);
        }


        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String inputText = documentListModelArrayList.get(position).getUpload_date();
        Date date1 = null;
        try {
            date1 = inputFormat.parse(inputText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputText = outputFormat.format(date1);
        holder.tv_upload_date_size.setText(outputText+"    "+documentListModelArrayList.get(position).getSize());

    }

    @Override
    public int getItemCount() {
        return documentListModelArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_doc_name, tv_upload_date_size, tv_desc, tv_download;
        ImageButton imgbtn_dwnload, imgbtn_share;
        ImageView img_fileimage;


        public MyViewHolder(final View itemView) {
            super(itemView);
            tv_doc_name = itemView.findViewById(R.id.tv_doc_name);
            tv_upload_date_size = itemView.findViewById(R.id.tv_upload_date_size);
            tv_desc = itemView.findViewById(R.id.tv_desc);
            tv_download = itemView.findViewById(R.id.tv_download);

            imgbtn_dwnload = itemView.findViewById(R.id.imgbtn_dwnload);
            imgbtn_share = itemView.findViewById(R.id.imgbtn_share);

            img_fileimage = itemView.findViewById(R.id.img_fileimage);

            imgbtn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);

                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, documentListModelArrayList.get(position).getDoc_name());
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, documentListModelArrayList.get(position).getDoc_name() + "\n" +documentListModelArrayList.get(position).getDownload_link());
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(Intent.createChooser(shareIntent, "Sharing file..."));
                    context.startActivity(shareIntent);
                }
            });

            imgbtn_dwnload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();

                    //--------code to download file, create destination folder, starts-----
                    File direct = new File(Environment.getExternalStorageDirectory()
                            + "/Caplet");
                  /*  if (direct.exists()) {
                        if (direct.isDirectory()) {
                            String[] children = direct.list();
                            for (int i = 0; i < children.length; i++) {
                                new File(direct, children[i]).delete();
                            }
                        }
                    }*/
                    if (!direct.exists()) {
                        direct.mkdirs();
                    }
                    try {
                        String fileUrl = documentListModelArrayList.get(position).getDownload_link();
//                    String fileName = "caplet";
                        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);

                        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUrl);

                        // concatinate above fileExtension to fileName
                        fileName += "." + fileExtension;

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl))
                                .setTitle(context.getString(R.string.app_name))
                                .setDescription("Downloading " + fileName)
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
//                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                                .setDestinationInExternalPublicDir("/Caplet", fileName);
                        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                    dm.enqueue(request);
                        idDownLoad = dm.enqueue(request);

                       /* View v1 = itemView.findViewById(R.id.cordinatorLayout);
                        new Snackbar("Downloading",v1, Color.parseColor("#ffffff"));*/

//                        tv_download.setVisibility(View.VISIBLE);
                       loading = ProgressDialog.show(context, "Downloading...", "Please wait while downloading document", false, false);


//                        Toast.makeText(context.getApplicationContext(),"Downloading",Toast.LENGTH_LONG).show();
                    }catch (Error e){
                        View v1 = itemView.findViewById(R.id.cordinatorLayout);
                        new Snackbar("Download Error",v1, Color.parseColor("#ffffff"));
                    }

                }
            });
            //--------code to download file, create destination folder, ends-----



        }
    }

}
