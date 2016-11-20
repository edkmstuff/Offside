package com.offsidegame.offside;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnswersFragment extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String [] values = new String[]{"Eran","Kfir", "Roee"};
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1, values);

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l,View v,int position, long id){
        super.onListItemClick(l,v,position,id);

    }


}
