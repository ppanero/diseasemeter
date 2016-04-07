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
        progressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
        diseaseText = (EditText)rootView.findViewById(R.id.zone_it);
        dateText = (EditText)rootView.findViewById(R.id.date_it);
        listv = (ListView)rootView.findViewById(R.id.info_lv);
        CreateListView();

        searchButton = (Button)rootView.findViewById(R.id.search_zone_btn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ZoneDateParam params = new ZoneDateParam(diseaseText.getText().toString(), dateText.getText().toString());

            }
        });

        return rootView;
    }

    private void CreateListView() {

        data.add(new DiseaseItem("Tifus", DiseaseLevel.MEDIUM, "Madrid, 04/12/2016", 525435, 2, 544));
        data.add(new DiseaseItem("Flew", DiseaseLevel.LOW, "Tarragona, 20/04/2016", 325466, 2, 1111));
        data.add(new DiseaseItem("Hepatitis", DiseaseLevel.LOW, "Bilbao, 09/05/2016", 3534, 1, 5435));
        data.add(new DiseaseItem("Cancer", DiseaseLevel.LOW, "Caceres, 17/03/2016", 53455, 1, 334));
        data.add(new DiseaseItem("AIDS", DiseaseLevel.LOW, "Barcelona, 28/11/2016", 345345, 1, 7443));
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

    public class RetriveDataTask extends AsyncTask<ZoneDateParam, Void, List<DiseaseItem>> {

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
                        if(!withParams) stringUrl.concat("?");
                        stringUrl.concat("zone=").concat(param.getZone());
                    }
                    if(!param.getDate().equals("")){
                        if(!withParams) stringUrl.concat("?");
                        else stringUrl.concat("&");
                        stringUrl.concat("date=").concat(param.getDate());
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
                            ret.add(new DiseaseItem(obj.getString("name"), DiseaseLevel.fromString(obj.getString("level")),
                                    obj.getString("place").concat(",").concat(obj.getString("date")),obj.getInt("tweets"),
                                    obj.getInt("cdc"), obj.getInt("news")));
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
