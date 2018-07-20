package com.dfbarone.cachinghttp.persistence.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by dominicbarone on 11/6/17.
 */

@Dao
public interface HttpResponseDao {

    @Query("SELECT * FROM http_response")
    List<HttpResponseEntry> selectAll();

    @Query("SELECT * FROM http_response where url = :arg0")
    HttpResponseEntry selectResponseByUrl(String arg0);

    @Query("SELECT * FROM http_response where url = :arg0")
    Single<HttpResponseEntry> selectResponseByUrlSingle(String arg0);

    @Query("SELECT * FROM http_response where url = :arg0")
    Flowable<HttpResponseEntry> selectResponseByUrlFlowable(String arg0);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HttpResponseEntry httpResponse);

    @Delete
    void delete(HttpResponseEntry response);
}
