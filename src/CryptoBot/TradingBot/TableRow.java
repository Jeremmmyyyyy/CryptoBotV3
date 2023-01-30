package CryptoBot.TradingBot;

public final class TableRow {

    private final String hour;
    private final String transactionType;
    private final String investedCapital;
    private final String investmentValue;
    private final String currentExchangeValue;
    private final String capital;
    private final String transactionPercent;
    private final String percentVariation;

    public TableRow(String hour,
                    String transactionType,
                    String investedCapital,
                    String investmentValue,
                    String currentExchangeValue,
                    String capital,
                    String transactionPercent,
                    String percentVariation){

        this.hour = hour;
        this.transactionType = transactionType;
        this.investedCapital = investedCapital;
        this.investmentValue = investmentValue;
        this.currentExchangeValue    = currentExchangeValue;
        this.capital = capital;
        this.transactionPercent = transactionPercent;
        this.percentVariation = percentVariation;
    }

    public String getHour() {
        return hour;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getInvestedCapital() {
        return investedCapital;
    }

    public String getInvestmentValue() {
        return investmentValue;
    }

    public String getCurrentExchangeValue() {
        return currentExchangeValue;
    }

    public String getCapital() {
        return capital;
    }

    public String getTransactionPercent() {
        return transactionPercent;
    }

    public String getPercentVariation() {
        return percentVariation;
    }
}
