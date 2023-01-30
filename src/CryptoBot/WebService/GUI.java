package CryptoBot.WebService;

import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public final class GUI {

    private static final Stage mainStage = new Stage();
    private static final ObservableRequestHandler observableRequestHandler = new ObservableRequestHandler();
    private static final RequestHandler requestHandler = new RequestHandler(observableRequestHandler);

    public GUI(){
        mainStage.setScene(new Scene(new BorderPane(center(), null, null, bottom(), null)));
        mainStage.setTitle("W.R.S"); // Web Request Service
        mainStage.setWidth(250);
        mainStage.setHeight(225);
        mainStage.setOnCloseRequest(Event::consume);
        mainStage.setResizable(false);
        mainStage.show();
        observableRequestHandler.setAll(requestHandler);
    }

    public static Node center(){
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        vBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox hBox1 = new HBox();
        hBox1.setPadding(new Insets(2, 5, 3,5));

        HBox hBox2 = new HBox();
        hBox2.setPadding(new Insets(0, 5, 3,5));

        HBox hBox3 = new HBox();
        hBox3.setPadding(new Insets(0, 5, 3,5));

        HBox hBox4 = new HBox();
        hBox4.setPadding(new Insets(0, 5, 3,5));

        HBox hBox5 = new HBox();
        hBox5.setPadding(new Insets(0, 5, 2,5));

        Text refresh = new Text("Refresh rate : ");
        refresh.setFill(Color.LIGHTGREEN);
        Text counter = new Text();
        counter.setFill(Color.LIGHTGREEN);
        Text slash = new Text(" / ");
        slash.setFill(Color.LIGHTGREEN);
        Text delay = new Text();
        delay.setFill(Color.LIGHTGREEN);

        Text internet = new Text("Internet Connection : ");
        internet.setFill(Color.LIGHTGREEN);
        Text internetConnection = new Text();
        internetConnection.setFill(Color.LIGHTGREEN);

        Text numberOC = new Text("Number of Calls today : ");
        numberOC.setFill(Color.LIGHTGREEN);
        Text numberOfCalls = new Text();
        numberOfCalls.setFill(Color.LIGHTGREEN);

        Text existingFiles = new Text("Files exist : ");
        existingFiles.setFill(Color.LIGHTGREEN);
        Text dailyData = new Text();
        dailyData.setFill(Color.LIGHTGREEN);
        Text space = new Text(" and ");
        space.setFill(Color.LIGHTGREEN);
        Text currentData = new Text();
        currentData.setFill(Color.LIGHTGREEN);

        counter.textProperty().bind(Bindings.convert(observableRequestHandler.getCounter()));
        delay.textProperty().bind(Bindings.convert(observableRequestHandler.getDelay()));
        internetConnection.textProperty().bind(Bindings.convert(observableRequestHandler.getInternetConnection()));
        numberOfCalls.textProperty().bind(Bindings.convert(observableRequestHandler.getNumberOfCalls()));
        dailyData.textProperty().bind(Bindings.convert(observableRequestHandler.getExistingDailyData()));
        currentData.textProperty().bind(Bindings.convert(observableRequestHandler.getExistingCurrentData()));

        hBox1.getChildren().addAll(refresh, counter, slash, delay);
        hBox2.getChildren().addAll(internet, internetConnection);
        hBox3.getChildren().addAll(numberOC, numberOfCalls);
        hBox4.getChildren().addAll(existingFiles, dailyData, space, currentData);

        vBox.getChildren().addAll(hBox1, hBox2, hBox3, hBox4, hBox5);
        return vBox;
    }

    public static Node bottom(){
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 10 , 10, 400));
        hBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        RadioButton start = new RadioButton("Start");
        start.setTextFill(Color.GREEN);
        RadioButton stop = new RadioButton("Stop");
        stop.setTextFill(Color.RED);
        ToggleGroup groupRadioButton = new ToggleGroup();
        start.setToggleGroup(groupRadioButton);
        start.setSelected(false);
        stop.setToggleGroup(groupRadioButton);
        stop.setSelected(true);

        hBox.getChildren().addAll(start, stop);
        hBox.setSpacing(30);
        hBox.setPadding(new Insets(10, 10, 10, 10));


        start.setOnAction(e -> new Thread(requestHandler::start).start());

        stop.setOnAction(e -> new Thread(requestHandler::stop).start());

        return hBox;
    }

    public static Node right(){
        VBox vBox = new VBox();
        vBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        vBox.setPadding(new Insets(20, 20, 20 , 20));
        vBox.setSpacing(10);
        Text text1 = new Text("These calls will not be counted");
        Text text2 = new Text("Only use if necessary !");
        text1.setFill(Color.RED);
        text2.setFill(Color.RED);
        Button requestNewDaily = new Button("Request new Daily Data Immediately");
        Button requestNewExchange = new Button("Request new Exchange Data Immediately");
        requestNewDaily.setOnAction(e-> requestHandler.requestDailyData());
        requestNewExchange.setOnAction(e -> requestHandler.requestCurrentData(RequestHandler.ApiKey.API_KEY_1.key));
        vBox.getChildren().addAll(text1, requestNewDaily, requestNewExchange, text2);
        return vBox;
    }
}
