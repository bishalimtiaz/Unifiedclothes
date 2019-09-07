package com.blz.prisoner.unifiedclothes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

public class FullScreenActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {


    private ViewPager viewPager;
    private List<Images> imagesList;

    private FullSizeAdapter fullSizeAdapter;

    //Images images;


    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        if(savedInstanceState==null){
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            imagesList = (List<Images>) extras.getSerializable("IMAGES");
            position = extras.getInt("POSITION",0);
        }
        viewPager = findViewById(R.id.viewPager);
        //images = imagesList.get(position);
        //Toast.makeText(FullScreenActivity.this,images.getImagePath(),Toast.LENGTH_SHORT).show();


        fullSizeAdapter = new FullSizeAdapter(FullScreenActivity.this,imagesList);
        viewPager.setAdapter(fullSizeAdapter);
        viewPager.setCurrentItem(position);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        fullSizeAdapter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
