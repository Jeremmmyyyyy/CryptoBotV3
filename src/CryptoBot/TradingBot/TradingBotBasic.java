package CryptoBot.TradingBot;

import CryptoBot.DataAnalyser.DocumentLoader;
import CryptoBot.Preconditions;
import CryptoBot.WebService.TextManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public abstract class TradingBotBasic {

    private final DateTimeFormatter formatDateDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatDateMin = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");
    private final LocalDateTime now = LocalDateTime.now();

    private final DocumentLoader documentLoader = new DocumentLoader();
    protected String[][] dailyData;
    protected String[][] exchangeData;
    protected int counter;
    private boolean startTask = false;
    private final String fileName;
    protected final String filePath;

    private double capital;
    protected double investedCapital;
    protected double currentInvestValue;
    protected double buyValue;
    protected double sellValue;
    protected double currentExchangeValue;
    protected double transactionFees;

    public TradingBotBasic(){
        update();
        counter = 0;
        capital = 0;
        fileName = "TradingBotBasis";
        filePath = formatDateDay.format(now) + "_" + fileName + ".txt";
        dailyData = documentLoader.getDailyData();
        exchangeData = documentLoader.getExchangeData();
        TextManager.createFile("TradingBotBasis");
    }

    public TradingBotBasic(String fileName){
        update();
        counter = 0;
        capital = 0;
        this.fileName = fileName;
        filePath = formatDateDay.format(now) + "_" + this.fileName + ".txt";
        dailyData = documentLoader.getDailyData();
        exchangeData = documentLoader.getExchangeData();
        TextManager.createFile(fileName);
    }

    private void update(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            String[][] tempExchangeData = documentLoader.getExchangeData();
            @Override
            public void run() {
                if (startTask){
                    dailyData = documentLoader.getDailyData();
                    exchangeData = documentLoader.getExchangeData();
                    if(exchangeData.length == 0 || dailyData.length == 0){
                        return;
                    }
                    if (exchangeData != tempExchangeData){
                        currentExchangeValue = Double.parseDouble(exchangeData[exchangeData.length - 1][7]);
                        computeNextAction();
                        tempExchangeData = documentLoader.getExchangeData();
                        ++counter;
                    }
                }
            }
        }, 0, 10000);
    }

    protected void start(){
        startTask = true;
    }

    protected void stop(){
        if (investedCapital != 0){
            sell(100); //TODO choose to let the capital
        }
        startTask = false;
    }

    protected void setAmount(double eurosToPlayWith){
        capital = eurosToPlayWith;
    }

    protected void setTransactionFees(double fees){transactionFees = fees;}

    protected abstract void computeNextAction();

    protected void buy(int capitalToPercent) {
        Preconditions.verify(0 < capitalToPercent && capitalToPercent <= 100);
        if (capital >= 0){
            buyValue = currentExchangeValue;
            double currentInvestment = capital * (capitalToPercent / 100.0);
            investedCapital += currentInvestment;
            currentInvestValue += currentInvestment;
            capital -= currentInvestment;
            TextManager.writeToFile(filePath,
                    formatDateMin.format(LocalDateTime.now())
                            + " BUY "
                            + investedCapital + " "
                            + currentInvestValue + " "
                            + currentExchangeValue + " "
                            + capital + " "
                            + capitalToPercent + " "
                            + percentVariation(1));
        }
    }

    protected void sell(int investedCapitalToSellPercent) {
        Preconditions.verify(0 < investedCapitalToSellPercent && investedCapitalToSellPercent <= 100);
        if (investedCapital >= 0){
            sellValue = currentExchangeValue;
            investedCapital = investedCapital * sellValue / buyValue;
            double gains = (investedCapital * (investedCapitalToSellPercent / 100.0));
            capital += gains;
            investedCapital -= gains;
            currentInvestValue = investedCapital * sellValue / buyValue;
            TextManager.writeToFile(filePath,
                    formatDateMin.format(LocalDateTime.now())
                            + " SELL "
                            + investedCapital + " "
                            + currentInvestValue + " "
                            + currentExchangeValue + " "
                            + capital + " "
                            + investedCapitalToSellPercent+ " "
                            + percentVariation(1));
        }
    }

    protected void doNothing(){
        sellValue = currentExchangeValue;
        currentInvestValue = investedCapital * sellValue / buyValue;
        TextManager.writeToFile(filePath,
                formatDateMin.format(LocalDateTime.now())
                        + " --- "
                        + investedCapital + " "
                        + currentInvestValue + " "
                        + currentExchangeValue + " "
                        + capital + " "
                        + "--- "
                        + percentVariation(1));
    }

    protected double percentVariation(int lastMin){
        if (exchangeData.length <= lastMin){
            return Double.NaN;
        }else {
            Preconditions.verify(lastMin >= 1);
            return 100 - ((currentExchangeValue / Double.parseDouble(exchangeData[exchangeData.length- 1 - lastMin][7]))) * 100;
        }
    }
}
