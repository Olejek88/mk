package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.Equipment;

/**
 * @author Dmitriy Logachev
 * Created by koputo on 25.01.17.
 */

public interface IEquipment {
    @GET("/references/equipment")
    Call<List<Equipment>> equipment();

    @GET("/references/equipment")
    Call<List<Equipment>> equipment(@Query("changedAfter") String changedAfter);

    @GET("/references/equipment")
    Call<List<Equipment>> equipmentById(@Query("id") String id);
}