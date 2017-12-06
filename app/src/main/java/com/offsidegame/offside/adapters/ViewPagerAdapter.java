package com.offsidegame.offside.adapters;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.offsidegame.offside.R;

import org.acra.ACRA;

import java.util.ArrayList;


/**
 * Created by user on 8/22/2017.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private final ArrayList<String> mFragmentTitleList = new ArrayList<>();
    Context context;
    ViewPager viewPager;
    TabLayout tabLayout;
    ArrayList<View> tabViews = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager, Context context, ViewPager viewPager,
                            TabLayout tabLayout) {
        super(manager);
        this.context = context;
        this.viewPager = viewPager;
        this.tabLayout = tabLayout;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        try
        {
            if (mFragmentList.contains(fragment))
                return;
            if (isTabFragmentExist(title))
                return;
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
        }

    }

    public boolean isTabFragmentExist(String title) {
        try
        {
            boolean tabFragmentExist = false;
            for (String tabTitle : mFragmentTitleList) {
                if (tabTitle.equals(title)) {
                    tabFragmentExist = true;
                    break;
                }

            }
            return tabFragmentExist;


        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                    return false;
        }


    }

    public void removeFrag(int position) {
        //removeTab(position);
        Fragment fragment = mFragmentList.get(position);
        mFragmentList.remove(fragment);
        mFragmentTitleList.remove(position);
        destroyFragmentView(viewPager, position, fragment);
        notifyDataSetChanged();
    }

    public View getTabView(final int position) {
        try
        {
            View view = LayoutInflater.from(context).inflate(R.layout.custom_tab_item, null);

            TextView tabItemName = (TextView) view.findViewById(R.id.textViewTabItemName);

            tabItemName.setText(mFragmentTitleList.get(position));

            tabItemName.setTextColor(context.getResources().getColor(R.color.navigationMenuSelectedItem));

            return view;


        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);

                    return null;
        }

    }



    public void destroyFragmentView(ViewGroup container, int position, Object object) {
        FragmentManager manager = ((Fragment) object).getFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove((Fragment) object);
        trans.commit();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
