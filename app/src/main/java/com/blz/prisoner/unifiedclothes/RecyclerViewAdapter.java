package com.blz.prisoner.unifiedclothes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolderClass> {

    private List<Images> imagesList;
    private Context context;
    //private  List<String> paths;


    public RecyclerViewAdapter(List<Images> imagesList, Context context) {
        this.imagesList = imagesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {

        Images image = imagesList.get(position);
        Glide.with(context).load(image.getImagePath()).into(holder.album_image);
        //paths.add(image.getImagePath());

    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }



    public class ViewHolderClass extends RecyclerView.ViewHolder{

        ImageView album_image;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            album_image = itemView.findViewById(R.id.album_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context,Integer.toString(getAdapterPosition()),Toast.LENGTH_SHORT).show();
                    //Images image = imagesList.get(getAdapterPosition());
                    //Toast.makeText(context,image.getImagePath(),Toast.LENGTH_SHORT).show();
                    /*
                    Intent i = new Intent(context,FullScreenActivity.class);
                    i.putExtra("IMAGES", (Serializable) imagesList);
                    i.putExtra("POSITION",getAdapterPosition());
                    context.startActivity(i);
                    */
                    Intent intent = new Intent(context,FullScreenActivity.class);
                    Bundle extras=new Bundle();
                    extras.putSerializable("IMAGES", (Serializable) imagesList);
                    extras.putInt("POSITION",getAdapterPosition());
                    intent.putExtras(extras);
                    context.startActivity(intent);
                }
            });

        }

    }



}
