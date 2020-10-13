package pl.dabrowski.electrotools;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Item {
	private final String value;
	private final Color color;	
}
