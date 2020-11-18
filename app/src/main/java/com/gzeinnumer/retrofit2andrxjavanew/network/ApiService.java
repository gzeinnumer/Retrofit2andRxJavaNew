package com.gzeinnumer.retrofit2andrxjavanew.network;


import com.gzeinnumer.retrofit2andrxjavanew.model.ResponseNews;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import io.reactivex.Observable;

public interface ApiService {
    //rx-java-type-1
    //?country=us&apiKey=e5430ac2a413408aaafdf60bfa27a874
    @GET("top-headlines")
    Observable<Response<ResponseNews>> getBeritaObservable(
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );

    //rx-java-type-2
    //?country=us&apiKey=e5430ac2a413408aaafdf60bfa27a874
    @GET("top-headlines")
    Flowable<Response<ResponseNews>> getBeritaFlowable(
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );
}
