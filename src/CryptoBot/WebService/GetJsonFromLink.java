package CryptoBot.WebService;

import CryptoBot.Preconditions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public final class GetJsonFromLink {
    private static String[][] data;

    private GetJsonFromLink() {}

    public static String[][] getFromUrl(String urlAsString){
        try{
            Preconditions.verify(urlAsString.contains("https://www.alphavantage.co/query?function") && urlAsString.contains("apikey="));
            URL url = new URL(urlAsString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = input.readLine()) != null){
                response.append(inputLine);
            }
            input.close();

            String jsonTitleForValues;
            JSONObject myResponse = new JSONObject(response.toString());

            if(urlAsString.contains("CURRENCY_EXCHANGE_RATE")){
                jsonTitleForValues = "Realtime Currency Exchange Rate";
                JSONArray title = myResponse.getJSONObject(jsonTitleForValues).names();
                data = new String[1][title.length()];
                for (int i = 0; i< title.length(); ++i){
                    data[0][i] = myResponse.getJSONObject(jsonTitleForValues).getString(title.getString(i));
                }
                data = sortRealTimeData();

            }else if(urlAsString.contains("DIGITAL_CURRENCY_DAILY")){
                jsonTitleForValues = "Time Series (Digital Currency Daily)";
                JSONArray title = myResponse.getJSONObject(jsonTitleForValues).names();
                data = new String[title.length()][11];
                for (int i = 0; i < title.length(); ++i) {
                    JSONArray dailyValues = myResponse.getJSONObject(jsonTitleForValues).getJSONObject(title.getString(i)).names();
                    data[i][0] = title.getString(i);

                    for (int j = 0; j < dailyValues.length(); ++j) {
                        data[i][j + 1] = myResponse.getJSONObject(jsonTitleForValues).getJSONObject(title.getString(i)).getString(dailyValues.getString(j));
                    }
                }
                data = sortDailyData();

            }else{
                throw new Error("the url is not valid");
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static String[][] sortRealTimeData(){
        String[][] sortedData = new String[1][data[0].length];
        sortedData[0][0] = data[0][2];
        sortedData[0][1] = data[0][4];
        sortedData[0][2] = data[0][8];
        sortedData[0][3] = data[0][1];
        sortedData[0][4] = data[0][0];
        sortedData[0][5] = data[0][7];
        sortedData[0][6] = data[0][6];
        sortedData[0][7] = data[0][3];
        sortedData[0][8] = data[0][5];
        return sortedData;
    }

    private static String[][] sortDailyData(){
        int rows = data.length, columns = data[0].length;
        String[][] tempTable = data.clone();
        String[][] sortedData = new String[rows][columns];
        //Arrays.sort(tempTable, (v1, v2) -> v2[0].compareTo(v1[0]));
        Arrays.sort(tempTable, (value1, value2) -> {
            final String time1 = value1[0];
            final String time2 = value2[0];
            return time1.compareTo(time2);
        });

        for (int i = 0; i < rows; i++) {
            sortedData[i][0] = tempTable[i][0];
            sortedData[i][1] = tempTable[i][10];
            sortedData[i][2] = tempTable[i][8];
            sortedData[i][3] = tempTable[i][2];
            sortedData[i][4] = tempTable[i][4];
            sortedData[i][5] = tempTable[i][5];
            sortedData[i][6] = tempTable[i][1];
            sortedData[i][7] = tempTable[i][6];
            sortedData[i][8] = tempTable[i][7];
            sortedData[i][9] = tempTable[i][3];
            sortedData[i][10] = tempTable[i][9];
        }

        return sortedData;
    }
}
