package pl.dabrowski.electrotools.store;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class StoreController {
	@FXML
	private AnchorPane anchor;
	@FXML
	private Rectangle background;

	private KeyValue outValue;
	private KeyFrame outFrame;

	private KeyValue inValue;
	private KeyFrame inFrame;
	@FXML private Label info;
	@FXML private ScrollPane scroll;

	@FXML
	private void initialize() {

		outValue = new KeyValue(anchor.translateYProperty(), 200);
		outFrame = new KeyFrame(Duration.seconds(0.5), outValue);

		inValue = new KeyValue(anchor.translateYProperty(), 380);
		inFrame = new KeyFrame(Duration.seconds(0.5), inValue);

		background.setOnMouseClicked(ev -> {
			Timeline timer = null;
			if(anchor.translateYProperty().get()== 200) {
				timer = new Timeline(inFrame);
				timer.play();
			} else {
				timer = new Timeline(outFrame);
				timer.play();
			}
		});
	}
	
	public void setInfo(String infoString) {
		info.setText(infoString);
	}
}
