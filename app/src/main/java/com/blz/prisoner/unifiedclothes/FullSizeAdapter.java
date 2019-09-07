package com.blz.prisoner.unifiedclothes;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.io.OutputStream;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static java.io.File.separator;

public class FullSizeAdapter extends PagerAdapter implements ActivityCompat.OnRequestPermissionsResultCallback{

    private Context context;
    private List<Images> imagesList;

    private Images images;

    private LayoutInflater inflater;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private Bitmap myBitMap;


    public FullSizeAdapter(Context context, List<Images> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
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
                Toast.makeText(context,Integer.toString(position),Toast.LENGTH_SHORT).show();
            }
        });

        amazon_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,Integer.toString(position),Toast.LENGTH_SHORT).show();
            }
        });

        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,Integer.toString(position),Toast.LENGTH_SHORT).show();
            }
        });



        /*** Download Image***/
        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                myBitMap = bitmap;
                ContentResolver contentResolver = context.getContentResolver();


                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){


                    if (checkPermission())
                    {

                        /*** If Storage Permission Is Given, Check External storage is available for read and write***/

                        if(isExternalStorageWritable()){

                            saveImage(bitmap,"Unifiedclothes",contentResolver);
                            Toast.makeText(context,"Photo Saved To Phone Storage ",Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(context,"Request code Matched",Toast.LENGTH_SHORT).show();
        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(context, "Permission Granted... \n Now you can use local drive .", Toast.LENGTH_LONG).show();


                ContentResolver contentResolver = context.getContentResolver();

                /*** If Storage Permission Is Given, Check External storage is available for read and write***/

                if (isExternalStorageWritable()) {

                    saveImage(myBitMap, "Unifiedclothes", contentResolver);
                    Toast.makeText(context, "Photo Saved To Phone Storage ", Toast.LENGTH_SHORT).show();
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
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    /*** Save Image To Storage***/

    private void saveImage(Bitmap bitmap, String folderName, ContentResolver contentResolver){
        //For API 29 or Later
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

            ContentValues values = new ContentValues();

            //Relative path of this media item within the storage device where it is persisted.
            values.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/" + folderName);

            values.put(MediaStore.Images.Media.IS_PENDING,true);
            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            //saveImageToStream

            if(uri != null){
                try {
                    saveImageToStream(bitmap, contentResolver.openOutputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                contentResolver.update(uri, values, null, null);
            }


        }

        //Before API 29
        else{

            File directory = new File(Environment.getExternalStorageDirectory().toString() + separator + folderName);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = String.format("%d.jpg",System.currentTimeMillis());
            File file = new File(directory, fileName);

            FileOutputStream ouputStream = null;
            try {
                ouputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            saveImageToStream(bitmap, ouputStream);

            //file.getAbsolutePath();
            ContentValues values = contentValues();
            //values = contentValues();
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        }

    }

    private ContentValues contentValues(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;

    }


    private  void saveImageToStream(Bitmap bitmap, OutputStream outputStream){

        if(outputStream != null){


            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



}
