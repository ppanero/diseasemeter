package com.diseasemeter.data_colector.microsoft_api;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * Created by Light on 21/04/16.
 */
public class Translator {


    private static final String CLIENT_SECRET = "Wm2Ggw08AcZzZZLZhjh9NdbB1LjAq/yawpWqKjiJAwU=";
    private static final String CLIENT_ID = "data-collector";

    public static String translate(String text) {
        //Replace client_id and client_secret with your own.
        Translate.setClientId(CLIENT_ID);
        Translate.setClientSecret(CLIENT_SECRET);

        // Translate an english string to spanish
        String translation = "";
        try {
            translation = Translate.execute(text, Language.SPANISH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return translation;
    }

    /*
    // Google Translate Implementation, not used because it is paid

    private static final String API_KEY = "&key=AIzaSyDHy1fqfux9sJ8vjWLy9czZftenXd9WCNc";
    private static final String HTTP = "https://www.googleapis.com/language/translate/v2";
    private static final String TARGETED_LANGUAGES = "&target=es&source=en";
    private static final String QUERY_PARAM = "?q=";

    public static String translate(String text) {

        String stringUrl = HTTP.concat(QUERY_PARAM).concat(text.replace(MACRO.SPACE, MACRO.PLUS))
                            .concat(TARGETED_LANGUAGES).concat(API_KEY);
        System.out.println(stringUrl);
        try {

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
                    JSONArray results = (JSONArray) jsonDiseases.get("results");
                    JSONObject geometry = (JSONObject) ((JSONObject)results.get(0)).get("geometry");
                    JSONObject location = geometry.getJSONObject("location");

                    return new Double[]{ location.getDouble("lng"), location.getDouble("lat")};
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }*/
}
