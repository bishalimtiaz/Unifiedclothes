package com.blz.prisoner.unifiedclothes;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.DOWNLOAD_SERVICE;
import static androidx.core.app.ActivityCompat.requestPermissions;
import static java.io.File.separator;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolderClass> implements ActivityCompat.OnRequestPermissionsResultCallback {

    private List<Images> imagesList;
    private List<Images> mainList;
    private Context context;
    private int counter = 21;

    private ArrayList<Bitmap> multiple_images = new ArrayList<>();
    private ArrayList<String> urls = new ArrayList<>();


    private Gallery_Activity galleryActivity;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private int count = 0;
    private long referenceID;
    private DownloadManager downloadManager;


    public RecyclerViewAdapter(List<Images> imagesList, Context context) {
        this.mainList = imagesList;
        this.imagesList = imagesList.subList(0,20);
        this.context = context;
        galleryActivity = (Gallery_Activity) context;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
        return new ViewHolderClass(view,galleryActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {

        Images image = imagesList.get(position);
        Glide.with(context)
                .load(image.getImagePath())
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if(counter <= mainList.size()){
                            imagesList.add(mainList.get(counter));
                            counter++;
                            notifyDataSetChanged();
                        }

                        return false;
                    }
                })
                .into(holder.album_image);

        if(!galleryActivity.is_in_action){
            holder.checkbox.setVisibility(View.GONE);
        }
        else{
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(false);
        }


    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //ContentResolver contentResolver = context.getContentResolver();

                /*** If Storage Permission Is Given, Check External storage is available for read and write***/

                if (isExternalStorageWritable()) {

                    Toast.makeText(context,"Downloading.... ",Toast.LENGTH_SHORT).show();
                    DownloadImage(urls);
                    reset();
                    //Toast.makeText(context, "Photo Saved To Phone Storage ", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(context, "External Storage is Not Available For Write", Toast.LENGTH_SHORT).show();

                }
            }

            else {

                Toast.makeText(context, "Permission Denied... \n You Should Allow External Storage Permission To Download Images.", Toast.LENGTH_LONG).show();
            }
        }
    }


    class ViewHolderClass extends RecyclerView.ViewHolder{

        ImageView album_image;

        Gallery_Activity galleryActivity;
        CheckBox checkbox;


        ViewHolderClass(@NonNull final View itemView, final Gallery_Activity galleryActivity) {
            super(itemView);

            album_image = itemView.findViewById(R.id.album_image);

            checkbox = itemView.findViewById(R.id.checkbox);
            this.galleryActivity = galleryActivity;

            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            album_image.setMinimumWidth(width/2);



            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if(!galleryActivity.is_in_action){

                        galleryActivity.is_in_action=true;
                        galleryActivity.toolbar.inflateMenu(R.menu.menu_action_mode);
                        notifyDataSetChanged();


                    }


                    return true;
                }
            });



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!galleryActivity.is_in_action){

                        Intent intent = new Intent(context,FullScreenActivity.class);
                        Bundle extras=new Bundle();
                        extras.putSerializable("IMAGES", (Serializable) mainList);
                        extras.putInt("POSITION",getAdapterPosition());
                        intent.putExtras(extras);
                        context.startActivity(intent);

                    }

                    else {

                        galleryActivity.toolbar_image.setVisibility(View.GONE);
                        galleryActivity.counter_text.setVisibility(View.VISIBLE);
                        BitmapDrawable drawable = (BitmapDrawable) album_image.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();

                        if (checkbox.isChecked()){
                            checkbox.setChecked(false);
                            count--;
                            String s= count + " Item Selected";
                            galleryActivity.counter_text.setText(s);
                            multiple_images.remove(bitmap);
                            urls.remove(imagesList.get(getAdapterPosition()).getImagePath());
                        }else {
                            checkbox.setChecked(true);
                            count++;
                            String s= count + " Items Selected";
                            galleryActivity.counter_text.setText(s);
                            multiple_images.add(bitmap);
                            urls.add(imagesList.get(getAdapterPosition()).getImagePath());
                        }
                    }
                }
            });

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    galleryActivity.toolbar_image.setVisibility(View.GONE);
                    galleryActivity.counter_text.setVisibility(View.VISIBLE);
                    BitmapDrawable drawable = (BitmapDrawable) album_image.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();

                    if (checkbox.isChecked()){

                        count++;
                        String s= count + " Items Selected";
                        galleryActivity.counter_text.setText(s);
                        multiple_images.add(bitmap);
                        urls.add(imagesList.get(getAdapterPosition()).getImagePath());
                    }else {

                        count--;
                        String s= count + " Item Selected";
                        galleryActivity.counter_text.setText(s);
                        multiple_images.remove(bitmap);
                        urls.remove(imagesList.get(getAdapterPosition()).getImagePath());


                    }
                }
            });



        }

    }

    void share_multple() throws IOException {

        ArrayList<Uri> files = new ArrayList<>();
        String title  = "Please Visit http://www.ebaystores.co.uk/UnifiedClothes or https://www.amazon.co.uk/unifiedclothes To Get More New Dresses";
        int i = 1;
        for (Bitmap b : multiple_images){

            String t = "Dress"+i+".jpg";
            File file = new File(context.getExternalCacheDir(),t);
            FileOutputStream fout = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG,100,fout);
            Uri uri = Uri.fromFile(file);
            files.add(uri);
            fout.flush();
            fout.close();
            file.setReadable(true,false);
            i++;

        }


        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_TEXT,title);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,files);
        intent.setType("image/jpeg");

        //need to fix the bug if no shareable app found
        context.startActivity(Intent.createChooser(intent,"Share Image Via"));
        reset();

    }


    void download_multiple(){



        context.registerReceiver(receiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        galleryActivity.isregistered = true;



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){


            if (checkPermission())
            {

                /*** If Storage Permission Is Given, Check External storage is available for read and write***/

                if(isExternalStorageWritable()){

                    Toast.makeText(context,"Downloading.... ",Toast.LENGTH_SHORT).show();
                    DownloadImage(urls);
                    reset();

                }
                else{

                    Toast.makeText(context,"External Storage is Not Available For Write",Toast.LENGTH_SHORT).show();

                }




            } else {

                requestPermission(); // Code for permission
            }

        }

        else{
            Toast.makeText(context,"Permission Is Granted..",Toast.LENGTH_SHORT).show();

        }


    }

    /***Checking Media Storage Permission ***/
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    /***If Media Storage Permission Is Not Granted Request Storage Permission***/
    private void requestPermission() {

        requestPermissions((Activity) context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    /***Checks if external storage is available for read and write ***/
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }




    private void reset(){
        count = 0;
        galleryActivity.toolbar_image.setVisibility(View.VISIBLE);
        galleryActivity.counter_text.setVisibility(View.GONE);
        galleryActivity.is_in_action=false;
        galleryActivity.toolbar.getMenu().clear();
        notifyDataSetChanged();
        multiple_images.clear();
        urls.clear();
    }


    private void DownloadImage(ArrayList<String> multipleUrls){

        for (String ur : multipleUrls){


            String filename=ur.substring(ur.lastIndexOf("/")+1);
            File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES ).getPath()+"/UnifiedClothes/" + filename);
            Log.d("Environment", "Environment extraData=" + file.getPath());

            DownloadManager.Request request=new DownloadManager.Request(Uri.parse(ur))
                    .setTitle(filename)
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(Uri.fromFile(file))
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);
            downloadManager= (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            referenceID = downloadManager.enqueue(request);

        }



    }



    private String DownloadStatus(Cursor cursor){

        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);

        String statusText = "";
        String reasonText = "";

        switch(status){
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch(reason){
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch(reason){
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "Image Saved Successfully";
                break;


        }

        return statusText + reasonText;


    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){


                DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();
                //set the query filter to our previously Enqueued download
                ImageDownloadQuery.setFilterById(referenceID);

                //Query the download manager about downloads that have been requested.
                Cursor cursor = downloadManager.query(ImageDownloadQuery);

                if(cursor.moveToFirst()){

                    Toast.makeText(context,DownloadStatus(cursor),Toast.LENGTH_SHORT).show();

                }



            }

            else {
                DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();
                ImageDownloadQuery.setFilterById(referenceID);
                Cursor cursor = downloadManager.query(ImageDownloadQuery);

                if(cursor.moveToFirst()){

                    Toast.makeText(context,DownloadStatus(cursor),Toast.LENGTH_SHORT).show();

                }

            }

        }
    };




}
