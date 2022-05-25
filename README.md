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

# Gofo Style

```java
import io.reactivex.Flowable;
import retrofit2.Response;
import retrofit2.http.GET;

public interface ApiService {

    @GET("master/all/{id_dev_unit}")
    Flowable<Response<BaseObjectResponse<MasterResponse>>> getMaster(@Path("id_dev_unit") String id_dev_unit);
    
}
```

```java
import android.net.ConnectivityManager;

import androidx.lifecycle.MutableLiveData;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UnitsRepoImpl implements UnitsRepo {

//    private final UnitsRepoImpl repoUnits;
//    repoUnits = new UnitsRepoImpl(application.getApplicationContext());

    private final Context context;
    private final CompositeDisposable compositeDisposable;
    private final ConnectivityManager cm;
    private final ApiService apiService;

    public UnitsRepoImpl(Context applicationContext) {
        this.context = applicationContext;
        this.compositeDisposable = new CompositeDisposable();
        this.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.apiService = RetroServer.getInstance(applicationContext);
        master = new MutableLiveData<>();
    }

    private boolean isConnect() {
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private MutableLiveData<BaseResource<MasterResponse>> master;

    public void setMaster(String id) {
        master.postValue(BaseResource.loading());

        if (isConnect()){
            compositeDisposable.add(
                    apiService.getMaster(id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                int code = response.code();
                                BaseObjectResponse<MasterResponse> res = response.body();
                                if (res.getStatus().equals(BaseConstant.RES_SUCCESS)) {
                                    MasterResponse list = response.body().getData();
                                    master.postValue(BaseResource.success(res.getTitle(), res.getMessage(), list, res.getInfo().getTotal()));
                                } else if(res.getStatus().equals(BaseConstant.RES_TC)) {
                                    master.postValue(BaseResource.error(res.getTitle(), BaseConstant.RES_TC_MSG));
                                } else {
                                    String msg = GblFunction.msgDebugOrRelease(response.toString()+"\n\n"+res.getMessage(), res.getMessage());
                                    master.postValue(BaseResource.error(res.getTitle(), msg));
                                }
                            }, throwable -> {
                                String msg = GblFunction.msgDebugOrRelease(throwable.getMessage(), BaseConstant.RES_TC_MSG);
                                master.postValue(BaseResource.error(BaseConstant.RES_TC_MSG_TITLE,msg));
                            })
            );
        } else {
            master.postValue(BaseResource.error(BaseConstant.RES_TC_MSG_TITLE, BaseConstant.RES_TC_MSG));
        }
    }

    public MutableLiveData<BaseResource<MasterResponse>> getMaster() {
        return master;
    }
}
```

```java
public class MasterVM extends AndroidViewModel {
    private final UnitsRepoImpl repoUnits;
    private final LiveData<BaseResource<MasterResponse>> master;
    SessionManager sessionManager;

    public MasterVM(Application application) {
        super(application);

        repoUnits = new UnitsRepoImpl(application.getApplicationContext());
        master = repoUnits.getMaster();
    }

    public void setMaster() {
        repoUnits.setMaster(sessionManager.getLogin().getData().getIdDevUnit()+"");
    }

    public LiveData<BaseResource<MasterResponse>> getMaster() {
        return master;
    }
}
```
```java
vm.setMaster();
vm.getMaster().observe(this, resource -> {
    switch (resource.status) {
        case STATUS_1_SUCCESS:
            onHideLoading();
            processDataToView(resource.data);
            break;
        case STATUS_2_ERROR:
            onHideLoading();
            onShowInfoDialogError(resource.title, resource.message);
            break;
        case STATUS_6_LOADING:
            onShowLoading();
            break;
    }
});
```

---

```
Copyright 2020 M. Fadli Zein
```
