package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.RepairPartType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IRepairPartType {
    @GET("/references/repair-part-type")
    Call<List<RepairPartType>> repairPartType();

    @GET("/references/repair-part-type")
    Call<List<RepairPartType>> repairPartType(@Query("changedAfter") String changedAfter);

    @GET("/references/repair-part-type")
    Call<List<RepairPartType>> repairPartTypeById(@Query("id") String id);
}