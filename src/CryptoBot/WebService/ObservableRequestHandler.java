package CryptoBot.WebService;

import javafx.beans.property.*;

public final class ObservableRequestHandler {

    private final BooleanProperty internetConnection;
    private final IntegerProperty counter;
    private final IntegerProperty delay;
    private final IntegerProperty numberOfCalls;
    private final BooleanProperty existingDailyData;
    private final BooleanProperty existingCurrentData;

    public ObservableRequestHandler(){
        internetConnection = new SimpleBooleanProperty();
        counter = new SimpleIntegerProperty();
        delay = new SimpleIntegerProperty();
        numberOfCalls = new SimpleIntegerProperty();
        existingDailyData = new SimpleBooleanProperty();
        existingCurrentData = new SimpleBooleanProperty();
    }

    public void setAll(RequestHandler requestHandler){
        internetConnection.set(requestHandler.getInternetConnection());
        counter.set(requestHandler.getCounter());
        delay.set(requestHandler.getDelay());
        numberOfCalls.set(requestHandler.getNumberOfCalls());
        existingDailyData.set(requestHandler.getExistingDailyData());
        existingCurrentData.set(requestHandler.getExistingCurrentData());
    }

    public BooleanProperty getInternetConnection(){
        return internetConnection;
    }

    public IntegerProperty getCounter(){
        return counter;
    }

    public IntegerProperty getDelay(){
        return delay;
    }

    public IntegerProperty getNumberOfCalls(){
        return numberOfCalls;
    }

    public BooleanProperty getExistingDailyData(){
        return existingDailyData;
    }

    public BooleanProperty getExistingCurrentData(){
        return existingCurrentData;
    }
}
