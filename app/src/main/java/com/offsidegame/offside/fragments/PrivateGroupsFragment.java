package com.offsidegame.offside.fragments;

/**
 * Created by user on 7/19/2017.
 */

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.adapters.PrivateGroupAdapter;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PrivateGroup;
import com.offsidegame.offside.models.PrivateGroupsInfo;

import java.util.ArrayList;


public class PrivateGroupsFragment extends Fragment {

    private ArrayList<PrivateGroup> privateGroups;
    private String groupType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_private_groups, container, false);

        if (OffsideApplication.getPrivateGroupsInfo() == null)
            return rootView;

        ListView listView = (ListView) rootView.findViewById(R.id.l_private_groups_list_view);
        PrivateGroupAdapter privateGroupAdapter = new PrivateGroupAdapter(this.getActivity(), getPrivateGroups());
        listView.setAdapter(privateGroupAdapter);


        return rootView;
    }

    private ArrayList<PrivateGroup> getPrivateGroups() {
        groupType = this.getArguments().getString(getString(R.string.key_group_type));
        PrivateGroupsInfo privateGroupsInfo = OffsideApplication.getPrivateGroupsInfo();
        if (privateGroupsInfo != null)
            privateGroups = privateGroupsInfo.getPrivateGroups();
        if (privateGroups == null)
            privateGroups = new ArrayList<>();

        ArrayList filteredGroupsList = new ArrayList<>();

        for (PrivateGroup privateGroup : privateGroups) {
            if (privateGroup.getGroupType().equals(groupType))
                filteredGroupsList.add(privateGroup);
        }

        return filteredGroupsList;

    }

}
