package com.ad.aggregator.app;

import com.ad.aggregator.app.dummy.DummyContent;

/**
 * Created by hal on 11/8/2016.
 */

public class RecipeItem extends DummyContent.DummyItem {

    public final String image;
    public final String owner;

    public RecipeItem(String id, String content, String details, String image, String owner) {
        super(id, content, details);
        this.image = image;
        this.owner = owner;
    }
}
