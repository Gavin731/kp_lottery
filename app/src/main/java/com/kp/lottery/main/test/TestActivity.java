package com.kp.lottery.main.test;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kp.lottery.kplib.app.KPActivity;
import com.kp.lottery.kplib.log.KPLog;
import com.kp.lottery.main.test.model.Movie;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Action1;

/**
 * Created by zenglinggui on 17/7/17.
 */

public class TestActivity extends KPActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        normalRequest();
        getMovieList();

    }

    private void getMovieList(){
        MovieLoader movieLoader=new MovieLoader();
        movieLoader.getMovie(0,10).subscribe(new Action1<List<Movie>>() {
            @Override
            public void call(List<Movie> movies) {
                KPLog.e("---getMovieList response："+movies);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                KPLog.e("---getMovieList error："+throwable.getMessage());
            }
        });
    }

    private void normalRequest() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.douban.com/v2/movie/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        //获取接口实例
//        MovieService movieService = retrofit.create(MovieService.class);
//        //调用方法得到一个Call
//        Call<MovieSubject> call = movieService.getTop250(1, 10);
//        //进行网络请求
//        call.enqueue(new Callback<MovieSubject>() {
//            @Override
//            public void onResponse(Call<MovieSubject> call, Response<MovieSubject> response) {
//                KPLog.e("-----" + GsonConverter.toJson(response));
//            }
//
//            @Override
//            public void onFailure(Call<MovieSubject> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });

        //同步
//        try {
//            Response<String> response = call.execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
