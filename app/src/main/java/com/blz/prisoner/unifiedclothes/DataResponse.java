package com.blz.prisoner.unifiedclothes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DataResponse {

    @SerializedName("images")
    List<Images> images;

    public List<Images> getImages() {
        return images;
    }
}

class Images implements Serializable {

    @SerializedName("image_id")
    private int imageId;
    @SerializedName("image_path")
    private String imagePath;

    public int getImageId() {
        return imageId;
    }

    public String getImagePath() {
        return imagePath;
    }
}
