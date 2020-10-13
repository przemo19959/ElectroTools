package pl.dabrowski.electrotools.main;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.dabrowski.electrotools.store.StoreController;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	private MainWindowController controller1;
	private StoreController controller2;
	private Group root;

	@Override
	public void start(Stage primaryStage) {
		try {
			root = new Group();
			Scene scene = new Scene(root, 790, 400);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
			TabPane pane1 = loader.load();
			controller1 = loader.getController();

			loader = new FXMLLoader(StoreController.class.getResource("store.fxml"));
			AnchorPane pane2 = loader.load();
			controller2 = loader.getController();
			pane2.translateYProperty().set(pane1.getPrefHeight() - 25); //hide pane 2

			controller1.setController2(controller2);

			root.getChildren().addAll(pane1, pane2);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
