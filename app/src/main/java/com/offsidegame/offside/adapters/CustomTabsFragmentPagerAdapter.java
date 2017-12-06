package com.offsidegame.offside.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.acra.ACRA;

import java.util.ArrayList;

/**
 * Created by user on 7/20/2017.
 */

public class CustomTabsFragmentPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();

    public CustomTabsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    //add page
    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }
    //set title

    @Override
    public CharSequence getPageTitle(int position) {

        try
        {
            String title = fragments.get(position).toString();

            return title;


        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                    return null;
        }


    }


}
