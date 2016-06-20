package com.wkw.rxcache.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wkw.rxcache.R;

/**
 * Created by wukewei on 16/6/20.
 */
public class MainFragment extends BaseFragment {

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main,container,false);
        init();
        return mView;
    }

    private void init() {
        mView.findViewById(R.id.concat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(MainFragment.this, ConcatFragment.newInstance());
            }
        });
        mView.findViewById(R.id.behavior_subject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(MainFragment.this, BehaviorSubjectFragment.newInstance());
            }
        });
    }
}
