package com.ad.aggregator.app.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.ad.aggregator.app.model.DummyContent;

import java.util.List;

/**
 * Created by dbarone on 5/2/2017.
 */

public class ListDiffCallback extends DiffUtil.Callback {
    private List<DummyContent.DummyItem> mOldList;
    private List<DummyContent.DummyItem> mNewList;

    public ListDiffCallback(List<DummyContent.DummyItem> oldList, List<DummyContent.DummyItem> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList != null ? mOldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewList != null ? mNewList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewList.get(newItemPosition).id == mOldList.get(oldItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewList.get(newItemPosition).equals(mOldList.get(oldItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //DummyContent.DummyItem newProduct = mNewList.get(newItemPosition);
        //DummyContent.DummyItem oldProduct = mOldList.get(oldItemPosition);

        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
