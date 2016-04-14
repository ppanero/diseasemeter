package com.ppanero.disease_checker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Light on 11/03/16.
 */

public class ZoneTab extends Fragment {

    /** Called when the activity is first created. */
    private ListView listv;
    private ProgressBar progressBar;
    private EditText diseaseText;
    private EditText dateText;
    private Button searchButton;
    private DiseaseItemAdapter listvAdapter;
    private List<DiseaseItem> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        data =new ArrayList<DiseaseItem>();

        View rootView = inflater.inflate(R.layout.zone_tab, container, false);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar_zonetab);
        diseaseText = (EditText)rootView.findViewById(R.id.zone_it);
        dateText = (EditText)rootView.findViewById(R.id.date_it);
        listv = (ListView)rootView.findViewById(R.id.info_lv);
        CreateListView();

        searchButton = (Button)rootView.findViewById(R.id.search_zone_btn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ZoneDateParam params = new ZoneDateParam(diseaseText.getText().toString(), dateText.getText().toString());
                ZoneDateParam[] myTaskParams = { params };
                new RetriveZoneDataTask().execute(myTaskParams);
            }
        });

        return rootView;
    }

    private void CreateListView() {
        //Create an adapter for the listView and add the ArrayList to the adapter.
        listvAdapter = new DiseaseItemAdapter(getActivity().getApplicationContext(), R.layout.disease_item, data);
        listv.setAdapter(listvAdapter);
    }

    public class ZoneDateParam {

        private String zone;
        private String date;

        public ZoneDateParam(String zone, String date) {
            this.zone = zone;
            this.date = date;
        }

        public String getZone() {
            return zone;
        }

        public String getDate() {
            return date;
        }
    }

    public class RetriveZoneDataTask extends AsyncTask<ZoneDateParam, Void, List<DiseaseItem>> {

        private Exception exception;

        protected void onPreExecute() {
            listvAdapter.clear();
            progressBar.setVisibility(View.VISIBLE);
        }

        protected List<DiseaseItem> doInBackground(ZoneDateParam... params) {
            // Do some validation here
            List<DiseaseItem> ret = new ArrayList<>();
            try {
                String stringUrl = MACRO.API_IP.concat(MACRO.API_ZONE_ENTRYPOINT);
                for (ZoneDateParam param : params){
                    boolean withParams = false;
                    if (!param.getZone().equals("")) {
                        if(!withParams) stringUrl = stringUrl.concat("?");
                        stringUrl = stringUrl.concat("zone=").concat(param.getZone());
                    }
                    if(!param.getDate().equals("")){
                        if(!withParams) stringUrl = stringUrl.concat("?");
                        else stringUrl = stringUrl.concat("&");
                        stringUrl = stringUrl.concat("date=").concat(param.getDate());
                    }
                }
                URL url = new URL(stringUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String response = "";
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    response = stringBuilder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
                try {
                    if (!response.equals("")) {
                        JSONObject jsonDiseases = (JSONObject) new JSONTokener(response).nextValue();
                        JSONArray jsondlist = (JSONArray) jsonDiseases.get("diseases");
                        for (int i = 0; i < jsondlist.length(); ++i){
                            JSONObject obj = jsondlist.getJSONObject(i);
                            DiseaseLevel dl = DiseaseLevel.values()[obj.getInt("level")];
                            if(!obj.getBoolean("active")) dl = DiseaseLevel.INACTIVE;

                            ret.add(new DiseaseItem(obj.getString("name"), dl,
                                    obj.getString("location").concat(",").concat(obj.getString("lastUpdate")),
                                    obj.getInt("tweetsCount"), obj.getInt("cdcCount"), obj.getInt("newsCount")));
                        }
                    }
                } catch (JSONException e) {
                    return ret;
                }
            } catch (MalformedURLException e) {
                return ret;
            } catch (IOException e) {
                return ret;
            }
            return ret;
        }

        protected void onPostExecute(List<DiseaseItem> response) {
            progressBar.setVisibility(View.GONE);
            if(!response.isEmpty()) {
                listvAdapter.addAll(response);
            }
        }
    }
}
