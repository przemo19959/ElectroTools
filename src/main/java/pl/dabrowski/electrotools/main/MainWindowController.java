package pl.dabrowski.electrotools.main;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import pl.dabrowski.electrotools.Item;
import pl.dabrowski.electrotools.Record;
import pl.dabrowski.electrotools.files.FileService;
import pl.dabrowski.electrotools.files.FileServiceImpl;
import pl.dabrowski.electrotools.initService.InitFromFileEngine;
import pl.dabrowski.electrotools.initService.InitFromFile;
import pl.dabrowski.electrotools.store.StoreController;

public class MainWindowController {
	private static final String ERROR_BORDER_STYLE = "-fx-border-color: white white red white";
	private static final String WHITE_BORDER_STYLE = "-fx-border-color: white white white white";
	public static final String PLUS_MINUS_SYMBOL = "\u00B1";
	public static final String MULTIPLY_SYMBOL = "\u00D7";
	public static final String OHM_SYMBOL = "\u2126";

	//@formatter:off
	@FXML private TabPane mainPane;
	@FXML private ComboBox<Item> firstDigit;
	@FXML private ComboBox<Item> secondDigit;
	@FXML private ComboBox<Item> thirdDigit;
	@FXML private ComboBox<Item> multiplier;
	@FXML private ComboBox<Item> tolerance;
	@FXML private Rectangle strip1;
	@FXML private Rectangle strip2;
	@FXML private Rectangle strip3;
	@FXML private Rectangle strip4;
	@FXML private Rectangle strip5;
	@FXML private Label resistance;
	@FXML private Button insertButton;
	@FXML private Button deleteButton;
	@FXML private Button findButton;
	@FXML private Label info;
	@FXML private TextField quantityField;
	@FXML private TextField inputVoltage;
	@FXML private TextField outputVoltage;
	@FXML private Button voltageDivider;
	@FXML private TextField errorField;
	@FXML private Button selectAll;
	@FXML private RadioButton strips4;
	@FXML private RadioButton strips5;
	@FXML private Label resistorsNumbersInStock;
	//@formatter:on

	@InitFromFile
	private List<Item> strips;
	@InitFromFile(prefixes = {MULTIPLY_SYMBOL})
	private List<Item> multipliers;
	@InitFromFile(prefixes= {PLUS_MINUS_SYMBOL})
	private List<Item> tolerances;

	private FileService fileService = new FileServiceImpl();
	private InitFromFileEngine initFromFileEngine = new InitFromFileEngine(this);
	private String resistorFileName = "resistors";
	private String currentResistanceValue;
	private String currentTolerance;

	// na potrzeby test�w
	public void setResistorFileName(String resistorFileName) {
		this.resistorFileName = resistorFileName;
	}

	/**
	 * Ta metoda aktualizuje warto�ci globalnych zmiennych przechowuj�cych dane o aktualnie wprowadzonej poprzez comboboxy rezystancji i tolerancji.
	 */
	private void updateCurrentValues() {
		String fullName = resistance.getText();
		currentResistanceValue = fullName.substring(0, fullName.indexOf(PLUS_MINUS_SYMBOL));
		currentTolerance = fullName.substring(fullName.indexOf(PLUS_MINUS_SYMBOL), fullName.indexOf(OHM_SYMBOL));
	}

	private void updateItemCustom(ListCell<Item> cell, Item item, boolean empty) {
		if(empty || item == null) {
			cell.setGraphic(null);
		} else {
			cell.setBackground(new Background(new BackgroundFill(item.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
			cell.setStyle(item.getValue().contains("Black") ? "-fx-text-fill:WHITE;" : "-fx-text-fill:BLACK;");
			cell.setText(item.getValue());
		}
	}

	private void addCustomCellFactory(ComboBox<Item> box, List<Item> items) {
		box.getItems().addAll(items);
		box.setCellFactory(callback -> new ListCell<Item>() {
			protected void updateItem(Item item, boolean empty) {
				super.updateItem(item, empty);
				updateItemCustom(this, item, empty);
			};
		});
		box.setButtonCell(box.getCellFactory().call(null));
	}

	private void setSelectedItemChangedHandler(ComboBox<Item> cBox, Rectangle strip) {
		cBox.getSelectionModel().selectedItemProperty().addListener((obs, old, next) -> {
			if(next != old) {
				strip.setFill(next.getColor());
				calculateResistance();
				updateCurrentValues();
			}
		});
	}

	private void setCBoxDefaultValues() {
		firstDigit.getSelectionModel().select(5);
		secondDigit.getSelectionModel().select(5);
		thirdDigit.getSelectionModel().select(6);
		multiplier.getSelectionModel().select(2);
		tolerance.getSelectionModel().select(9);
	}

	private char getSelectedItemDigitFromCBox(ComboBox<Item> box) {
		return box.getSelectionModel().getSelectedItem().getValue().charAt(0);
	}

	/**
	 * Ta metoda pobiera warto�� mno�nika z przedostatniego paska do prawej. Zapisany jest on w formacie "x<b>warto�� mno�nika_kolor</b>", gdzie znak _-oznacza spacj�. Przyk�adowo dla mno�nika x1e3 ta
	 * metoda zwr�ci liczb� zer r�wn� 000.
	 * 
	 * @return warto�� mno�nika aktualnie wybranego w postaci zer.
	 */
	private String getMuliplier() {
		String result = multiplier.getSelectionModel().getSelectedItem().getValue(); // pobierz element z comboboxa
		result = result.substring(result.indexOf(MULTIPLY_SYMBOL) + 2, result.lastIndexOf(" ")); // pobierz sam mno�nik
		result = result.replace("e3", "000").replace("e6", "000000").replace("e9", "000000000");
		return result;
	}

	/**
	 * Ta metoda zwraca aktualnie wybran� tolerancj�, czyli ostatni pasek od lewej. Przechowywana jest ona w formacie "&#x00B1;<b>warto��</b>%_kolor", gdzie _-to spacja. Ta funkcja zwraca element
	 * &#x00B1;<b>warto��</b>%.
	 * 
	 * @return warto�� tolerancji aktualnie wybranej.
	 */
	private String getTolerance() {
		String result = tolerance.getSelectionModel().getSelectedItem().getValue(); // pobierz element z comboboxa
		result = result.substring(0, result.lastIndexOf(" "));
		return result;
	}

	/**
	 * Ta metoda wykonuje przeskalowanie wielko�ci stringa. Przyk�adowo 556 000 musi zosta� zamienione na 556k. Tak jak w tym przypadku wystarczy zera 000 zamieni� na odpowiednik jako k. Je�li mamy
	 * inny przypadek 55 600 wtedy odpowiada to 55.6k, zatem nale�y doda� znak kropki.
	 * 
	 * @param input - string z warto�ci� rezystancji (przyk�adowo 5560).
	 * @param magnitude - rz�d wielko�ci stringa kt�ry odpowiada przyrostkowi (przyk�adowo dla 5560 rz�d 3 ->5.56k)
	 * @param symbol - symbol przyrostka (k,M lub G)
	 * @return przeskalowany string z warto�ci� rezystancji np.5.56k
	 */
	private String scaleValue(String input, int magnitude, String symbol) {
		int i = input.length() - magnitude;
		if(input.substring(i).replace("0", "").equals(""))
			return input.substring(0, i) + symbol;
		return input.substring(0, i) + "." + input.substring(i).replace("0", "") + symbol;
	}

	/**
	 * Ta metoda ma za zadanie przetworzy� string przyk�adowo 55600 w string 55.6k.
	 * 
	 * @param value - string wej�ciowy (przyk�adowo 55600)
	 * @return warto�� rezystancji po przetworzeniu (przyk�adowo 55.6k)
	 */
	private String processResistance(String value) {
		if(value.charAt(value.length() - 2) == '.') // je�li mno�nik wynosi 0.1, wtedy mamy przyk�adowo 556.1 lub 22.1 (4 strips)
			if(strips5.isSelected())
				value = value.substring(0, 2) + "." + value.charAt(2); // to daje odpowiednio 55.6 oraz 22.. <-zle, patrz ni�ej
			else
				value = value.charAt(0) + "." + value.charAt(1); // to daje 2.2
		else if(value.length() >= 3 && value.charAt(value.length() - 3) == '.') { // je�li mno�nik wynosi 0.01 wtedy mamy 556.01 lub 22.01 (4 strips)
			if(strips5.isSelected())
				value = value.charAt(0) + "." + value.substring(1, 3); // to daje odpowiednio 5.56 oraz 2.2. <-zle, patrz ni�ej
			else
				value = value.charAt(0) + "." + value.charAt(1); // to daje 2.2
		} else if(value.length() > 9) // je�li string wi�kszy ni� 9 znak�w czyli np. 5 560 000 000 ->5.56G
			value = scaleValue(value, 9, "G");
		else if(value.length() > 6)
			value = scaleValue(value, 6, "M");
		else if(value.length() > 3)
			value = scaleValue(value, 3, "k");
		return value;
	}

	/**
	 * Ta metoda oblicza rezystancj� na podstawie ustawie� combobox�w. Z trzech pierwszych pask�w od lewej pobierane s� kolejne cyfry (ka�dy kolor posiada unikaln� cyfr�). Nast�pnie dodawany jest
	 * mno�nik w postaci zer. String o postaci np. 55600 musi zosta� przetworzony do postaci 55.6k, odpowiada za to inna metoda. Po powrocie dodawana jest warto�� tolerancji oraz dodawany jest na
	 * koniec znak ohma co daje przyk�adowo 55.6k&#x00B1;10%&#x2126;.
	 */
	private void calculateResistance() {
		String res = "";
		res += getSelectedItemDigitFromCBox(firstDigit);
		res += getSelectedItemDigitFromCBox(secondDigit);
		if(strips5.isSelected())
			res += getSelectedItemDigitFromCBox(thirdDigit);
		res += getMuliplier();

		res = processResistance(res);

		res += getTolerance();
		resistance.setText(res + OHM_SYMBOL);
	}

	private void patternMatcher(String pattern, Button button, String fieldContent, TextField field) {
		boolean patternMatches = fieldContent.matches(pattern);
		field.setStyle(patternMatches ? WHITE_BORDER_STYLE : ERROR_BORDER_STYLE);
		button.setDisable(patternMatches == false);
	}

	private void patternMatcher2(String pattern, int index, String fieldContent, TextField field) {
		boolean patternMatches = fieldContent.matches(pattern);
		field.setStyle(patternMatches ? WHITE_BORDER_STYLE : ERROR_BORDER_STYLE);
		flags[index] = patternMatches ? 1 : 0;
	}

	private int[] flags = new int[]{0, 0, 0};
	private ToggleGroup radioGroup = new ToggleGroup();
	private StoreController controller2;

	public void setController2(StoreController controller2) {
		this.controller2 = controller2;
	}

	@FXML
	public void initialize() {
		initFromFileEngine.initFields();
		System.out.println(strips);

		strips4.setToggleGroup(radioGroup);
		strips5.setToggleGroup(radioGroup);

		radioGroup.selectedToggleProperty().addListener((obs, old, next) -> {
			boolean is4StripsSelected = obs.toString().split("'")[1].equals(strips4.getText());
			thirdDigit.setDisable(is4StripsSelected);
			strip3.setFill(is4StripsSelected ? Color.TRANSPARENT : thirdDigit.getSelectionModel().getSelectedItem().getColor());
			calculateResistance();
			updateCurrentValues();
		});

		addCustomCellFactory(firstDigit, strips.subList(1, strips.size()));
		addCustomCellFactory(secondDigit, strips);
		addCustomCellFactory(thirdDigit, strips);
		addCustomCellFactory(multiplier, multipliers);
		addCustomCellFactory(tolerance, tolerances);

		setCBoxDefaultValues();

		setSelectedItemChangedHandler(firstDigit, strip1);
		setSelectedItemChangedHandler(secondDigit, strip2);
		setSelectedItemChangedHandler(thirdDigit, strip3);
		setSelectedItemChangedHandler(multiplier, strip4);
		setSelectedItemChangedHandler(tolerance, strip5);

		firstDigit.getSelectionModel().select(4); //must be here, so that digit changes and resistance value is calculated
		quantityField.textProperty().addListener((obs, old, next) -> patternMatcher("\\d+", insertButton, next, quantityField));

		TextField[] fields = new TextField[]{inputVoltage, outputVoltage, errorField};
		for(int i = 0;i < fields.length;i++) {
			final int index = i;
			fields[index].textProperty().addListener((obs, old, next) -> {
				patternMatcher2("(\\d+)|(\\d+,\\d+)", index, next, fields[index]);
				voltageDivider.setDisable((flags[0] == 1 && flags[1] == 1 && flags[2] == 1) == false);
			});
		}

		voltageDivider.setOnAction(ev -> {
			float error = Float.parseFloat(errorField.getText().replace(",", "."));
			float input = Float.parseFloat(inputVoltage.getText().replace(",", "."));
			float output = Float.parseFloat(outputVoltage.getText().replace(",", "."));
			controller2.setInfo(calculate(error, input, output));
		});

		if(fileService.txtFileExists(resistorFileName) == false) // pocz�tkowa inicjalizacja, je�li nie ma pliku to go stw�rz
			fileService.createTxtFile(resistorFileName);

		//CRUD operation handlers
		findButton.setOnAction(ev -> {
			controller2.setInfo(getAmountOfGivenResistor(currentResistanceValue));
			resistorsNumbersInStock.setText("");
		});

		selectAll.setOnAction(ev -> controller2.setInfo(selectAll()));

		insertButton.setOnAction(ev -> {
			controller2.setInfo(insertOrUpdate(currentResistanceValue, Integer.valueOf(quantityField.getText()), currentTolerance));
			resistorsNumbersInStock.setText("");
		});

		deleteButton.setOnAction(ev -> {
			controller2.setInfo(deleteGivenResistorRecord(currentResistanceValue, currentTolerance));
			resistorsNumbersInStock.setText("");
		});
	}

	/**
	 * Ta funkcja liczy napi�cie wyj�ciowe dzielnika napi�ciowego dla wszystkich mo�liwych wariant�w rezystor�w znajduj�cych si� w pliku. Pocz�tkowo pobierana jest lista rekord�w z pliku. Je�li ilo��
	 * rekord�w jest wi�ksza ni� 1, wtedy pobierane s� wszystkie r�ne (distinct) warto�ci rezystor�w z pliku, podczas tego procesu warto�ci rezystancji s� konwertowane z postaci stringa do liczb
	 * zmiennoprzecinkowych. Funkcja liczy dla ka�ej kombinacji rezystor�w w pliku napi�cie wyj�ciowe weg�ug zale�no�ci <b>Vout=Vin*(R1/(R1+R2))</b>, je�li <i>output</i> -<b>Vout</b><<i>error</i> to
	 * taka kombinacja jest dodawana do wyj�ciowego stringa. Dodatkowo je�li dana kombinacja spe�nia warunek, to wy�wietlana jest ilo�� mo�liwych do zbudowania takich dzielnik�w przy obecnym stanie
	 * magazynu - zapisanego w pliku.
	 * 
	 * @param error - b��d wzgl�dny napi�cia wyj�ciowego obliczonego a ��danego
	 * @param input - napi�cie wej�ciowe dzielnika napi�ciowego
	 * @param output - ��dane napi�cie wyj�ciowe dzielnika
	 * @return Too few records - jesli plik zawiera 1 lub 0 rekord�w.<br>
	 * No resistors fulfilling given conditions - je�li nie ma w pliku takiej kombinacji rezystor�w spe�niaj�cych warunki.<br>
	 * default - string z kombinacjami rezystor�w spe�niaj�cych warunki
	 * @throws IOException
	 */
	public String calculate(float error, float input, float output) {
		List<String> resistors = fileService.readTxtFile(resistorFileName); // odczytaj wszystkie rekordy
		if(resistors.isEmpty() || resistors.size() == 1)
			return "Too few records";
		List<Record> records = resistors.stream().map(item -> new Record(item, FileService.separator)).collect(Collectors.toList()); //lista rekord�w
		List<String> values = records.stream().map(item -> item.getItem(0)).distinct().collect(Collectors.toList()); //lista warto�ci rezystancji w stringu
		Map<Float, String> map = values.stream().collect(Collectors.toMap(item -> resistanceValueOf(item), item -> item)); //mapa warto�ci rezystancji w float oraz warto�ci w stringu
		Map<Float, Integer> numberInStock = values.stream().collect(Collectors.toMap(item -> resistanceValueOf(item), item -> count(records, item))); //mapa warto�ci w float oraz ilo�ci w ca�ym magazynie
		float tmp = 0;
		String result = "";
		List<Float> resistances = map.keySet().stream().collect(Collectors.toList());
		float resistor1 = 0,resistor2 = 0;
		int available = 0;
		for(int i = 0;i < values.size();i++) {
			for(int j = 0;j < values.size();j++) {
				resistor1 = resistances.get(i);
				resistor2 = resistances.get(j);
				if(!values.get(j).equals(values.get(i))) {
					tmp = input * (resistor1) / (resistor1 + resistor2);
					if(Math.abs(output - tmp) < error) {
						available = (numberInStock.get(resistor1) > numberInStock.get(resistor2)) ? numberInStock.get(resistor2) : numberInStock.get(resistor1);
						result += String.format("R1: %s" + OHM_SYMBOL + ", R2: %s" + OHM_SYMBOL + ", Vout: %.3e[V], Available: %d\n", map.get(resistor1), map.get(resistor2), tmp, available);
					}
				}
			}
		}
		if(result.equals(""))
			return "No resistors fulfilling given conditions";
		return result;
	}

	/**
	 * Ta funkcja zwraca ilo�� rezystor�w w li�cie o podanej warto�ci.
	 * 
	 * @param list - lista rekord�w klasy <b>Record</b>
	 * @param value - warto�� rezystora zapisana w stringu (np. 2.2k)
	 * @return ilo�� wskazanego rezystora w li�cie.
	 */
	private int count(List<Record> list, String value) {
		int result = 0;
		for(int i = 0;i < list.size();i++) {
			if(list.get(i).getItem(0).equals(value))
				result += Integer.valueOf(list.get(i).getItem(1));
		}
		return result;
	}

	private float resistanceValueOf(String value) {
		float result = 1;
		switch (value.charAt(value.length() - 1)) {//@formatter:off
			case 'k' :result *= 1000;break;
			case 'M' :result *= 1000000;break;
			case 'G' :result *= 1000000000;break;
		}//@formatter:on
		result *= Float.valueOf(value.replaceAll("[kMG]", ""));
		return result;
	}

	public String selectAll() {
		List<String> resistors = fileService.readTxtFile(resistorFileName); // odczytaj wszystkie rekordy
		if(resistors.isEmpty())
			return "No records";
		resistorsNumbersInStock.setText("No: " + resistors.size()); // wy�wietl ilo�� wszystkich rekord�w znajduj�cych si� w pliku
		return resistors.stream().map(item -> new Record(item, FileService.separator).toString()).collect(Collectors.joining("\n"));
	}

	/**
	 * Ta funkcja realizuje zapytanie SELECT-FROM-WHERE zwracaj�c rekordy o podanych warto�ciach rezystancji. Pocz�tkowo pobierana jest lista wszystkich rekord�w. Nast�pnie zwracany jest string
	 * zawieraj�cy dane o ilo�ci oraz tolerancji rezystor�w o danej rezystancji w pliku. String ma form� "<i>tolerance</i> Quantity: <i>quantity</i>".
	 * 
	 * @param resistorValue - warto�� rezystora, kt�ry ma by� wyszukany w pliku
	 * @return No records - jesli plik jest pusty <br>
	 * No resistors with given value in stock -je�li danego rezystora nie ma w pliku <br>
	 * default - string o podanym formacie zawieraj�cy dane o ilo�ci i tolerancji danego rezystora
	 * @throws IOException
	 */
	public String getAmountOfGivenResistor(String resistorValue) {
		List<String> resistors = fileService.readTxtFile(resistorFileName); // odczytaj wszystkie rekordy
		if(resistors.isEmpty())
			return "No records";
		String result = resistors.stream().map(item -> new Record(item, FileService.separator)) // utw�rz z nich obiekty Record
			.filter(record -> record.getItem(0).equals(resistorValue)) // wybierz te rekordy z ��dan� warto�ci� rezystancji
			.map(item -> item.getItem(2) + " Quantity: " + item.getItem(1)) // utw�rz stringi: tolerancja Quantity: ilo��
			.collect(Collectors.joining("\n")); // po��cz znakiem \n
		if(result.equals(""))
			return "No resistors with given value in stock";
		return result;
	}

	/**
	 * Ta funkcja odpowiada zapytaniu DELETE. Pocz�tkowo pobierane s� wszystkie rekordy. Podczas przeszukiwania szukany jest rekord o danej warto�� rezystancji i tolerancji, w przypadku znaleznienia
	 * ten rekord jest usuwany, inaczej nic si� dzieje.
	 * 
	 * @param resistorValue - warto�� rezystora, kt�ry chcemy usun��
	 * @param tolerance - warto�� tolerancji rezystora, kt�ry ma by� usuni�ty
	 * @return Successfully deleted - je�li usuni�to poprawnie <br>
	 * No such resistor in stock - je�li rezystora o podanych parametrach nie ma w pliku <br>
	 * No records - je�li plik jest pusty
	 * @throws IOException
	 */
	public String deleteGivenResistorRecord(String resistorValue, String tolerance) {
		List<String> resistors = fileService.readTxtFile(resistorFileName); // odczytaj wszystkie rekordy
		if(resistors.isEmpty())
			return "No records";
		Record tmp = null;
		boolean deleted = false;
		for(int i = 0;i < resistors.size();i++) {
			tmp = new Record(resistors.get(i), FileService.separator);
			if(tmp.getItem(0).equals(resistorValue) && tmp.getItem(2).equals(tolerance)) {
				resistors.remove(i);
				deleted = true;
				break;
			}
		}
		if(!deleted)
			return "No such resistor in stock";
		fileService.clearTxtFile(resistorFileName);
		fileService.writeTxtFile(resistorFileName, resistors.stream().collect(Collectors.joining("\n")) + "\n"); // dodaj now� zawarto��
		return "Successfully deleted";
	}

	/**
	 * Ta funkcja wykonuje operacj� INSERT lub UPDATE analogicznie do zapytania SQL, tylko na pliku tekstowym. Pobierana jest lista wszystkich rekord�w. Je�li plik jest pusty, dodawany jest nowy
	 * rekord o warto�ciach jak w argumentach funkcji. W innym przypadku sprawdzane jest czy dany rezystor o danej tolerancji ju� znajduje si� w pliku. Je�li nie to wykonywana jest operacja INSERT.
	 * Je�li ju� taki jest to wykonywana jest operacja UPDATE kt�ra aktualizuje ilo�� danego rezystora o danej tolerancji.
	 * 
	 * @param resistorValue - warto�� rezystora
	 * @param quantity - ilo�� rezystor�w
	 * @param tolerance - tolerancja rezystora
	 * @return Success - je�li poprawnie dodano/zauktualizowano rekord
	 * @throws IOException
	 */
	public String insertOrUpdate(String resistorValue, int quantity, String tolerance) {
		List<String> resistors = fileService.readTxtFile(resistorFileName); // odczytaj wszystkie rekordy
		Record record = new Record(resistorValue + FileService.separator + quantity + FileService.separator + tolerance, FileService.separator);
		if(resistors.isEmpty()) // je�li plik pusty, dodaj nowy rekord
			fileService.writeTxtFile(resistorFileName, record.toPrimaryFormat(FileService.separator) + "\n"); // INSERT
		else { // je�li plik nie jest pusty
			Record inStock = resistors.stream().map(item -> new Record(item, FileService.separator)).filter(item -> item.getItem(0).equals(resistorValue)).filter(item -> item.getItem(2).equals(
				tolerance)).findFirst().orElse(null); // zwr��, je�li rekord istnieje, inaczej null
			if(inStock == null)
				fileService.writeTxtFile(resistorFileName, record.toPrimaryFormat(FileService.separator) + "\n"); // INSERT
			else {
				for(int i = 0;i < resistors.size();i++) {
					if(resistors.get(i).startsWith(resistorValue)) {
						Record rec = new Record(resistors.get(i), FileService.separator);
						if(rec.getItem(2).equals(tolerance)) {
							rec.setItem(1, quantity + ""); // UPDATE
							resistors.set(i, rec.toPrimaryFormat(FileService.separator));
							break;
						}
					}
				}
				fileService.clearTxtFile(resistorFileName);
				fileService.writeTxtFile(resistorFileName, resistors.stream().collect(Collectors.joining("\n")) + "\n");
			}
		}
		return "Success";
	}

	// na potrzeby test�w
	// public static void main(String[] args) throws IOException {
	// MainWindowController con=new MainWindowController();
	// con.insertOrUpdate("55.6k", 12, MainWindowController.plusMinusSymbol+"10%");
	// con.insertOrUpdate("55.6k", 21, MainWindowController.plusMinusSymbol+"1%");
	// con.insertOrUpdate("55.2k", 2, MainWindowController.plusMinusSymbol+"1%");
	// con.insertOrUpdate("4.5M", 34, MainWindowController.plusMinusSymbol+"5%");
	// }
}
