package com.blz.prisoner.unifiedclothes;

import android.content.Context;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyPreloaderModelProvider implements ListPreloader.PreloadModelProvider {
    private final int imageWidthPixels = 196;
    private final int imageHeightPixels = 147;
    List<Images> images;
    Context context;

    @NonNull
    @Override
    public List getPreloadItems(int position) {
        String url = images.get(position).getImagePath();
        if (TextUtils.isEmpty(url)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(url);
    }

    public MyPreloaderModelProvider(Context context,List<Images> images) {
        this.context = context;
        this.images = images;
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull Object item) {
        return Glide.with(context)
                .load(item)
                .skipMemoryCache(true);
    }
}
