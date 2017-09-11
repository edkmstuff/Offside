package com.offsidegame.offside.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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

        String title = fragments.get(position).toString();

//        String groupType = fragments.get(position).getArguments().getString("groupType");
//        String title = "לא ידוע";
//        if (groupType == null)
//            return title;
//
//        if (groupType.equals("PRIVATE_GROUP"))
//            title = "הקבוצות שלי";
//        else if (groupType.equals("PUBLIC_GROUP"))
//            title = "קבוצות ציבוריות";

        return title;


    }


}
