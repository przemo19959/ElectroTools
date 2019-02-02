package application;

import javafx.scene.paint.Color;

public class Item {
	private final String value;
	private final Color color;
	
	public Item(String value, Color color) {
		this.value = value;
		this.color = color;
	}

	public String getValue() {
		return value;
	}

	public Color getColor() {
		return color;
	}
}
