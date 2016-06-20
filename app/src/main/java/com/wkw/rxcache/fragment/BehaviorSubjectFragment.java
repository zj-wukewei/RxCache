package com.wkw.rxcache.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wkw.cache.CacheLoader;
import com.wkw.cache.NetworkCache;
import com.wkw.rxcache.R;
import com.wkw.rxcache.bean.Person;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * Created by wukewei on 16/6/20.
 */
public class BehaviorSubjectFragment extends BaseFragment{

    public static BehaviorSubjectFragment newInstance() {
        BehaviorSubjectFragment fragment = new BehaviorSubjectFragment();
        return fragment;
    }

    String diskData = null;
    String  networkData = "从服务器获取的数据";

    BehaviorSubject<String> cache;

    View mView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_content,container,false);
        init();
        return mView;
    }

    private void init() {
        mView.findViewById(R.id.get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscriptionData(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.d("onNext", s);
                    }
                });
            }
        });

        mView.findViewById(R.id.memory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BehaviorSubjectFragment.this.cache = null;
            }
        });

        mView.findViewById(R.id.memory_disk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BehaviorSubjectFragment.this.cache = null;
                BehaviorSubjectFragment.this.diskData = null;
            }
        });

    }

    private void loadNewWork() {
        Observable<String> o = Observable.just(networkData)
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        BehaviorSubjectFragment.this.diskData = s;
                        Log.d("写入磁盘","写入磁盘");
                    }
                });
        o.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                cache.onNext(s);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        });
    }

    private Subscription subscriptionData(@NonNull Observer<String> observer) {
        if (cache == null) {
            cache = BehaviorSubject.create();
            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    String data = diskData;
                    if (data == null) {
                        Log.d("来自网络","来自网络");
                        loadNewWork();
                    } else {
                        Log.d("来自磁盘","来自磁盘");
                        subscriber.onNext(data);
                    }
                }
            })
                    .subscribeOn(Schedulers.io())
                    .subscribe(cache);

        } else {
            Log.d("来自内存","来自内存");
        }

        return cache.observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }


}
