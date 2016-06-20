package com.wkw.rxcache.fragment;

import android.os.Bundle;
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
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by wukewei on 16/6/20.
 */
public class ConcatFragment extends BaseFragment{

    private String key = "ConcatFragment";

    NetworkCache<Person> networkCache;
    List<String> data = new ArrayList<>();
    Observable<Person> observable;

    public static ConcatFragment newInstance() {
        ConcatFragment fragment = new ConcatFragment();
        return fragment;
    }


    View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_content,container,false);
        init();
        return mView;
    }

    private void init() {

        for(int i =0; i < 20; i ++) {
            data.add("wukewei"+i);
        }

        networkCache = new NetworkCache<Person>() {
        @Override
        public Observable<Person> get(String key, Class<Person> cls) {
            return Observable.just(data)
                    .flatMap(new Func1<List<String>, Observable<Person>>() {
                        @Override
                        public Observable<Person> call(List<String> strings) {
                            Person person =  new Person();
                            person .data = strings;
                            return Observable.just(person) ;
                        }
                    });
        }
    };
    observable = CacheLoader.getInstance(mActivity).asDataObservable(key, Person.class, networkCache);

    mView.findViewById(R.id.get).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            observable.subscribe(new Action1<Person>() {
                @Override
                public void call(Person person) {
                    Log.d("aaaaa",person.data.size()+"");
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {

                }
            });

        }
    });

    mView.findViewById(R.id.memory).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CacheLoader.getInstance(mActivity).clearMemory(key);
        }
    });
    mView.findViewById(R.id.memory_disk).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CacheLoader.getInstance(mActivity).clearMemoryDisk(key);
        }
    });

    }



}
