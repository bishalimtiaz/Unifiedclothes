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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import static android.content.Context.DOWNLOAD_SERVICE;
import static androidx.core.app.ActivityCompat.requestPermissions;


public class FullSizeAdapter extends PagerAdapter implements ActivityCompat.OnRequestPermissionsResultCallback{

    private Context context;
    private List<Images> imagesList;

    FullScreenActivity fullScreenActivity;

    private Images images;

    private LayoutInflater inflater;
    private static final int PERMISSION_REQUEST_CODE = 1;


    //New Work

    private long referenceID;
    private DownloadManager downloadManager;

    String url;

    //New Work


    public FullSizeAdapter(Context context, List<Images> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
        fullScreenActivity = (FullScreenActivity) context;
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.full_screen_item_layout,null);

        final ImageView imageView = v.findViewById(R.id.img);




        ImageButton ebay_button = v.findViewById(R.id.ebay_button);
        ImageButton amazon_button = v.findViewById(R.id.amazon_button);
        ImageButton share_button = v.findViewById(R.id.share_button);
        ImageButton download_button = v.findViewById(R.id.download_button);




        ebay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrlViaBrowser("http://www.ebaystores.co.uk/UnifiedClothes");
            }
        });

        amazon_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrlViaBrowser("https://www.amazon.co.uk/stores/Unifiedclothes/node/10450243031");
            }
        });



        /***Share Images***/
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                String title  = "Please Visit http://www.ebaystores.co.uk/UnifiedClothes or https://www.amazon.co.uk/unifiedclothes To Get More New Dresses";



                try {
                    File file = new File(context.getExternalCacheDir(),"Dress.jpg");
                    FileOutputStream fout = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,fout);
                    fout.flush();
                    fout.close();
                    file.setReadable(true,false);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_TEXT,title);
                    intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file));
                    intent.setType("image/jpeg");

                    //need to fix the bug if no shareable app found
                    context.startActivity(Intent.createChooser(intent,"Share Image Via"));


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(context,"File Not Found",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });



        /*** Download Image***/
        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context.registerReceiver(receiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                fullScreenActivity.isregistered = true;
                /*BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                myBitMap = bitmap;
                ContentResolver contentResolver = context.getContentResolver();

                */
                url = imagesList.get(position).getImagePath();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){


                    if (checkPermission())
                    {

                        /*** If Storage Permission Is Given, Check External storage is available for read and write***/

                        if(isExternalStorageWritable()){

                            //saveImage(bitmap,"Unifiedclothes",contentResolver);


                            Toast.makeText(context,"Downloading.... ",Toast.LENGTH_SHORT).show();
                            DownloadImage(url);
                        }
                        else{

                            Toast.makeText(context,"External Storage is Not Available For Write",Toast.LENGTH_SHORT).show();

                        }




                    } else {

                        requestPermission();
                    }

                }

                else{
                    Toast.makeText(context,"Permission Is Granted..",Toast.LENGTH_SHORT).show();

                }


            }
        });



        images = imagesList.get(position);

        Glide.with(context).load(images.getImagePath()).apply(new RequestOptions().centerInside()).into(imageView);

        ViewPager vp = (ViewPager) container;
        vp.addView(v,0);
        return v;



    }

    //end of instantiateItem

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        ViewPager viewPager = (ViewPager) container;

        View view = (View) object;
        viewPager.removeView(view);
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




    /***on RequestPermissionResult an Alert Dialogue Will Appear to Ask User To Enable Or Disable Storage Permission ***/

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {





                /*** If Storage Permission Is Given, Check External storage is available for read and write***/

                if (isExternalStorageWritable()) {


                    Toast.makeText(context,"Downloading.... ",Toast.LENGTH_SHORT).show();
                    DownloadImage(url);


                } else {

                    Toast.makeText(context, "External Storage is Not Available For Write", Toast.LENGTH_SHORT).show();

                }
            }

            else {

                Toast.makeText(context, "Permission Denied... \n You Should Allow External Storage Permission To Download Images.", Toast.LENGTH_LONG).show();
            }
        }


    }


    /***Checks if external storage is available for read and write ***/
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }



    private void openUrlViaBrowser(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }


    /*** New Work***/


    private void DownloadImage(String url){
        String filename=url.substring(url.lastIndexOf("/")+1);
        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES ).getPath()+"/UnifiedClothes/" + filename);
        Log.d("Environment", "Environment extraData=" + file.getPath());

        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url))
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

        }
    };



}
