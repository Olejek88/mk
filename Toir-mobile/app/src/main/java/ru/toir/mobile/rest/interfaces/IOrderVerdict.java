package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.OrderVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOrderVerdict {
    @GET("/references/order-verdict")
    Call<List<OrderVerdict>> orderVerdict();

    @GET("/references/order-verdict")
    Call<List<OrderVerdict>> orderVerdict(@Query("changedAfter") String changedAfter);

    @GET("/references/order-verdict")
    Call<List<OrderVerdict>> orderVerdictById(@Query("id") String id);
}