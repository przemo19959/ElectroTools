package application;
	
import org.controlsfx.control.InfoOverlay;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	private MainWindowController mainCon;
	private StackPane stackPane;
	private InfoOverlay infoOverLay;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			infoOverLay=new InfoOverlay();
			stackPane=new StackPane(infoOverLay);
			stackPane.setPrefHeight(400);
			stackPane.setPrefWidth(800);
			Scene scene = new Scene(stackPane);
			
			FXMLLoader loader=new FXMLLoader(getClass().getResource("mainWindow.fxml"));
			TabPane root = loader.load();
			mainCon=loader.getController();
			infoOverLay.setContent(root);
			infoOverLay.setShowOnHover(false);
			mainCon.setInfoOverlay(infoOverLay); //wstrzyknij
						
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
