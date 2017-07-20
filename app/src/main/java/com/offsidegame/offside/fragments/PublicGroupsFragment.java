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
import com.offsidegame.offside.adapters.CustomAdapter;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PrivateGroup;

import java.util.ArrayList;
import java.util.Arrays;


public class PublicGroupsFragment extends Fragment{

    private PrivateGroup[] privateGroups;
    private static String GROUP_CLASSIFICATION_NAME = "PUBLIC_GROUP";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_public_groups, container, false);

        if(OffsideApplication.getPrivateGroupsInfo()==null)
            return rootView;

        ListView listView = (ListView) rootView.findViewById(R.id.f_public_groups_list_view);
        CustomAdapter customAdapter = new CustomAdapter(this.getActivity(), getPrivateGroups());
        listView.setAdapter(customAdapter);


        return rootView;
    }

    private ArrayList<PrivateGroup> getPrivateGroups(){

        privateGroups = OffsideApplication.getPrivateGroupsInfo().getPrivateGroups();

        //ArrayList privateGroupsArrayList = new ArrayList(Arrays.asList(privateGroups));
        ArrayList filteredGroupsList = new ArrayList<>();

        for(PrivateGroup privateGroup: privateGroups){
            if(privateGroup.getGroupClassification().equals(GROUP_CLASSIFICATION_NAME))
                filteredGroupsList.add(privateGroup);
        }

        return filteredGroupsList;

    }



    @Override
    public String toString() {
        String title = "קבוצות ציבוריות";
        return title;

    }



}
