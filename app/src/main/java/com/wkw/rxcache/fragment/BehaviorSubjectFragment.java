package com.wkw.rxcache.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wkw.cache.NetworkCache;
import com.wkw.rxcache.R;
import com.wkw.rxcache.bean.Person;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by wukewei on 16/6/20.
 */
public class BehaviorSubjectFragment extends BaseFragment{

    public static BehaviorSubjectFragment newInstance() {
        BehaviorSubjectFragment fragment = new BehaviorSubjectFragment();
        return fragment;
    }


    View mView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_content,container,false);
        return mView;
    }




}
