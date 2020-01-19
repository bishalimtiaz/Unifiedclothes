package com.blz.prisoner.unifiedclothes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

public class FullScreenActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {


    private ViewPager viewPager;
    private List<Images> imagesList;

    private FullSizeAdapter fullSizeAdapter;

    ImageButton right_btn, left_btn;
    boolean isregistered = false;




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
        right_btn = findViewById(R.id.right_btn);
        left_btn = findViewById(R.id.left_btn);

        fullSizeAdapter = new FullSizeAdapter(FullScreenActivity.this,imagesList);
        viewPager.setAdapter(fullSizeAdapter);
        viewPager.setCurrentItem(position);

        if(position == 0){
            left_btn.setVisibility(View.GONE);
            right_btn.setVisibility(View.VISIBLE);
        }
        else if (position == fullSizeAdapter.getCount() - 1){
            right_btn.setVisibility(View.GONE);
            left_btn.setVisibility(View.VISIBLE);
        }
        else{
            left_btn.setVisibility(View.VISIBLE);
            right_btn.setVisibility(View.VISIBLE);

        }

        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1,true);

            }
        });

        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1,true);

            }
        });

        pageChange();

    }


    private void pageChange() {

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                if(position == 0){
                    left_btn.setVisibility(View.GONE);
                    right_btn.setVisibility(View.VISIBLE);
                }
                else if (position == fullSizeAdapter.getCount() - 1){
                    right_btn.setVisibility(View.GONE);
                    left_btn.setVisibility(View.VISIBLE);
                }
                else{
                    right_btn.setVisibility(View.VISIBLE);
                    left_btn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        fullSizeAdapter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {


        if(isregistered){
            unregisterReceiver(fullSizeAdapter.receiver);
            isregistered = false;
        }
        super.onPause();
    }
}
