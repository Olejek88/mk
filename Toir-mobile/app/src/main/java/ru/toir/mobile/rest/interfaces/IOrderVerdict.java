package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.OrderVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOrderVerdict {
    @GET("/order-verdict")
    Call<List<OrderVerdict>> get();

    @GET("/order-verdict")
    Call<List<OrderVerdict>> get(@Query("changedAfter") String changedAfter);

    @GET("/order-verdict")
    Call<List<OrderVerdict>> getById(@Query("id") String id);

    @GET("/order-verdict")
    Call<List<OrderVerdict>> getById(@Query("id[]") String[] id);

    @GET("/order-verdict")
    Call<List<OrderVerdict>> getByUuid(@Query("uuid") String uuid);

    @GET("/order-verdict")
    Call<List<OrderVerdict>> getByUuid(@Query("uuid[]") String[] uuid);
}

