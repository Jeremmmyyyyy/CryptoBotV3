package CryptoBot.TradingBot;

import CryptoBot.WebService.TextManager;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TradingBotGUI {

    private final String fileName;
    private final String fileTag;
    private String[][] tradingBotData;
    private ScheduledExecutorService scheduledExecutorService;
    private Future<?> scheduledTask;
    private final TradingBotBasic tradingBot;
    private final TableView<TableRow> tableView = new TableView<>();
    private final XYChart.Series<String, Number> series1 = new XYChart.Series<>();
    private final XYChart.Series<String, Number> series2 = new XYChart.Series<>();
    private BarChart<String, Number> stackedBarChart;


    public TradingBotGUI(String fileName, TradingBotBasic tradingBot){
        DateTimeFormatter formatDateDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        this.fileTag = fileName;
        this.fileName = formatDateDay.format(now) + "_" + fileName + ".txt";
        this.tradingBot = tradingBot;
        tradingBotData = TextManager.readFromFileToTable(this.fileName);
    }

    public Node borderPane(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        start();

        return new BorderPane(center(), null, right(), bottom(), null);
    }

    private Node bottom(){
        VBox vBox = new VBox();


        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setMaxHeight(225);

        TableColumn<TableRow, String> hour = new TableColumn<>("Hour");
        hour.setCellValueFactory(new PropertyValueFactory<>("hour"));

        TableColumn<TableRow, String> transactionType = new TableColumn<>("Type");
        transactionType.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        transactionType.setMaxWidth(1300);

        TableColumn<TableRow, String> investedCapital = new TableColumn<>("Invested");
        investedCapital.setCellValueFactory(new PropertyValueFactory<>("investedCapital"));

        TableColumn<TableRow, String> investmentValue = new TableColumn<>("Investment Value");
        investmentValue.setCellValueFactory(new PropertyValueFactory<>("investmentValue"));

        TableColumn<TableRow, String> currentExchangeValue = new TableColumn<>("Current Rate");
        currentExchangeValue.setCellValueFactory(new PropertyValueFactory<>("currentExchangeValue"));

        TableColumn<TableRow, String> capital = new TableColumn<>("Capital");
        capital.setCellValueFactory(new PropertyValueFactory<>("capital"));

        TableColumn<TableRow, String> transactionPercent = new TableColumn<>("Buy / Sell Percent");
        transactionPercent.setCellValueFactory(new PropertyValueFactory<>("transactionPercent"));

        TableColumn<TableRow, String> percentVariation = new TableColumn<>("Variation");
        percentVariation.setCellValueFactory(new PropertyValueFactory<>("percentVariation"));

        tableView.getColumns().addAll(
                hour,
                transactionType,
                investedCapital,
                investmentValue,
                currentExchangeValue,
                capital,
                transactionPercent,
                percentVariation);

        for (String[] elem : tradingBotData) {
            tableView.getItems().add(new TableRow(elem[0], elem[1], elem[2], elem[3], elem[4], elem[5], elem[6], elem[7]));
        }


        vBox.getChildren().add(tableView);
        return vBox;
    }

    private Node center(){
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        stackedBarChart = new BarChart<>(xAxis, yAxis);
        stackedBarChart.setAnimated(false);
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        updateStackBarchart();

        stackedBarChart.getData().addAll(series2, series1);

        return stackedBarChart;
    }

    private Node right(){
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 10, 10, 10));
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        vBox.setSpacing(10);

        TextField textField = new TextField();
        textField.setPromptText("Amount");

        Text text = new Text("----------");

        RadioButton start = new RadioButton("Start");
        RadioButton stop = new RadioButton("Stop");
        ToggleGroup group = new ToggleGroup();
        start.setToggleGroup(group);
        stop.setToggleGroup(group);
        stop.setSelected(true);

        HBox hBoxFees = new HBox();
        TextField fees = new TextField();
        fees.setPromptText("Transaction Fees");
        CheckBox checkBox = new CheckBox();
        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()){
                fees.setDisable(true);
                if (isNumber(fees.getText())){
                    tradingBot.setTransactionFees(Double.parseDouble(fees.getText()));
                }else {
                    fees.clear();
                    fees.setPromptText("Not a Number");
                    checkBox.setSelected(false);
                    fees.setDisable(false);
                }
            }else{
                fees.setDisable(false);
            }
        });
        hBoxFees.getChildren().addAll(fees, checkBox);
        hBoxFees.setSpacing(10);

        Button clear = new Button("Clear file");
        clear.setOnAction(e -> {
            Stage stage = new Stage(StageStyle.UTILITY);
            stage.initModality(Modality.WINDOW_MODAL);
            HBox hBox1 = new HBox();
            BorderPane borderPane = new BorderPane(null, new Text("Are you sure you want to reset the file ?"), hBox1, null, null);
            stage.setScene(new Scene(borderPane));
            stage.setOnCloseRequest(Event::consume);
            Button yes  = new Button("Yes");
            Button no = new Button("No");
            hBox1.getChildren().addAll(yes, no);
            hBox1.setSpacing(10);
            yes.setOnAction(event -> {
                TextManager.delete(fileName);
                TextManager.createFile(fileTag);
                stage.close();
            });
            no.setOnAction(event -> {
                stage.close();
            });

            stage.show();
        });

        start.setOnAction(e -> {
            if(isNumber(textField.getText())){
                textField.setDisable(true);
                text.setText("Trading started with : \n" + textField.getText());
                tradingBot.setAmount(Double.parseDouble(textField.getText()));
                tradingBot.start();
            }else{
                text.setText("Error not a Number");
                stop.setSelected(true);
            }
        });
        stop.setOnAction(e -> {
            tradingBot.stop();
            text.setText("Trading stopped");
            textField.setDisable(false);
            tradingBotData = TextManager.readFromFileToTable(fileName);
            textField.setText(tradingBotData[tradingBotData.length - 1][5]);
        });

        hBox.getChildren().addAll(start, stop);
        vBox.getChildren().addAll(textField, hBox, text, hBoxFees,clear);
        return vBox;
    }


    private boolean isNumber(String isNumber){
        if(isNumber == null){
            return false;
        }
        try {
            Double.parseDouble(isNumber);
        }catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    private void updateStackBarchart(){
        stackedBarChart.getData().clear();
        series1.setName("Invested capital");
        series2.setName("Unused capital");
        series1.getData().clear();
        series2.getData().clear();

        if (tradingBotData.length > 15){
            for (int i = tradingBotData.length - 15; i < tradingBotData.length; i++) {
                series1.getData().add(new XYChart.Data<>(tradingBotData[i][0].substring(11, 19), Double.parseDouble(tradingBotData[i][3])));
                series2.getData().add(new XYChart.Data<>(tradingBotData[i][0].substring(11, 19), Double.parseDouble(tradingBotData[i][5])));
            }
        }else{
            for (String[] tradingBotDatum : tradingBotData) {
                series1.getData().add(new XYChart.Data<>(tradingBotDatum[0].substring(11, 19), Double.parseDouble(tradingBotDatum[3])));
                series2.getData().add(new XYChart.Data<>(tradingBotDatum[0].substring(11, 19), Double.parseDouble(tradingBotDatum[5])));
            }
        }
        stackedBarChart.getData().addAll(series2, series1);
    }



    private void start(){
        scheduledTask = scheduledExecutorService.scheduleAtFixedRate(() -> Platform.runLater(()-> {
            tradingBotData = TextManager.readFromFileToTable(fileName);
            tableView.getItems().clear();
            for (String[] elem : tradingBotData) {
                tableView.getItems().add(new TableRow(elem[0], elem[1], elem[2], elem[3], elem[4], elem[5], elem[6], elem[7]));
            }
            tableView.refresh();
            updateStackBarchart();

        }), 0, 5, TimeUnit.SECONDS);
    }

    private void stop(){scheduledTask.cancel(true);}
}
