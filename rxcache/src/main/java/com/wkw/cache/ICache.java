package com.wkw.cache;

import rx.Observable;

/**
 * Created by wukewei on 16/6/19.
 */
public interface ICache {

    <T extends Object> Observable<T> get(String key, Class<T> tClass);

    <T extends Object> void put(String key, T t);

}
