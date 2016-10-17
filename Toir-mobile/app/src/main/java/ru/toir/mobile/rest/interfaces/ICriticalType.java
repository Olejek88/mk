package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.CriticalType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 15.09.16.
 */
public interface ICriticalType {
    @GET("/api/references/critical_type")
    Call<List<CriticalType>> criticalType(@Header("Authorization") String token,
                                          @Query("ChangedAfter") String changedAfter);
}