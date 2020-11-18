package com.gzeinnumer.retrofit2andrxjavanew;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.gzeinnumer.retrofit2andrxjavanew.adapter.AdapterRX;
import com.gzeinnumer.retrofit2andrxjavanew.model.ResponseNews;
import com.gzeinnumer.retrofit2andrxjavanew.network.RetroServer;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    RecyclerView recyclerView;
    AdapterRX adapterRX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv);

        typeFlowable();
//        typeObservable();
    }

    @SuppressLint("CheckResult")
    private void typeFlowable() {
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
    }

    @SuppressLint("CheckResult")
    private void typeObservable() {
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
//                        Log.d(TAG, "onNext: "+data.toString());
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
    }

    private void sentDataToAdapter(ResponseNews responseNews) {
        adapterRX = new AdapterRX(getApplicationContext(), responseNews.getArticles());
        recyclerView.setAdapter(adapterRX);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
