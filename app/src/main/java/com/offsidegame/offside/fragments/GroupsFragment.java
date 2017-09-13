package com.offsidegame.offside.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.activities.CreatePrivateGroupActivity;
import com.offsidegame.offside.adapters.ViewPagerAdapter;
import com.offsidegame.offside.events.NavigationEvent;
import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PrivateGroup;
import com.offsidegame.offside.models.PrivateGroupsInfo;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by user on 8/22/2017.
 */


public class GroupsFragment extends Fragment {
    private LinearLayout groupsRoot;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private FrameLayout loadingRoot;
    private TextView versionTextView;
    private TextView createPrivateGroupButtonTextView;
    private String playerId;


    public static GroupsFragment newInstance(){
        GroupsFragment groupsFragment = new GroupsFragment();

        return groupsFragment;
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBus.getDefault().register(this);
        playerId = OffsideApplication.getPlayerId();
        OffsideApplication.signalRService.requestPrivateGroupsInfo(playerId);
    }

    @Override
    public void onPause(){
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        getIDs(view);
        setEvents();

        resetVisibility();

        versionTextView.setText(OffsideApplication.getVersion() == null ? "0.0" : OffsideApplication.getVersion());


        return view;
    }

    private void getIDs(View view) {

        groupsRoot = (LinearLayout) view.findViewById(R.id.fg_groups_root);
        loadingRoot = (FrameLayout) view.findViewById(R.id.shared_loading_root);
        versionTextView = (TextView) view.findViewById(R.id.shared_version_text_view);

        viewPager = (ViewPager) view.findViewById(R.id.fg_tabs_container_view_pager);
        tabLayout = (TabLayout) view.findViewById(R.id.fg_groups_tab_layout);

        viewPagerAdapter = new ViewPagerAdapter(getFragmentManager(), getActivity(), viewPager, tabLayout);
        viewPager.setAdapter(viewPagerAdapter);

        createPrivateGroupButtonTextView = (TextView) view.findViewById(R.id.fg_create_private_group_button_text_view);
    }

    int selectedTabPosition;

    private void setEvents() {

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                viewPager.setCurrentItem(tab.getPosition());
                selectedTabPosition = viewPager.getCurrentItem();
                Log.d("Selected", "Selected " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                Log.d("Unselected", "Unselected " + tab.getPosition());
            }
        });

        createPrivateGroupButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), CreatePrivateGroupActivity.class);
                startActivity(intent);

            }
        });


    }

    public void resetVisibility() {

        loadingRoot.setVisibility(View.VISIBLE);
        groupsRoot.setVisibility(View.VISIBLE);

    }


    public void addPage(String groupType) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.key_group_type), groupType);
        PrivateGroupsFragment privateGroupsFragment = new PrivateGroupsFragment();
        privateGroupsFragment.setArguments(bundle);
        viewPagerAdapter.addFragment(privateGroupsFragment, getPageTitle(groupType));
        viewPagerAdapter.notifyDataSetChanged();
        if (viewPagerAdapter.getCount() > 0) tabLayout.setupWithViewPager(viewPager);


        setupTabLayout();
    }

    private String getPageTitle(String groupType) {
        String title = "Unknown";
        if (groupType.equals(getString(R.string.key_private_group_name)))
            title = getString(R.string.lbl_my_private_groups);
        else if (groupType.equals(getString(R.string.key_public_group_name)))
            title = getString(R.string.lbl_public_groups);
        return title;

    }

    public void setupTabLayout() {
        selectedTabPosition = viewPager.getCurrentItem();
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setCustomView(viewPagerAdapter.getTabView(i));
        }
    }

    public boolean isTabsCreated() {
        return viewPagerAdapter != null && viewPagerAdapter.getCount() > 0;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePrivateGroupsInfo(PrivateGroupsInfo privateGroupsInfo) {
        try {

            if (privateGroupsInfo != null && privateGroupsInfo.getPrivateGroups() != null) {
                OffsideApplication.setPrivateGroupsInfo(privateGroupsInfo);

                addPagesToGroupsFragment();
                viewPager.setCurrentItem(0);



            }

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    public void addPagesToGroupsFragment() {

        try {
            if (!isTabsCreated()) {
                addPage(getString(R.string.key_private_group_name));
                addPage(getString(R.string.key_public_group_name));
            }

            loadingRoot.setVisibility(View.GONE);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }
}
