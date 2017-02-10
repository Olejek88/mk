package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.TaskVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */
public interface ITaskVerdict {
    @GET("/references/task-verdict")
    Call<List<TaskVerdict>> taskVerdict(@Header("Authorization") String token);

    @GET("/references/task-verdict")
    Call<List<TaskVerdict>> taskVerdict(@Header("Authorization") String token,
                                        @Query("changedAfter") String changedAfter);

    @GET("/references/task-verdict")
    Call<List<TaskVerdict>> taskVerdictById(@Header("Authorization") String token,
                                            @Query("id") String id);
}
