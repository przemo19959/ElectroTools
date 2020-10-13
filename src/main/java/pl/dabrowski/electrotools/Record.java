package pl.dabrowski.electrotools;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Record {
	private final static int NUMBER_OF_COLUMNS = 3;
	private String[] items = new String[NUMBER_OF_COLUMNS];

	public Record(String record, String separator) {
		items = record.split(separator);
	}
	
	//@formatter:off
	public String getItem(int index) {return items[index];}
	public void setItem(int index, String newItem) {items[index] = newItem;}
	public String toPrimaryFormat(String separator) {return Arrays.stream(items).collect(Collectors.joining(separator));}
	//@formatter:on

	@Override
	public String toString() {
		return String.format("R: %s, Q: %s, T: %s", items[0], items[1], items[2]);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(!(obj instanceof Record))
			return false;
		Record that = (Record) obj;
		if(!items[0].equals(that.items[0]) && !items[1].equals(that.items[1]) && !items[2].equals(that.items[2]))
			return false;
		return true;
	}
}
