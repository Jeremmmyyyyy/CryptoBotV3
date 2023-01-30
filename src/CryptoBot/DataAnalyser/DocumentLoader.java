package CryptoBot.DataAnalyser;

import CryptoBot.WebService.TextManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public final class DocumentLoader {

    private String[][] dailyData;
    private String[][] exchangeData;

    private final DateTimeFormatter formatDateDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDateTime now = LocalDateTime.now();
    private final String DAILY_DATA = "DailyData";
    private final String DAILY_DATA_FILENAME = formatDateDay.format(now) + "_" + DAILY_DATA + ".txt";
    private final String EXCHANGE_RATE = "ExchangeRate";
    private final String EXCHANGE_RATE_FILENAME = formatDateDay.format(now) + "_" + EXCHANGE_RATE + ".txt";

    public DocumentLoader(){
        dailyData = TextManager.readFromFileToTable(DAILY_DATA_FILENAME);
        exchangeData = TextManager.readFromFileToTable(EXCHANGE_RATE_FILENAME);
    }

    public String[][] getDailyData(){
        updateDailydata();
        return dailyData;
    }
    public String[][] getExchangeData(){
        updateExchangeData();
        return exchangeData;
    }

    private void updateDailydata(){
        String[][] tempDailyData = TextManager.readFromFileToTable(DAILY_DATA_FILENAME);
        if (! Arrays.deepEquals(tempDailyData, dailyData)){
            dailyData = tempDailyData;
        }
    }

    private void updateExchangeData(){
        String[][] tempExchangeData = TextManager.readFromFileToTable(EXCHANGE_RATE_FILENAME);
        if (! Arrays.deepEquals(tempExchangeData, exchangeData)){
            exchangeData = tempExchangeData;
        }
    }
}
