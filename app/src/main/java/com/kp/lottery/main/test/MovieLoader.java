package com.kp.lottery.main.test;

import com.kp.lottery.main.http.BaseResponse;
import com.kp.lottery.main.http.ObjectLoader;
import com.kp.lottery.main.http.PayLoad;
import com.kp.lottery.main.http.RetrofitServiceManager;
import com.kp.lottery.main.test.model.Movie;
import com.kp.lottery.main.test.model.MovieSubject;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by zenglinggui on 17/7/23.
 */

public class MovieLoader extends ObjectLoader {

    private MovieService mMovieService;

    public MovieLoader() {
        mMovieService = RetrofitServiceManager.getInstance().create(MovieService.class);
    }

    /**
     * 获取电影列表
     *
     * @param start
     * @param count
     * @return
     */
//    public Observable<List<Movie>> getMovie(int start, int count) {
//        return observe(mMovieService.getTop250(start, count))
//                .map(new Func1<MovieSubject, List<Movie>>() {
//                    @Override
//                    public List<Movie> call(MovieSubject movieSubject) {
//                        return movieSubject.subjects;
//                    }
//                });
//    }
    public Observable<MovieSubject> getMovie(int start, int count) {
        return observe(mMovieService.getTop250(start, count))
                .map(new PayLoad<MovieSubject>());
    }

    public Observable<String> getWeatherList(String cityId, String key) {
        return observe(mMovieService.getWeather(cityId, key)).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return null;
            }
        });
    }
}
