package com.ad.aggregator.app;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by hal on 11/9/2016.
 */

public class BaseViewModel {

    public static List<RecipeItem> mRecipeList = new ArrayList<RecipeItem>();

    public static List<RecipeItem> mChefsList = new ArrayList<RecipeItem>();

    public static List<RecipeItem> mShowsList = new ArrayList<RecipeItem>();

    public BaseViewModel() {
        populateList(mRecipeList);
        populateList2(mChefsList);
        populateList3(mShowsList);

    }

    private void populateList( List<RecipeItem> list) {
        if (list.size() > 0)
            return;

        list.clear();
        list.add(new RecipeItem("0",
                "The Pioneer Woman's Pecan Pie",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/fullset/2012/4/27/1/WU0213H_pecan-pie_s4x3.jpg.rend.snigalleryslide.jpeg",
                "Alton Brown"));

        list.add(new RecipeItem("1",
                "Thanksgiving Turkeys",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/fullset/2014/6/10/0/GH0502_Thanksgiving-Turkeys_s4x3.jpg.rend.snigalleryslide.jpeg",
                "Ann Burrell"));

        list.add(new RecipeItem("2",
                "Spiced out sugar cookies",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/fullset/2014/6/27/1/FN_Spiced-Cut-Out-Sugar-Cookies_s4x3.jpg.rend.snigalleryslide.jpeg",
                "bobby Flay"));

        list.add(new RecipeItem("3",
                "Pumpkin Creme Brulee",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/fullset/2003/1/10/10/SD1A32_pumpkinbrulee.jpg.rend.snigalleryslide.jpeg",
                "Guy Fieri"));

        list.add(new RecipeItem("4",
                "Apple Spice Cake with Cream Cheese Icing",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/fullset/2013/7/9/0/FN_ANNE-BURRELL-APPLE-SPICE-CAKE_s4x3.jpg.rend.snigalleryslide.jpeg",
                "Rachel Ray"));
    }

    private void populateList2( List<RecipeItem> list) {
        if (list.size() > 0)
            return;

        list.clear();
        list.add(new RecipeItem("0",
                "The Pioneer Woman's Pecan Pie",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/fullset/2014/3/7/0/FN_Alton-Brown-CTK-Bio_s4x3.jpg.rend.sni6col.landscape.jpeg",
                "Alton Brown"));

        list.add(new RecipeItem("1",
                "Thanksgiving Turkeys",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/fullset/2014/11/13/1/EW0103H_Anne-Burrell_s4x3.jpg.rend.sni6col.landscape.jpeg",
                "Ann Burrell"));

        list.add(new RecipeItem("2",
                "Spiced out sugar cookies",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/secured/fullset/2015/11/18/0/QFSP02_Bobby-Flay-3-crop_s3x4.jpg.rend.sni6col.landscape.jpeg",
                "Bobby Flay"));

        list.add(new RecipeItem("3",
                "Pumpkin Creme Brulee",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/fullset/2014/12/18/0/DV_Guy-Fieri-at-Nortons_s4x3.jpg.rend.sni6col.landscape.jpeg",
                "Guy Fieri"));

        list.add(new RecipeItem("4",
                "Apple Spice Cake with Cream Cheese Icing",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/fullset/2011/1/6/1/RR_rachael-ray-peoples-choice_s4x3.jpg.rend.sni6col.landscape.jpeg",
                "Rachel Ray"));
    }

    private void populateList3( List<RecipeItem> list) {
        if (list.size() > 0)
            return;

        list.clear();


        list.add(new RecipeItem("0",
                "",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/fullset/2013/8/27/0/FN-ShowLogo-DDD_1920x1080.jpg",
                "Guy Fieri"));

        list.add(new RecipeItem("1",
                "The Pioneer Woman's Pecan Pie",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/fullset/2013/8/27/0/FN-ShowLogo-Barefoot-Contessa-1920x1080.jpg.rend.sni6col.wide.jpeg",
                ""));

        list.add(new RecipeItem("2",
                "Thanksgiving Turkeys",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/unsized/2016/10/25/0/FN_Clash-of-the-Grandmas_Showchip_1920x1080.jpg.rend.sni6col.wide.jpeg",
                ""));

        list.add(new RecipeItem("3",
                "Spiced out sugar cookies",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/unsized/2013/7/23/0/FN-T2-Show-ITKRefined-01.jpg",
                ""));


        list.add(new RecipeItem("4",
                "Apple Spice Cake with Cream Cheese Icing",
                "0",
                "http://foodnetwork.sndimg.com/content/dam/images/food/fullset/2013/8/27/0/FN-ShowLogo-Chopped-1920x1080.jpg.rend.sni6col.wide.jpeg",
                ""));
    }

    public List<RecipeItem> shuffleRecipes(int iSeed) {
        List<RecipeItem> newList = mRecipeList;
        long seed = System.nanoTime();
        if (iSeed > 0)
            seed = seed / iSeed;
        Collections.shuffle(newList, new Random(seed));

        return newList;
    }

    public static void LoadImage(Context context, String imageUrl, ImageView imageView){
        try {

            Glide.with(context)
                    .load(imageUrl)
                    .into(imageView);

        } catch (Exception e) {
            Log.d("", e.getMessage());
        }
    }
}
