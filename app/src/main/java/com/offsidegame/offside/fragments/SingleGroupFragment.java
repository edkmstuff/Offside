package com.offsidegame.offside.fragments;


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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.adapters.LeagueAdapter;
import com.offsidegame.offside.adapters.ViewPagerAdapter;
import com.offsidegame.offside.events.NavigationEvent;
import com.offsidegame.offside.events.PrivateGameGeneratedEvent;
import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.LeagueRecord;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PrivateGroup;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 8/22/2017.
 */


public class SingleGroupFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;


    private ViewPagerAdapter viewPagerAdapter;
    private FrameLayout loadingRoot;
    private TextView versionTextView;

    private LinearLayout singlePrivateGroupRoot;
    private LinearLayout singleGroupGamesTabRoot;
    private LinearLayout singleGroupLeagueTabRoot;
    private LinearLayout singleGroupGamesRoot;
    private LinearLayout singleGroupLeagueRoot;
    private TextView singleGroupPositionOutOfTextView;
    private ListView singleGroupLeagueListView;
    private TextView groupNavigationGroupNameTextView;
//    private TextView groupNavigationLeftButtonTextView;
//    private TextView groupNavigationRightButtonTextView;
    private ImageView groupNavigationLeftButtonImageView;
    private ImageView groupNavigationRightButtonImageView;
    private TextView groupNavigationLastPlayedTextView;





    private int currentGroupSelectedIndex = -1;
    private int groupsCount = -1;


    public static SingleGroupFragment newInstance() {
        SingleGroupFragment singleGroupFragment = new SingleGroupFragment();

        return singleGroupFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        currentGroupSelectedIndex = OffsideApplication.getPrivateGroupsInfo().getPrivateGroups().indexOf(OffsideApplication.getSelectedPrivateGroup());
        groupsCount = OffsideApplication.getPrivateGroupsInfo().getPrivateGroups().size();

        navigateGroup(0);

    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_single_group, container, false);
            getIDs(view);
            setEvents();

            versionTextView.setText(OffsideApplication.getVersion());
            return view;

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
            return null;
        }

    }

    private void getIDs(View view) {

        loadingRoot = (FrameLayout) view.findViewById(R.id.shared_loading_root);
        versionTextView = (TextView) view.findViewById(R.id.shared_version_text_view);
        //leagues
        viewPager = (ViewPager) view.findViewById(R.id.fsg_leagues_pages_view_pager);
        tabLayout = (TabLayout) view.findViewById(R.id.fsg_leagues_selection_tab_layout);

        viewPagerAdapter = new ViewPagerAdapter(getFragmentManager(), getActivity(), viewPager, tabLayout);
        viewPager.setAdapter(viewPagerAdapter);

        singlePrivateGroupRoot = (LinearLayout) view.findViewById(R.id.fsg_single_group_root);
        singleGroupGamesTabRoot = (LinearLayout) view.findViewById(R.id.fsg_single_group_games_tab_root);
        singleGroupLeagueTabRoot = (LinearLayout) view.findViewById(R.id.fsg_single_group_league_tab_root);
        singleGroupGamesRoot = (LinearLayout) view.findViewById(R.id.fsg_single_group_games_root);
        singleGroupLeagueRoot = (LinearLayout) view.findViewById(R.id.fsg_single_group_league_root);
        singleGroupPositionOutOfTextView = (TextView) view.findViewById(R.id.fsg_single_group_position_out_of_text_view);
        singleGroupLeagueListView = (ListView) view.findViewById(R.id.fsg_single_group_league_list_view);
        groupNavigationGroupNameTextView = (TextView) view.findViewById(R.id.fsg_group_navigation_group_name_text_view);
//        groupNavigationLeftButtonTextView = (TextView) view.findViewById(R.id.fsg_group_navigation_left_button_text_view);
//        groupNavigationRightButtonTextView = (TextView) view.findViewById(R.id.fsg_group_navigation_right_button_text_view);
        groupNavigationLeftButtonImageView = (ImageView) view.findViewById(R.id.fsg_group_navigation_left_button_image_view);
        groupNavigationRightButtonImageView = (ImageView) view.findViewById(R.id.fsg_group_navigation_right_button_image_view);
        groupNavigationLastPlayedTextView = (TextView) view.findViewById(R.id.fsg_group_navigation_last_played_text_view);
        singleGroupPositionOutOfTextView = (TextView) view.findViewById(R.id.fsg_single_group_position_out_of_text_view);
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

        singleGroupGamesTabRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleGroupTabSwitched(view);
                singleGroupGamesTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);
                singleGroupLeagueTabRoot.setBackgroundResource(R.color.navigationMenu);
            }
        });

        singleGroupLeagueTabRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleGroupGamesTabRoot.setBackgroundResource(R.color.navigationMenu);
                singleGroupLeagueTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);
                singleGroupTabSwitched(view);
            }
        });

        groupNavigationLeftButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateGroup(-1);
            }
        });

        groupNavigationRightButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateGroup(1);
            }
        });
    }

    private void navigateGroup(int step) {

        try {
            currentGroupSelectedIndex = currentGroupSelectedIndex + step;
            int newSelectedGroupIndex = currentGroupSelectedIndex % groupsCount;
            if (newSelectedGroupIndex < 0)
                newSelectedGroupIndex = newSelectedGroupIndex + groupsCount;
            // this is just to avoid negative indexes
            currentGroupSelectedIndex = newSelectedGroupIndex;
            PrivateGroup newSelectedGroup = OffsideApplication.getPrivateGroupsInfo().getPrivateGroups().get(newSelectedGroupIndex);
            if(step!=0)
                OffsideApplication.setSelectedPrivateGroup(newSelectedGroup);

            groupNavigationGroupNameTextView.setText(OffsideApplication.getSelectedPrivateGroup().getName());
            groupNavigationLastPlayedTextView.setText(OffsideApplication.getSelectedPrivateGroup().getPrettyLastPlayed());

            OffsideApplication.signalRService.requestAvailableGames(OffsideApplication.getPlayerId(), OffsideApplication.getSelectedPrivateGroupId());

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    private void singleGroupTabSwitched(View view) {
        if (singleGroupGamesTabRoot == view) {
            showAvailableGames();
        } else if (singleGroupLeagueTabRoot == view) {
            showLeague();
        }
    }

    public void showAvailableGames() {

        singleGroupLeagueRoot.setVisibility(View.GONE);
        singleGroupLeagueTabRoot.setBackgroundResource(R.color.navigationMenu);
        singleGroupGamesRoot.setVisibility(View.VISIBLE);
        singleGroupGamesTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);

        loadingRoot.setVisibility(View.GONE);
        singleGroupLeagueRoot.setVisibility(View.GONE);


    }

    private void showLeague() {

        HashMap<String, LeagueRecord[]> leaguesRecords = OffsideApplication.getLeaguesRecords();
        PrivateGroup selectedPrivateGroup = OffsideApplication.getSelectedPrivateGroup();
        if (selectedPrivateGroup == null)
            return;

        String groupId = selectedPrivateGroup.getId();
        if (!leaguesRecords.containsKey(groupId)) {
            getLeagueRecords(groupId);
            return;
        }
        LeagueRecord[] leagueRecords = leaguesRecords.get(groupId);
        LeagueAdapter leagueAdapter = new LeagueAdapter(getActivity(), new ArrayList<>(Arrays.asList(leagueRecords)));
        singleGroupLeagueListView.setAdapter(leagueAdapter);

        singleGroupGamesRoot.setVisibility(View.GONE);
        singleGroupGamesTabRoot.setBackgroundResource(R.color.navigationMenu);
        singleGroupLeagueRoot.setVisibility(View.VISIBLE);
        singleGroupLeagueTabRoot.setBackgroundResource(R.color.navigationMenuSelectedItem);

        //calc my position
        String myPlayerId = OffsideApplication.getPlayerId();
        int myPosition = 0;
        for(int i=0; i< leagueRecords.length;i++ ){
            LeagueRecord lr =  leagueRecords[i];
            if(lr.getPlayerId().equals(myPlayerId))
                myPosition = i+1;
        }

        String myPositionOutOf = String.format("%d/%d", myPosition,leagueRecords.length );
        singleGroupPositionOutOfTextView.setText(myPositionOutOf);




    }

    private void getLeagueRecords(String groupId) {

        String playerId = OffsideApplication.getPlayerId();
        OffsideApplication.signalRService.requestLeagueRecords(playerId, groupId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveLeagueRecords(LeagueRecord[] leagueRecords) {
        try {
            if (leagueRecords == null || leagueRecords.length == 0)
                return;

            String groupId = OffsideApplication.getSelectedPrivateGroup().getId();
            if (OffsideApplication.getLeaguesRecords().containsKey(groupId))
                OffsideApplication.getLeaguesRecords().remove(groupId);
            OffsideApplication.getLeaguesRecords().put(groupId, leagueRecords);

            showLeague();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveAvailableGames(AvailableGame[] availableGames) {
        try {
            if (availableGames == null || availableGames.length == 0)
                return;

            OffsideApplication.setAvailableGames(availableGames);

            List<String> leagues = getDistinctLeagues(availableGames);
            for (String leagueType : leagues) {
                addLeaguePageToSingleGroupFragment(leagueType);

            }
            viewPager.setCurrentItem(0);

            loadingRoot.setVisibility(View.GONE);
            showAvailableGames();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    public void addLeaguePageToSingleGroupFragment(String leagueType) {

        try {
            addPage(leagueType);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }
    }

    private void addPage(String leagueType) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.key_league_type), leagueType);
        AvailableGamesFragment availableGamesFragment = new AvailableGamesFragment();
        availableGamesFragment.setArguments(bundle);
        viewPagerAdapter.addFragment(availableGamesFragment, getPageTitle(leagueType));
        viewPagerAdapter.notifyDataSetChanged();
        if (viewPagerAdapter.getCount() > 0) tabLayout.setupWithViewPager(viewPager);


        setupTabLayout();
    }

    private String getPageTitle(String groupType) {
        String title = "Unknown";
        if (groupType.equals("PL"))
            title = getString(R.string.lbl_primier_league);
        else if (groupType.equals("PD"))
            title = getString(R.string.lbl_la_liga);
        else if (groupType.equals("CL"))
            title = getString(R.string.lbl_champions_league);
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
    public void onPrivateGameGenerated(PrivateGameGeneratedEvent privateGameGeneratedEvent) {
        try {

            String currentPrivateGameId = privateGameGeneratedEvent.getPrivateGameId();
            joinPrivateGame(currentPrivateGameId);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }

    }

    private void joinPrivateGame(String selectedPrivateGameId) {
        OffsideApplication.setSelectedPrivateGameId(selectedPrivateGameId);
        EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_play));

    }

    private List<String> getDistinctLeagues(AvailableGame[] availableGames) {

        List<String> availableLeagues = new ArrayList<>();
        for (int i = 0; i < availableGames.length; i++) {
            String currentLeague = availableGames[i].getLeagueName();
            if (!availableLeagues.contains(currentLeague)) {
                availableLeagues.add(currentLeague);
            }

        }

        return availableLeagues;


    }

}
