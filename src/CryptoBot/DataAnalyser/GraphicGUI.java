package CryptoBot.DataAnalyser;

import CryptoBot.TradingBot.Bots.TradingBot1;
import CryptoBot.TradingBot.TradingBotGUI;
import CryptoBot.WebService.GUI;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class GraphicGUI {

    private String [][] dailyData;
    private String [][] exchangeData;

    private LineChart<String, Number> dailyLineChart;
    private final XYChart.Series<String, Number> dailyMinSeries;
    private final XYChart.Series<String, Number> dailyMaxSeries;

    private LineChart<String, Number> exchangeLineChart;
    private final XYChart.Series<String, Number> exchangeSeries;
    private final Slider exchangeSlider = new Slider();

    private final DocumentLoader documentLoader = new DocumentLoader();

    private NumberAxis yAxis = new NumberAxis();
    private int sliderData = 0;
    private ScheduledExecutorService scheduledExecutorService;
    private Future<?> scheduledTask;
    private boolean createYAxis = true;

    private BorderPane dailyBorderPane;
    private BorderPane exchangeBorderPane;


    public GraphicGUI(){
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Stage mainStage = new Stage();
        mainStage.setTitle("BTC Graphics");
        mainStage.setScene(new Scene(tabPane));
        mainStage.getIcons().add(new Image("bitcoinLogo.jpeg"));
        mainStage.setWidth(900);
        mainStage.setHeight(600);
        mainStage.setResizable(false);
        mainStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        mainStage.show();
        dailyData = documentLoader.getDailyData();
        exchangeData = documentLoader.getExchangeData();

        dailyMinSeries = new XYChart.Series<>();
        dailyMaxSeries = new XYChart.Series<>();
        exchangeSeries = new XYChart.Series<>();

        windowIfNoData();

        if (dailyData.length != 0){
            dailyBorderPane.setCenter(dailyCenter());
            dailyBorderPane.setBottom(dailyBottom());
            dailyLineChart.setAnimated(true);
        }

        if (exchangeData.length != 0){
            exchangeBorderPane.setCenter(exchangeCenter());
            exchangeBorderPane.setBottom(exchangeBottom());
        }

        TradingBotGUI tradingBotGUI = new TradingBotGUI("tradingBot1", new TradingBot1("tradingBot1"));

        Tab BTCDailyTab = new Tab("BTC Course", dailyBorderPane);
        Tab BTCExchangeRateTab = new Tab("BTC ExchangeRate", exchangeBorderPane);
        Tab requestHandlerTab = new Tab("RequestHandler", new BorderPane(GUI.center(), null, GUI.right(), GUI.bottom(), null));
        Tab bot = new Tab("Trading Bot", tradingBotGUI.borderPane());
        tabPane.getTabs().addAll(BTCDailyTab, BTCExchangeRateTab, requestHandlerTab, bot);
    }

    private void windowIfNoData(){
        Button requestNewData1 = new Button("Refresh");
        Button requestNewData2 = new Button("Refresh");
        VBox vBox1 = new VBox();
        VBox vBox2 = new VBox();
        Text text1 = new Text("The requested data for the graphics are unavailable.");
        Text text2 = new Text("The requested data for the graphics are unavailable.");
        vBox1.setPadding(new Insets(10, 10, 200,425));
        vBox2.setPadding(new Insets(10, 10, 200,425));

        vBox1.getChildren().addAll(text1,requestNewData1);
        vBox2.getChildren().addAll(text2, requestNewData2);
        dailyBorderPane = new BorderPane(text1, null, null, vBox1, null);
        exchangeBorderPane = new BorderPane(text2, null, null, vBox2, null);

        AtomicInteger counter1 = new AtomicInteger();
        requestNewData1.setOnAction(e -> {
            dailyData = documentLoader.getDailyData();
            if (dailyData.length != 0){
                dailyBorderPane.setCenter(dailyCenter());
                dailyBorderPane.setBottom(dailyBottom());
            }else{
                text1.setText("The requested data for the graphics are unavailable.\n \n Please download them with the request handler(" + counter1.getAndIncrement() + ")");
            }
        });
        AtomicInteger counter2 = new AtomicInteger();
        requestNewData2.setOnAction(e -> {
            exchangeData = documentLoader.getExchangeData();
            if (exchangeData.length != 0){
                exchangeBorderPane.setCenter(exchangeCenter());
                exchangeBorderPane.setBottom(exchangeBottom());
            }else{
                text2.setText("The requested data for the graphics are unavailable.\n \n Please download them with the request handler(" + counter2.getAndIncrement() + ")");
            }
        });
    }



    private Node dailyCenter(){
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Value");
        dailyLineChart = new LineChart<>(xAxis, yAxis);

        dailyLineChart.setTitle("BTC course");
        dailyLineChart.setCreateSymbols(false);
        dailyLineChart.getXAxis().setAutoRanging(true);
        dailyLineChart.getYAxis().setAutoRanging(true);

        for (int i = 1000 - 365; i < 1000; ++i) {
            dailyMinSeries.getData().add(new XYChart.Data<>(dailyData[i][0], Double.parseDouble(dailyData[i][4])));
            dailyMaxSeries.getData().add(new XYChart.Data<>(dailyData[i][0], Double.parseDouble(dailyData[i][6])));
        }
        dailyMinSeries.setName("BTC high in $");
        dailyMaxSeries.setName("BTC low in $");
        dailyLineChart.getData().add(dailyMinSeries);
        dailyLineChart.getData().add(dailyMaxSeries);
        return dailyLineChart;
    }

    private Node exchangeCenter(){
        computeExchangeLineChart(exchangeData.length);
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        yAxis.setLabel("Value");
        exchangeLineChart = new LineChart<>(xAxis, yAxis);

        exchangeLineChart.setTitle("BTC exchange rate");
        exchangeLineChart.setCreateSymbols(false);
        exchangeLineChart.getXAxis().setAutoRanging(true);
        exchangeLineChart.getYAxis().setAutoRanging(false);

        exchangeSeries.setName("BTC exchange rate");
        exchangeLineChart.getData().add(exchangeSeries);
        return exchangeLineChart;
    }

    private Node dailyBottom(){
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 10, 10,10));

        Slider slider = new Slider();
        slider.setMin(10);
        slider.setMax(1000);
        slider.setValue(365);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setBlockIncrement(10);

        slider.valueProperty().addListener((o, oV, nV) -> updateDailyLineChart(nV.intValue()));
        vBox.getChildren().add(slider);

        return vBox;
    }

    private Node exchangeBottom(){
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 10, 10,10));
        exchangeSlider.setMin(2);
        exchangeSlider.setMax(Math.max(exchangeData.length, 3));
        exchangeSlider.setValue(exchangeData.length);
        exchangeSlider.setShowTickMarks(true);
        exchangeSlider.setShowTickLabels(true);
        exchangeSlider.setBlockIncrement(10);
        exchangeSlider.valueProperty().addListener((o, oV, nV) -> {
            if (!nV.equals(oV)){
                sliderData = nV.intValue();
                updateExchangeLineChart();
            }
        });

        CheckBox checkBox = new CheckBox("Update automatically");

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        checkBox.selectedProperty().addListener((o, oV, nV) -> {
            if (nV){
                start();
            }else{
                stop();
            }
        });
        checkBox.setPadding(new Insets(0, 10, 0,700));
        vBox.getChildren().addAll(exchangeSlider, checkBox);
        return vBox;
    }

    private void computeExchangeLineChart(int numberOfValues){

        double max = Double.parseDouble(exchangeData[exchangeData.length -1][7]),
                min = Double.parseDouble(exchangeData[exchangeData.length -1][7]),
                currentValue;
        for (int i = exchangeData.length - numberOfValues; i < exchangeData.length; i++) {
            currentValue = Double.parseDouble(exchangeData[i][7]);
            exchangeSeries.getData().add(new XYChart.Data<>(exchangeData[i][1], currentValue));
            if (currentValue >= max){
                max = currentValue;
            }
            if (currentValue <= min){
                min = currentValue;
            }
        }
        if (createYAxis){
            yAxis = new NumberAxis(min - 25, max + 25, 1); //TODO warning 2000 major tick units
            createYAxis = false;
        }
        yAxis.setLowerBound(min-25);
        yAxis.setUpperBound(max+25);
    }

    private void start(){
        scheduledTask = scheduledExecutorService.scheduleAtFixedRate(() -> Platform.runLater(()-> {
            exchangeData = documentLoader.getExchangeData();
            exchangeSlider.setMax(exchangeData.length);
            updateExchangeLineChart();
        }), 0, 5, TimeUnit.SECONDS);
    }

    private void stop(){
        scheduledTask.cancel(true);
    }

    private void updateExchangeLineChart(){
        exchangeLineChart.setAnimated(false);
        exchangeSeries.getData().clear();
        computeExchangeLineChart(sliderData);
    }

    private void updateDailyLineChart(int numberOfDays){
        dailyLineChart.setAnimated(true);
        dailyMinSeries.getData().clear();
        dailyMaxSeries.getData().clear();


        for (int i = 1000 - numberOfDays; i < 1000; ++i) {
            dailyMinSeries.getData().add(new XYChart.Data<>(dailyData[i][0], Double.parseDouble(dailyData[i][4])));
            dailyMaxSeries.getData().add(new XYChart.Data<>(dailyData[i][0], Double.parseDouble(dailyData[i][6])));
        }
        dailyLineChart.setAnimated(false);
    }
}
