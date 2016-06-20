package com.wkw.rxcache.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.wkw.rxcache.OnAddFragmentListener;

/**
 * Created by wukewei on 16/3/11.
 */
public class BaseFragment extends Fragment {

    private static final String IS_HIDDEN = "is_hidden";
    AppCompatActivity mActivity;
    private OnAddFragmentListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity =(AppCompatActivity)context;
        if (context instanceof OnAddFragmentListener) {
            listener = (OnAddFragmentListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isHidden = savedInstanceState.getBoolean(IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_HIDDEN, isHidden());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    protected void addFragment(Fragment fromFragment, Fragment toFragment) {
        if (listener != null) {
            listener.onAddFragment(fromFragment, toFragment);
        }
    }
}
