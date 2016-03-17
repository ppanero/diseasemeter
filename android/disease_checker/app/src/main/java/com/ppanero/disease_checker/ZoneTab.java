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
    private List<DiseaseItem> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        data =new ArrayList<DiseaseItem>();

        View rootView = inflater.inflate(R.layout.zone_tab, container, false);
        listv = (ListView)rootView.findViewById(R.id.info_lv);
        CreateListView();

        return rootView;
    }

    private void CreateListView() {

        data.add(new DiseaseItem("Ebola", DiseaseLevel.HIGH, "Madrid, 20/10/2016", 34563456, 2, 3464));
        data.add(new DiseaseItem("Zika", DiseaseLevel.HIGH, "Barcelona, 21/10/2016", 3456663, 3, 537));
        data.add(new DiseaseItem("Dengue", DiseaseLevel.MEDIUM, "Vigo, 23/01/2016", 100, 3, 4234));
        data.add(new DiseaseItem("Malaria", DiseaseLevel.MEDIUM, "Malaga, 19/02/2016",1240, 2, 7343));
        data.add(new DiseaseItem("Tifus", DiseaseLevel.MEDIUM, "Madrid, 04/12/2016", 525435, 2, 544));
        data.add(new DiseaseItem("Flew", DiseaseLevel.LOW, "Tarragona, 20/04/2016", 325466, 2, 1111));
        data.add(new DiseaseItem("Hepatitis", DiseaseLevel.LOW, "Bilbao, 09/05/2016", 3534, 1, 5435));
        data.add(new DiseaseItem("Cancer", DiseaseLevel.LOW, "Caceres, 17/03/2016", 53455, 1, 334));
        data.add(new DiseaseItem("AIDS", DiseaseLevel.LOW, "Barcelona, 28/11/2016", 345345, 1, 7443));

        //Create an adapter for the listView and add the ArrayList to the adapter.
        listv.setAdapter(new DiseaseItemAdapter(getActivity().getApplicationContext()
                , R.layout.disease_item, data));

        listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //args2 is the listViews Selected index
            }
        });
    }
}
