package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.ToolType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IToolType {
    @GET("/references/tool-type")
    Call<List<ToolType>> toolType();

    @GET("/references/tool-type")
    Call<List<ToolType>> toolType(@Query("changedAfter") String changedAfter);

    @GET("/references/tool-type")
    Call<List<ToolType>> toolTypeById(@Query("id") String id);

}