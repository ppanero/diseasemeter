package com.ppanero.disease_checker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Light on 11/03/16.
 */

public class ZoneTab extends Fragment {

    /** Called when the activity is first created. */
    ListView listv;
    private List<String> info;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        info =new ArrayList<String>();

        View rootView = inflater.inflate(R.layout.zone_tab, container, false);
        listv = (ListView)rootView.findViewById(R.id.info_lv);
        CreateListView();

        return rootView;
    }

    private void CreateListView() {
        info.add("Ebola");
        info.add("Zika");
        info.add("Dengue");
        info.add("Malaria");
        info.add("Tifus");
        info.add("Flew");
        info.add("Hepatitis");
        info.add("Cancer");
        info.add("AIDS");
        //Create an adapter for the listView and add the ArrayList to the adapter.
        listv.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext()
                                                    , android.R.layout.simple_list_item_1, info));
        listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //args2 is the listViews Selected index
            }
        });
    }
}
