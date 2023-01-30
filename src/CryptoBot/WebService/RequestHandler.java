package CryptoBot.WebService;

import CryptoBot.Preconditions;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

public final class RequestHandler {

    private final ObservableRequestHandler observableRequestHandler;

    private boolean startScheduledTask = false;

    private final DateTimeFormatter formatDateDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatDateHour = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final LocalDateTime now = LocalDateTime.now();
    private int delay = 90;
    private int counter = 0;
    private final Timer timer = new Timer();

    private final String CURRENT_DAY_PREFERENCE = "CurrentDay";
    private final String COUNTING_PREFERENCE = "Counting";
    private final Preferences preferences = Preferences.userRoot().node(this.getClass().getName());

    private boolean URLConnection = false;
    private final String DAILY_DATA = "DailyData";
    private final String DAILY_DATA_FILENAME = formatDateDay.format(now) + "_" + DAILY_DATA + ".txt";
    private final String EXCHANGE_RATE = "ExchangeRate";
    private final String EXCHANGE_RATE_FILENAME = formatDateDay.format(now) + "_" + EXCHANGE_RATE + ".txt";

    public enum ApiKey{
        API_KEY_1 (""),
        API_KEY_2 ("");
        String key;
        ApiKey(String key){this.key = key;}
    }

    public RequestHandler(ObservableRequestHandler observableRequestHandler){
        this.observableRequestHandler = observableRequestHandler;
        TextManager.createFile(DAILY_DATA);
        TextManager.createFile(EXCHANGE_RATE);

        if ( !(TextManager.checkExistingFile(DAILY_DATA_FILENAME)
                && TextManager.checkExistingFile(EXCHANGE_RATE_FILENAME))){
            throw new Error("Problem with data base creation.");
        }
        checkInternetConnection();
        setPreferences();
        startScheduledTask();
    }

    private void startScheduledTask(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                observableRequestHandler.setAll(RequestHandler.this);
                checkInternetConnection();
                computeNewDelay();
                if (startScheduledTask && URLConnection){
                    counter++;
                    if (delay == counter){
                        scheduledRequestTask();
                        counter = 0;
                    }
                }
            }
        }, 0, 1000);
    }

    public void start(){
        startScheduledTask = true;
    }

    public void stop(){
        startScheduledTask = false;
    }

    public void stopTimer(){
        timer.cancel();
        timer.purge();
    }

    private void scheduledRequestTask(){
        if (! checkDay() || TextManager.readFromFileToArray(DAILY_DATA_FILENAME).isEmpty()){
            requestDailyData();
            preferences.put(CURRENT_DAY_PREFERENCE, formatDateDay.format(now));
            preferences.putInt(COUNTING_PREFERENCE, (preferences.getInt(COUNTING_PREFERENCE, -1) + 1));
            System.out.println("New Daily Data Request");
        }

        if (preferences.getInt(COUNTING_PREFERENCE, -1) <= 490){
            requestCurrentData(ApiKey.API_KEY_1.key);
            System.out.println("ok1");
        }else if(preferences.getInt(COUNTING_PREFERENCE, -1) <=980){
            requestCurrentData(ApiKey.API_KEY_2.key);
            System.out.println("ok2");
        }else{
            throw new Error("To many AlphaVantage calls for today");
        }
        preferences.putInt(COUNTING_PREFERENCE, preferences.getInt(COUNTING_PREFERENCE, -1) + 1);
    }

    public void requestDailyData(){
        ArrayList<String> text = TextManager.readFromFileToArray(DAILY_DATA_FILENAME);
        StringBuilder line = new StringBuilder();
        if (text.isEmpty()){
            String[][] data = GetJsonFromLink.getFromUrl("https://www.alphavantage.co/query?function=DIGITAL_CURRENCY_DAILY&symbol=BTC&market=EUR&apikey=0EK8OVT119SHBLX8");
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    line.append(data[i][j]).append(" ");
                }
                TextManager.writeToFile(DAILY_DATA_FILENAME, line.toString());
                line.setLength(0);
            }
        }
    }

    public void requestCurrentData(String key){
        StringBuilder line = new StringBuilder();
        String[][] data = GetJsonFromLink.getFromUrl("https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=BTC&to_currency=EUR&apikey=" + key);
        for (int i = 0; i < data[0].length; i++) {
            line.append(data[0][i]).append(" ");
        }
        TextManager.writeToFile(EXCHANGE_RATE_FILENAME, line.toString());
    }

    private void computeNewDelay(){
        int AMOUNT_OF_DAILY_CALLS = 980;
        double timeUntilNewDay = timeUntilNewDay();
        int numberOfCalls = preferences.getInt(COUNTING_PREFERENCE, -1);
        Preconditions.verify(numberOfCalls != -1);
        int remainingApiCalls = AMOUNT_OF_DAILY_CALLS - numberOfCalls;
        double newDelay = timeUntilNewDay / remainingApiCalls;
        int tempDelay = (int) Math.round(newDelay * 60);
        delay = Math.max(tempDelay, 60);
    }


    private void setPreferences(){
        if (preferences.getInt(COUNTING_PREFERENCE, -1) == -1){
            preferences.putInt(COUNTING_PREFERENCE, 0);
        }
        if (preferences.get(CURRENT_DAY_PREFERENCE, "NaN").equals("NaN")
                || ! (preferences.get(CURRENT_DAY_PREFERENCE, "NaN").equals(formatDateDay.format(now)))){
            preferences.putInt(COUNTING_PREFERENCE, 0);
            preferences.put(CURRENT_DAY_PREFERENCE, formatDateDay.format(now));
        }
    }

    private boolean checkDay(){
        return preferences.get(CURRENT_DAY_PREFERENCE, "NaN").equals(formatDateDay.format(now));
    }

    private int timeUntilNewDay(){
        int hours = Integer.parseInt(formatDateHour.format(now).substring(11,13));
        int minutes = Integer.parseInt(formatDateHour.format(now).substring(14,16));
        return 24*60 - (hours * 60 + minutes);
    }

    private void checkInternetConnection(){
        try {
            URL url = new URL("https://www.alphavantage.co/");
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            URLConnection = true;
        }catch (Exception e){
            URLConnection = false;
        }
    }

    public boolean getInternetConnection(){
        return URLConnection;
    }

    public  int getCounter(){
        return counter;
    }

    public int getDelay(){
        return delay;
    }

    public int getNumberOfCalls(){
        return preferences.getInt(COUNTING_PREFERENCE, -1);
    }

    public boolean getExistingDailyData(){
        return TextManager.checkExistingFile(DAILY_DATA_FILENAME);
    }

    public boolean getExistingCurrentData(){
        return TextManager.checkExistingFile(EXCHANGE_RATE_FILENAME);
    }
}
