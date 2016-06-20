package com.wkw.cache;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
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
public class DiskCache implements ICache{

    private static final String NAME = ".db";
    public static long CACHE_TIME = 1 * 60 * 1000;
    File fileDir;
    public DiskCache() {
        fileDir = CacheLoader.getApplication().getCacheDir();
    }

    @Override
    public <T> Observable<T> get(final String key, final Class<T> cls) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {

                String diskData = getDiskData(key + NAME);

                if (subscriber.isUnsubscribed()) {
                    return;
                }

                if (diskData == null || TextUtils.isEmpty(diskData)) {
                    subscriber.onNext(null);
                } else {
                    T t = new Gson().fromJson(diskData, cls);
                    subscriber.onNext(t);
                }

                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public <T> void put(final String key, final T t) {
        Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {

                save(key + NAME, new Gson().toJson(t));

                if (!subscriber.isUnsubscribed()) {

                    subscriber.onNext(t);
                    subscriber.onCompleted();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
    /**
     * 保存数据
     */
    private void save(String fileName, String data) {
        File dataFile = new File(fileDir, fileName);
        try {
            if (!dataFile.exists()) {
                try {
                    dataFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Writer writer = new FileWriter(dataFile);
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取保存的数据
     */
    private  String getDiskData(String fileName) {
        File dataFile = new File(fileDir, fileName);

        if (isCacheDataFailure(dataFile)) {
            return null;
        }

        if (!dataFile.exists()) {
           return null;
        }

        try {
            Reader reader = new FileReader(dataFile);
            BufferedReader r = new BufferedReader(reader);
            StringBuilder b = new StringBuilder();
            String line;
            while((line = r.readLine())!=null) {
                b.append(line);
                b.append("\r\n");
            }

            return  b.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断缓存是否已经失效
     */
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
