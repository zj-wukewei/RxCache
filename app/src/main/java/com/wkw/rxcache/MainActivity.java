package com.wkw.rxcache;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.wkw.rxcache.fragment.MainFragment;

public class MainActivity extends AppCompatActivity implements OnAddFragmentListener {



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(savedInstanceState);

    }
    private void init(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            MainFragment loginFragment = MainFragment.newInstance();
            loadFragment(loginFragment);
        }
    }

    private void addFragment(Fragment formFragment, Fragment toFragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container,toFragment,toFragment.getClass().getName())
                .show(toFragment)
                .hide(formFragment)
                .addToBackStack(toFragment.getClass().getName())
                .commit();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment,fragment.getClass().getName())
                .show(fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onAddFragment(Fragment fromFragment, Fragment toFragment) {
        addFragment(fromFragment, toFragment);
    }
}
