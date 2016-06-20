package com.wkw.cache;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wukewei on 16/6/19.
 */
public class DiskCache implements ICache {

    private static final String NAME = ".db";
    public static long CACHE_TIME = 60 * 1000;
    File fileDir;
    public DiskCache() {
        fileDir = CacheLoader.getApplication().getCacheDir();
    }

    @Override
    public <T> Observable<T> get(final String key, final Class<T> tClass) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                String data = getDataString(key + NAME);
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                if (TextUtils.isEmpty(data)) {
                    subscriber.onNext(null);
                } else {
                    subscriber.onNext(new Gson().fromJson(data, tClass));
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public <T> void put(final String key, final T t) {
        Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                save(key + NAME, t.toString());
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe();
    }




    private void save(String fileName, String data) {
        File file = new File(fileDir, fileName);
        try {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Writer writer = new FileWriter(file);
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDataString(String fileName) {
        File file = new File(fileDir, fileName);
        if (isCacheDataFailure(file)) {
            return null;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Reader reader = new FileReader(file);
            return reader.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    public  boolean isCacheDataFailure(File dataFile) {
        if (!dataFile.exists()) {
            return false;
        }
        long existTime = System.currentTimeMillis() - dataFile.lastModified();
        boolean failure = false;
        failure = existTime > CACHE_TIME ? true : false;
        return failure;
    }

    public void clearDisk(String key) {
        File file = new File(fileDir, key + NAME);
        if (file.exists()) file.delete();
    }
}
