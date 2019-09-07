package com.blz.prisoner.unifiedclothes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

public class Gallery_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GridLayoutManager layoutManager;

    private ApiInterface apiInterface;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);


        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        progressBar.setVisibility(View.VISIBLE);

        Call<List<DataResponse>> call = apiInterface.getImages();

        call.enqueue(new Callback<List<DataResponse>>() {
            @Override
            public void onResponse(Call<List<DataResponse>> call, Response<List<DataResponse>> response) {

                List<Images> images = response.body().get(0).getImages();
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
}
