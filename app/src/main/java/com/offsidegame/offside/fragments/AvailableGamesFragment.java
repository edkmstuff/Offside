package com.offsidegame.offside.fragments;

/**
 * Created by user on 7/19/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.adapters.AvailableGamesAdapter;

import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.OffsideApplication;


import java.util.ArrayList;


public class AvailableGamesFragment extends Fragment {

    private AvailableGame[] availableGames;
    private String leagueType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_available_games, container, false);

        if (OffsideApplication.getAvailableGames() == null)
            return rootView;

        ListView listView = (ListView) rootView.findViewById(R.id.l_available_games_list_view);
        AvailableGamesAdapter availableGamesAdapter = new AvailableGamesAdapter(this.getActivity(), getAvailableGames());
        listView.setAdapter(availableGamesAdapter);


        return rootView;
    }


    private ArrayList<AvailableGame> getAvailableGames() {
        leagueType = leagueType != null ? leagueType :this.getArguments().getString("leagueType");
        availableGames = OffsideApplication.getAvailableGames();

        ArrayList filteredAvailableGamesList = new ArrayList<>();

        for (AvailableGame availableGame : availableGames) {
            if (availableGame.getLeagueName().equals(leagueType))
                filteredAvailableGamesList.add(availableGame);
        }

        return filteredAvailableGamesList;

    }


//    @Override
//    public String toString() {
//        leagueType = this.getArguments().getString("leagueType");
//        String title = "לא ידוע";
//        if (leagueType == null)
//            return title;
//        if (leagueType.equals("PL"))
//            title = "ליגת האלופות";
//        else if (leagueType.equals("isr1"))
//            title = "ליגת העל";
//
//        return title;
//
//    }

//    public String getTitle() {
//        String title = "לא ידוע";
//        if (leagueType == null)
//            return title;
//        if (leagueType.equals("PL"))
//            title = "ליגת האלופות";
//        else if (leagueType.equals("isr1"))
//            title = "ליגת העל";
//
//        return title;
//
//    }


}
