package com.noteworth.lunch.viewmodel.feature;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.noteworth.lunch.model.LunchModel;
import com.noteworth.lunch.model.data.json.place.Photo;
import com.noteworth.lunch.model.data.json.place.details.Result;
import com.noteworth.lunch.model.feature.GooglePlacesApi;

import java.util.List;

/**
 * Created by dfbarone on 2/16/2018.
 */

public class BaseLunchViewModel extends ViewModel {

    protected LunchModel mLunchModel;

    public BaseLunchViewModel() {
        mLunchModel = LunchModel.getInstance();
    }

    public static String getImageUrl(Photo photo, int width, int height) {
        String url = "";
        if (photo != null) {
            url = GooglePlacesApi.getImageUrl(photo.getPhotoReference(), width, height);
        }
        return url;
    }

    public static String getImageUrl(List<Photo> photos, int width, int height) {
        String url = "";
        if (photos != null && photos.size() > 0) {
            url = GooglePlacesApi.getImageUrl(photos.get(0).getPhotoReference(), width, height);
        }
        return url;
    }

    public static void loadIconImage(Context context, String url, final ImageView imageView, @DrawableRes int placeholder) {
        final double radiusPercent = .08;

        Glide.with(context)
                .load(url)
                .asBitmap()
                .fitCenter()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(placeholder)
                .placeholder(placeholder)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(imageView.getContext().getResources(), resource);
                        int minSize = Math.min(resource.getWidth(), resource.getHeight());
                        circularBitmapDrawable.setCornerRadius((int) (minSize * radiusPercent));
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    public static void loadImage(Context context, String url, final ImageView imageView, @DrawableRes int placeholder) {

        Glide.with(context)
                .load(url)
                .asBitmap()
                .fitCenter()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(placeholder)
                .placeholder(placeholder)
                .into(imageView);
    }
}
