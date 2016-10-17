package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.EquipmentType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IEquipmentType {
    @GET("/api/references/equipment_type")
    Call<List<EquipmentType>> equipmentType(@Header("Authorization") String token,
                                            @Query("ChangedAfter") String changedAfter);

}