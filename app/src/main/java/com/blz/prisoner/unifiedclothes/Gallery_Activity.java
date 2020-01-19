package com.blz.prisoner.unifiedclothes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.FixedPreloadSizeProvider;

import java.io.IOException;
import java.util.List;



public class Gallery_Activity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GridLayoutManager layoutManager;

    private ApiInterface apiInterface;
    private RecyclerViewAdapter adapter;

    public TextView counter_text;
    public ImageView toolbar_image;


    boolean is_in_action = false;
    boolean isregistered = false;

    private final int imageWidthPixels = 196;
    private final int imageHeightPixels = 147;
    List<Images> images;


    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        counter_text = findViewById(R.id.counter_text);
        toolbar_image = findViewById(R.id.toolbar_image);



        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        progressBar.setVisibility(View.VISIBLE);

        Call<List<DataResponse>> call = apiInterface.getImages();

        call.enqueue(new Callback<List<DataResponse>>() {
            @Override
            public void onResponse(Call<List<DataResponse>> call, Response<List<DataResponse>> response) {

                images = response.body().get(0).getImages();
                ListPreloader.PreloadSizeProvider sizeProvider =
                        new FixedPreloadSizeProvider(imageWidthPixels, imageHeightPixels);
                ListPreloader.PreloadModelProvider modelProvider = new MyPreloaderModelProvider(Gallery_Activity.this,images);
                RecyclerViewPreloader<ContactsContract.Contacts.Photo> preloader =
                        new RecyclerViewPreloader<>(
                                Glide.with(Gallery_Activity.this), modelProvider, sizeProvider, 5 /*maxPreload*/);
                recyclerView.addOnScrollListener(preloader);
                adapter = new RecyclerViewAdapter(images,Gallery_Activity.this);
                recyclerView.setAdapter(adapter);
                //Toast.makeText(Gallery_Activity.this,"First Page Is Loaded...",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<DataResponse>> call, Throwable t) {

                Toast.makeText(Gallery_Activity.this,"Failure",Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()== R.id.menu_download){
            adapter.download_multiple();
        }

        if (item.getItemId()== R.id.menu_share){
            try {
                adapter.share_multple();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        adapter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onPause() {

        if(isregistered){
            unregisterReceiver(adapter.receiver);
            isregistered = false;
        }
        super.onPause();
        super.onPause();
    }
}
