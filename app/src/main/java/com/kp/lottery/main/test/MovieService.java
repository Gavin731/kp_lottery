package com.kp.lottery.main.test;

import com.kp.lottery.main.http.BaseResponse;
import com.kp.lottery.main.test.model.MovieSubject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by zenglinggui on 17/7/17.
 */

public interface MovieService {

    //获取豆瓣Top250 榜单
    @GET("top250")
    Observable<BaseResponse<MovieSubject>> getTop250(@Query("start") int start, @Query("count") int count);

    @FormUrlEncoded
    @POST("/x3/weather")
    Observable<String> getWeather(@Field("cityId") String cityId, @Field("key") String key);

}
