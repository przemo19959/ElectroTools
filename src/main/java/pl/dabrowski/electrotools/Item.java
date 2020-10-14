package pl.dabrowski.electrotools;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class Item {
	private final String value;
	private final Color color;

	public Item(String[] args) {
		this.value = args[0];
		this.color = Color.valueOf(args[1]);
	}
}
