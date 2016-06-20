package com.wkw.cache;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by wukewei on 16/6/19.
 */
public class CacheLoader {

    private static Application application;

    public static Application getApplication() {
        return application;
    }

    private ICache mMemoryCache;
    private ICache mDiskCache;


    private static CacheLoader loader;

    public static CacheLoader getInstance(Context context) {

        application = (Application) context.getApplicationContext();
        if (loader == null) {
            synchronized (CacheLoader.class) {
                if (loader == null) {
                    loader = new CacheLoader();
                }
            }
        }
        return loader;
    }

    public CacheLoader() {
        mDiskCache = new DiskCache();
        mMemoryCache = new MemoryCache();
    }


    public <T> Observable<T> asDataObservable(String key, Class<T> cls, NetworkCache<T> networkCache) {
        Observable observable = Observable.concat(loadMemory(key, cls)
        , loadDisk(key, cls), loadNetWork(key, cls ,networkCache))
                .first(new Func1<T, Boolean>() {
                    @Override
                    public Boolean call(T t) {
                        return t != null;
                    }
                });
        return observable;
    }


    private <T> Observable<T> loadMemory(String key, Class<T> cls) {
       return mMemoryCache.get(key, cls).doOnNext(new Action1<T>() {
           @Override
           public void call(T t) {
               if (null != t) {
                   Log.d("RxCache","I am from Memory");
               }
           }
       });
    }

    private <T> Observable<T> loadDisk(final String key, final Class<T> cls) {
        return mDiskCache.get(key, cls)
                .doOnNext(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        if(null != t) {
                            Log.d("RxCache","I am from Disk");
                            mMemoryCache.put(key, cls);
                        }
                    }
                });
    }


    private <T> Observable<T> loadNetWork(final String key, Class<T> cls, NetworkCache<T> networkCache) {
        return networkCache.get(key, cls)
                .doOnNext(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        if (null != t) {
                            Log.d("RxCache","I am from NetWork");
                            mMemoryCache.put(key, t);
                            mDiskCache.put(key, t);
                        }
                    }
                });
    }


    private void clearMemory(String key) {
        ((MemoryCache)mMemoryCache).clearMemory(key);
    }


    private void clearMemoryDisk(String key) {
        ((MemoryCache)mMemoryCache).clearMemory(key);
        ((DiskCache)mDiskCache).clearDisk(key);
    }

}
