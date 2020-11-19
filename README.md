# Retrofit2andRxJavaNew

- Implementation
```gradle
implementation 'com.squareup.retrofit2:adapter-rxjava2:2.5.0'
implementation 'com.squareup.retrofit2:converter-gson:2.5.0'
implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
implementation 'io.reactivex.rxjava2:rxjava:2.2.9'
implementation 'com.squareup.okhttp3:logging-interceptor:3.10.0'
```

- RetroServer
```java
public class RetroServer {
    private static final String base_url = "https://newsapi.org/v2/";

    private static Retrofit setInit(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient
                .Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Authorization", "Bearer Token")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .connectTimeout(90, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build();
    }

    public static ApiService getInstance(){
        return setInit().create(ApiService.class);
    }
}
```

- Apiservice
```java
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
```

- Rxjava Flowable
```java
RetroServer.getInstance()
    .getBeritaFlowable("us", "e5430ac2a413408aaafdf60bfa27a874")
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .onErrorReturn(new Function<Throwable, Response<ResponseNews>>() {
        @Override
        public Response<ResponseNews> apply(@NonNull Throwable throwable) throws Exception {
            Log.d(TAG, "apply: " + throwable);
            return null;
        }
    })
    .subscribe(new Consumer<Response<ResponseNews>>() {
        @Override
        public void accept(Response<ResponseNews> listResponse) throws Exception {
            ResponseNews data = listResponse.body(); //json body
            int code = listResponse.code(); //200
            String msg = listResponse.message(); //SUCCESS

            Log.d(TAG, "accept: "+code);
            sentDataToAdapter(data);
        }
    });
```

- Rxjava Observable
```java
RetroServer.getInstance()
    .getBeritaObservable("us", "e5430ac2a413408aaafdf60bfa27a874")
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Observer<Response<ResponseNews>>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(TAG, "onSubscribe: Loading Show");
        }

        @Override
        public void onNext(@NonNull Response<ResponseNews> listResponse) {
            ResponseNews data = listResponse.body(); //json body
            int code = listResponse.code(); //200
            String msg = listResponse.message(); //SUCCESS
            Log.d(TAG, "onNext: "+code);
            sentDataToAdapter(data);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d(TAG, "onError: Error");
        }

        @Override
        public void onComplete() {
            Log.d(TAG, "onComplete: Loading Dismis");
        }
    });
```

---

**FullCode [AndroidManifest](https://github.com/gzeinnumer/Retrofit2andRxJavaNew/blob/master/app/src/main/AndroidManifest.xml) & [MainActivity](https://github.com/gzeinnumer/Retrofit2andRxJavaNew/blob/master/app/src/main/java/com/gzeinnumer/retrofit2andrxjavanew/MainActivity.java) & [RetroServer](https://github.com/gzeinnumer/Retrofit2andRxJavaNew/blob/master/app/src/main/java/com/gzeinnumer/retrofit2andrxjavanew/network/RetroServer.java) & [ApiService](https://github.com/gzeinnumer/Retrofit2andRxJavaNew/blob/master/app/src/main/java/com/gzeinnumer/retrofit2andrxjavanew/network/ApiService.java) & [AdapterRX](https://github.com/gzeinnumer/Retrofit2andRxJavaNew/blob/master/app/src/main/java/com/gzeinnumer/retrofit2andrxjavanew/adapter/AdapterRX.java) & [ResponseNews](https://github.com/gzeinnumer/Retrofit2andRxJavaNew/blob/master/app/src/main/java/com/gzeinnumer/retrofit2andrxjavanew/model/ResponseNews.java) & [ArticlesItem](https://github.com/gzeinnumer/Retrofit2andRxJavaNew/blob/master/app/src/main/java/com/gzeinnumer/retrofit2andrxjavanew/model/ArticlesItem.java) & [Source](https://github.com/gzeinnumer/Retrofit2andRxJavaNew/blob/master/app/src/main/java/com/gzeinnumer/retrofit2andrxjavanew/model/Source.java)**

---

```
Copyright 2020 M. Fadli Zein
```