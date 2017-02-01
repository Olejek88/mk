package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.OrderStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOrderStatus {
    @GET("/references/order-status")
    Call<List<OrderStatus>> orderStatus(@Header("Authorization") String token);

    @GET("/references/order-status")
    Call<List<OrderStatus>> orderStatus(@Header("Authorization") String token,
                                        @Query("changedAfter") String changedAfter);

    @GET("/references/order-status")
    Call<List<OrderStatus>> orderStatusById(@Header("Authorization") String token,
                                            @Query("id") String id);

}
