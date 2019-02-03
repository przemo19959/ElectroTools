package application;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import application.files.FileService;
import application.files.FileServiceImpl;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;

public class MainWindowController {
	public static final String plusMinusSymbol = "\u00B1";
	public static final String multiplySymbol = "\u00D7";
	public static final String ohmSymbol = "\u2126";

	@FXML
	private TabPane mainPane;
	@FXML
	private ComboBox<Item> firstDigit;
	@FXML
	private ComboBox<Item> secondDigit;
	@FXML
	private ComboBox<Item> thirdDigit;
	@FXML
	private ComboBox<Item> multiplier;
	@FXML
	private ComboBox<Item> tolerance;
	private final List<Item> itemsVer1 = Arrays.asList(	new Item("0 Black", Color.BLACK), new Item("1 Brown", Color.SADDLEBROWN), new Item("2 Red", Color.RED), new Item("3 Orange", Color.ORANGE),
														new Item("4 Yellow", Color.YELLOW), new Item("5 Green", Color.GREEN), new Item("6 Blue", Color.DODGERBLUE),
														new Item("7 Violet", Color.MEDIUMVIOLETRED), new Item("8 Gray", Color.GRAY), new Item("9 White", Color.WHITE));

	private final List<Item> multipliers = Arrays.asList(	new Item(multiplySymbol+ "1 Black", Color.BLACK), new Item(multiplySymbol+ "10 Brown", Color.SADDLEBROWN),
															new Item(multiplySymbol+ "100 Red", Color.RED), new Item(multiplySymbol+ "1e3 Orange", Color.ORANGE),
															new Item(multiplySymbol+ "10e3 Yellow", Color.YELLOW), new Item(multiplySymbol+ "100e3 Green", Color.GREEN),
															new Item(multiplySymbol+ "1e6 Blue", Color.DODGERBLUE), new Item(multiplySymbol+ "10e6 Violet", Color.MEDIUMVIOLETRED),
															new Item(multiplySymbol+ "100e6 Gray", Color.GRAY), new Item(multiplySymbol+ "1e9 White", Color.WHITE),
															new Item(multiplySymbol+ "0.1 Gold", Color.GOLD), new Item(multiplySymbol+ "0.01 Silver", Color.SILVER));

	private final List<Item> tolerances = Arrays.asList(new Item(plusMinusSymbol+ "1% Brown", Color.SADDLEBROWN), new Item(plusMinusSymbol+ "2% Red", Color.RED),
														new Item(plusMinusSymbol+ "3% Orange", Color.ORANGE), new Item(plusMinusSymbol+ "4% Yellow", Color.YELLOW),
														new Item(plusMinusSymbol+ "0.5% Green", Color.GREEN), new Item(plusMinusSymbol+ "0.25% Blue", Color.DODGERBLUE),
														new Item(plusMinusSymbol+ "0.1% Violet", Color.MEDIUMVIOLETRED), new Item(plusMinusSymbol+ "0.05% Gray", Color.GRAY),
														new Item(plusMinusSymbol+ "5% Gold", Color.GOLD), new Item(plusMinusSymbol+ "10% Silver", Color.SILVER));
	@FXML
	private Rectangle strip1;
	@FXML
	private Rectangle strip2;
	@FXML
	private Rectangle strip3;
	@FXML
	private Rectangle strip4;
	@FXML
	private Rectangle strip5;
	@FXML
	private Label resistance;
	@FXML
	private Button insertButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button findButton;
	@FXML
	private Label info;
	private FileService fileService = new FileServiceImpl();
	private String resistorFileName = "resistors";
	@FXML
	private TextField quantityField;

	private String currentResistanceValue;
	private String currentTolerance;
	@FXML
	private TextField inputVoltage;
	@FXML
	private TextField outputVoltage;
	@FXML
	private Button voltageDivider;
	@FXML
	private TextField errorField;

	// na potrzeby testów
	public void setResistorFileName(String resistorFileName) {
		this.resistorFileName = resistorFileName;
	}

	/**
	 * Ta metoda aktualizuje wartoœci globalnych zmiennych przechowuj¹cych dane o aktualnie wprowadzonej poprzez comboboxy rezystancji i tolerancji.
	 */
	private void updateCurrentValues() {
		String fullName = resistance.getText();
		currentResistanceValue = fullName.substring(0, fullName.indexOf(plusMinusSymbol));
		currentTolerance = fullName.substring(fullName.indexOf(plusMinusSymbol), fullName.indexOf(ohmSymbol));
	}

	/**
	 * Ta funkcja aktualizuje stan komórek w comboboxach. Jeœli dodano do niej obiekt klasy <b>Item</b> to kolor t³a oraz tekst jest ustawiany zgodnie z polami tego obiektu.
	 * 
	 * @param cell
	 *            - komórka comboboxa.
	 * @param item
	 *            - obiekt dodano do tej komórki.
	 * @param empty
	 *            - flaga, sygnalizuj¹ca czy dana komórka jest pusta.
	 */
	private void updateItemCustom(ListCell<Item> cell, Item item, boolean empty) {
		if(empty|| item== null) {
			cell.setGraphic(null);
		} else {
			cell.setBackground(new Background(new BackgroundFill(item.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
			if(item.getValue().contains("Black"))
				cell.setStyle("-fx-text-fill:WHITE;");
			else
				cell.setStyle("-fx-text-fill:BLACK;");
			cell.setText(item.getValue());
		}
	}

	/**
	 * Ta metoda ustawia klienck¹ dekoracje danemu comboboxowi. Pocz¹tkowo dodawane s¹ elementy z wejœciowej listy do comboboxa. Nastêpnie ustawiany kliencki dekorator, który zale¿nie od pól obiektów
	 * z list ustawia tekst oraz kolor t³a danego elementu w comboxie.
	 * 
	 * @param box
	 *            - combobox.
	 * @param items
	 *            - lista obiektów klasy <b>Item</b> dodawana do comboboxa.
	 */
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

	/**
	 * Ta metoda dodaje zdarzenie nas³uchuj¹ce do danego comboboxa. Ka¿dy combobox jest powi¹zany z danym paskiem na rysunku rezystora. W momencie zmiany wartoœci comboboxa, zmianie ulega kolor danego
	 * paska, dodatkowo obliczana jest nowa wartoœæ rezystancji o wybranych kolorach pasków.
	 * 
	 * @param box
	 *            - combobox.
	 * @param strip
	 *            - pasek klasy <b>Rectangle</b> powiazany z danym comboboxem.
	 */
	private void addListener(ComboBox<Item> box, Rectangle strip) {
		box.getSelectionModel().selectedItemProperty().addListener((obs, old, next) -> {
			if(next!= old) {
				strip.setFill(next.getColor());
				calculateResistance();
				updateCurrentValues();
			}
		});
	}

	/**
	 * Ta metoda ustawia domyœlne wartoœci comboboxów (a zatem i powi¹zanych pasków).
	 */
	private void setBoxDefaultValues() {
		firstDigit.getSelectionModel().select(5);
		secondDigit.getSelectionModel().select(5);
		thirdDigit.getSelectionModel().select(6);
		multiplier.getSelectionModel().select(2);
		tolerance.getSelectionModel().select(9);
	}

	/**
	 * Tekst dla trzech pasków od lewej ma postaæ "<i>numer</i> kolor". Ta funkcja pobiera numer danego elementu z danego comboboxa.
	 * 
	 * @param box
	 *            - combobox.
	 * @return numer danego elementu
	 */
	private char getDigit(ComboBox<Item> box) {
		return box.getSelectionModel().getSelectedItem().getValue().charAt(0);
	}

	/**
	 * Ta metoda pobiera wartoœæ mno¿nika z przedostatniego paska do prawej. Zapisany jest on w formacie "x<b>wartoœæ mno¿nika_kolor</b>", gdzie znak _-oznacza spacjê. Przyk³adowo dla mno¿nika x1e3 ta
	 * metoda zwróci liczbê zer równ¹ 000.
	 * 
	 * @return wartoœæ mno¿nika aktualnie wybranego w postaci zer.
	 */
	private String getMuliplier() {
		String result = multiplier.getSelectionModel().getSelectedItem().getValue(); // pobierz element z comboboxa
		result = result.substring(result.indexOf(multiplySymbol)+ 2, result.lastIndexOf(" ")); // pobierz sam mno¿nik
		result = result.replace("e3", "000").replace("e6", "000000").replace("e9", "000000000");
		return result;
	}

	/**
	 * Ta metoda zwraca aktualnie wybran¹ tolerancjê, czyli ostatni pasek od lewej. Przechowywana jest ona w formacie "&#x00B1;<b>wartoœæ</b>%_kolor", gdzie _-to spacja. Ta funkcja zwraca element
	 * &#x00B1;<b>wartoœæ</b>%.
	 * 
	 * @return wartoœæ tolerancji aktualnie wybranej.
	 */
	private String getTolerance() {
		String result = tolerance.getSelectionModel().getSelectedItem().getValue(); // pobierz element z comboboxa
		result = result.substring(0, result.lastIndexOf(" "));
		return result;
	}

	/**
	 * Ta metoda wykonuje przeskalowanie wielkoœci stringa. Przyk³adowo 556 000 musi zostaæ zamienione na 556k. Tak jak w tym przypadku wystarczy zera 000 zamieniæ na odpowiednik jako k. Jeœli mamy
	 * inny przypadek 55 600 wtedy odpowiada to 55.6k, zatem nale¿y dodaæ znak kropki.
	 * 
	 * @param input
	 *            - string z wartoœci¹ rezystancji (przyk³adowo 5560).
	 * @param magnitude
	 *            - rz¹d wielkoœci stringa który odpowiada przyrostkowi (przyk³adowo dla 5560 rz¹d 3 ->5.56k)
	 * @param symbol
	 *            - symbol przyrostka (k,M lub G)
	 * @return przeskalowany string z wartoœci¹ rezystancji np.5.56k
	 */
	private String scaleValue(String input, int magnitude, String symbol) {
		int i = input.length()- magnitude;
		if(input.substring(i).replace("0", "").equals(""))
			return input.substring(0, i)+ symbol;
		return input.substring(0, i)+ "."+ input.substring(i).replace("0", "")+ symbol;
	}

	/**
	 * Ta metoda ma za zadanie przetworzyæ string przyk³adowo 55600 w string 55.6k.
	 * 
	 * @param value
	 *            - string wejœciowy (przyk³adowo 55600)
	 * @return wartoœæ rezystancji po przetworzeniu (przyk³adowo 55.6k)
	 */
	private String processResistance(String value) {
		if(value.charAt(value.length()- 2)== '.') // jeœli mno¿nik wynosi 0.1, wtedy mamy przyk³adowo 556.1 lub 22.1 (4 strips)
			if(strips5.isSelected())
				value = value.substring(0, 2)+ "."+ value.charAt(2); // to daje odpowiednio 55.6 oraz 22.. <-zle, patrz ni¿ej
			else
				value = value.charAt(0)+ "."+ value.charAt(1); // to daje 2.2
		else if(value.length()>= 3&& value.charAt(value.length()- 3)== '.') { // jeœli mno¿nik wynosi 0.01 wtedy mamy 556.01 lub 22.01 (4 strips)
			if(strips5.isSelected())
				value = value.charAt(0)+ "."+ value.substring(1, 3); // to daje odpowiednio 5.56 oraz 2.2. <-zle, patrz ni¿ej
			else
				value = value.charAt(0)+ "."+ value.charAt(1); // to daje 2.2
		} else if(value.length()> 9) // jeœli string wiêkszy ni¿ 9 znaków czyli np. 5 560 000 000 ->5.56G
			value = scaleValue(value, 9, "G");
		else if(value.length()> 6)
			value = scaleValue(value, 6, "M");
		else if(value.length()> 3)
			value = scaleValue(value, 3, "k");
		return value;
	}

	/**
	 * Ta metoda oblicza rezystancjê na podstawie ustawieñ comboboxów. Z trzech pierwszych pasków od lewej pobierane s¹ kolejne cyfry (ka¿dy kolor posiada unikaln¹ cyfrê). Nastêpnie dodawany jest
	 * mno¿nik w postaci zer. String o postaci np. 55600 musi zostaæ przetworzony do postaci 55.6k, odpowiada za to inna metoda. Po powrocie dodawana jest wartoœæ tolerancji oraz dodawany jest na
	 * koniec znak ohma co daje przyk³adowo 55.6k&#x00B1;10%&#x2126;.
	 */
	private void calculateResistance() {
		String res = "";
		res += getDigit(firstDigit);
		res += getDigit(secondDigit);
		if(strips5.isSelected())
			res += getDigit(thirdDigit);
		res += getMuliplier();

		res = processResistance(res);

		res += getTolerance();
		resistance.setText(res+ ohmSymbol);
	}

	/**
	 * Ta metoda obs³uguje sprawdzenie zawartoœci pola tekstowego, jeœli wpisany tekst jest zgodny z formatem podanym jako argument, to dany przycisk jest aktywny. W innym przypadku nie jest, a pole
	 * tekstowe jest podœwietlane na czerwono.
	 * 
	 * @param pattern
	 *            - wzór jaki ma spe³niaæ zawartoœæ pola tekstowego.
	 * @param button
	 *            - kontrolowany przycisk.
	 * @param fieldContent
	 *            - zawartoœæ pola tekstowego, która podlega sprawdzeniu
	 * @param field
	 *            - sprawdzane pole tekstowe.
	 */
	private void patternMatcher(String pattern, Button button, String fieldContent, TextField field) {
		if(!fieldContent.matches(pattern)) {
			field.setStyle("-fx-border-color: white white red white");
			button.setDisable(true);
		} else {
			field.setStyle("-fx-border-color: white white white white");
			button.setDisable(false);
		}
	}

	/**
	 * Identyczna funkcja jak <b>patternMatcher</b> z tym, ¿e kilka przycisków musi spe³niæ dany wzorzec. Jako parametr <b>index</b> podaje siê indeks flagi dla danego pola, jeœli wszystkie flagi w
	 * tablicy s¹ ustawione, wtedy dany przyciks jest aktywowany.
	 * 
	 * @param pattern
	 *            - wzór jaki ma spe³niaæ zawartoœæ pola tekstowego.
	 * @param index
	 *            - indeks flagi w tablicy.
	 * @param fieldContent-
	 *            zawartoœæ pola tekstowego, która podlega sprawdzeniu.
	 * @param field
	 *            - sprawdzane pole tekstowe.
	 */
	private void patternMatcher2(String pattern, int index, String fieldContent, TextField field) {
		if(!fieldContent.matches(pattern)) {
			field.setStyle("-fx-border-color: white white red white");
			flags[index] = 0;
		} else {
			field.setStyle("-fx-border-color: white white white white");
			flags[index] = 1;
		}
	}

	private int[] flags = new int[]{0, 0, 0};
	@FXML
	private Button selectAll;
	@FXML
	private RadioButton strips4;
	@FXML
	private RadioButton strips5;
	private ToggleGroup radioGroup = new ToggleGroup();
	@FXML
	private Label resistorsNumbersInStock;
	private StoreController controller2;

	public void setController2(StoreController controller2) {
		this.controller2 = controller2;
	}

	@FXML
	public void initialize() {
		strips4.setToggleGroup(radioGroup);
		strips5.setToggleGroup(radioGroup);

		radioGroup.selectedToggleProperty().addListener((obs, old, next) -> {
			String[] tmp = obs.toString().split("'");
			if(tmp[1].equals(strips4.getText())) {
				thirdDigit.setDisable(true);
				strip3.setFill(Color.TRANSPARENT);;
			} else {
				thirdDigit.setDisable(false);
				strip3.setFill(thirdDigit.getSelectionModel().getSelectedItem().getColor());
			}
			calculateResistance();
			updateCurrentValues();
		});

		// Dodanie klienckich dekoratorów do ka¿dego comboboxa
		addCustomCellFactory(firstDigit, itemsVer1.subList(1, itemsVer1.size()));
		addCustomCellFactory(secondDigit, itemsVer1);
		addCustomCellFactory(thirdDigit, itemsVer1);
		addCustomCellFactory(multiplier, multipliers);
		addCustomCellFactory(tolerance, tolerances);

		setBoxDefaultValues();

		addListener(firstDigit, strip1);
		addListener(secondDigit, strip2);
		addListener(thirdDigit, strip3);
		addListener(multiplier, strip4);
		addListener(tolerance, strip5);

		// po to aby wygenerowaæ zmianê i ¿eby obliczy³o wartoœæ rezystancji po w³¹czeniu aplikacji
		firstDigit.getSelectionModel().select(4);

		quantityField.textProperty().addListener((obs, old, next) -> {
			patternMatcher("\\d+", insertButton, next, quantityField);
		});

		TextField[] fields = new TextField[]{inputVoltage, outputVoltage, errorField};
		for(int i = 0;i< fields.length;i++) {
			final int index = i;
			fields[index].textProperty().addListener((obs, old, next) -> {
				patternMatcher2("(\\d+)|(\\d+,\\d+)", index, next, fields[index]);

				if(flags[0]== 1&& flags[1]== 1&& flags[2]== 1)
					voltageDivider.setDisable(false);
				else
					voltageDivider.setDisable(true);
			});
		}

		voltageDivider.setOnAction(ev -> {
			float error = Float.parseFloat(errorField.getText().replace(",", "."));
			float input = Float.parseFloat(inputVoltage.getText().replace(",", "."));
			float output = Float.parseFloat(outputVoltage.getText().replace(",", "."));
			try {
				controller2.setInfo(calculate(error, input, output));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		try {
			if(!fileService.txtFileExists(resistorFileName)) // pocz¹tkowa inicjalizacja, jeœli nie ma pliku to go stwórz
				fileService.createTxtFile(resistorFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		findButton.setOnAction(ev -> {
			try {
				controller2.setInfo(getAmountOfGivenResistor(currentResistanceValue));
				resistorsNumbersInStock.setText("");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		selectAll.setOnAction(ev -> {
			try {
				controller2.setInfo(selectAll());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		insertButton.setOnAction(ev -> {
			try {
				controller2.setInfo(insertOrUpdate(currentResistanceValue, Integer.valueOf(quantityField.getText()), currentTolerance));
				resistorsNumbersInStock.setText("");
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		deleteButton.setOnAction(ev -> {
			try {
				controller2.setInfo(deleteGivenResistorRecord(currentResistanceValue, currentTolerance));
				resistorsNumbersInStock.setText("");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Ta funkcja liczy napiêcie wyjœciowe dzielnika napiêciowego dla wszystkich mo¿liwych wariantów rezystorów znajduj¹cych siê w pliku. Pocz¹tkowo pobierana jest lista rekordów z pliku. Jeœli iloœæ
	 * rekordów jest wiêksza ni¿ 1, wtedy pobierane s¹ wszystkie ró¿ne (distinct) wartoœci rezystorów z pliku, podczas tego procesu wartoœci rezystancji s¹ konwertowane z postaci stringa do liczb
	 * zmiennoprzecinkowych. Funkcja liczy dla ka¿ej kombinacji rezystorów w pliku napiêcie wyjœciowe weg³ug zale¿noœci <b>Vout=Vin*(R1/(R1+R2))</b>, jeœli <i>output</i> -<b>Vout</b><<i>error</i> to
	 * taka kombinacja jest dodawana do wyjœciowego stringa. Dodatkowo jeœli dana kombinacja spe³nia warunek, to wyœwietlana jest iloœæ mo¿liwych do zbudowania takich dzielników przy obecnym stanie
	 * magazynu - zapisanego w pliku.
	 * 
	 * @param error
	 *            - b³¹d wzglêdny napiêcia wyjœciowego obliczonego a ¿¹danego
	 * @param input
	 *            - napiêcie wejœciowe dzielnika napiêciowego
	 * @param output
	 *            - ¿¹dane napiêcie wyjœciowe dzielnika
	 * @return Too few records - jesli plik zawiera 1 lub 0 rekordów.<br>
	 *         No resistors fulfilling given conditions - jeœli nie ma w pliku takiej kombinacji rezystorów spe³niaj¹cych warunki.<br>
	 *         default - string z kombinacjami rezystorów spe³niaj¹cych warunki
	 * @throws IOException
	 */
	public String calculate(float error, float input, float output) throws IOException {
		List<String> resistors = fileService.readTxtFile(resistorFileName); // odczytaj wszystkie rekordy
		if(resistors.isEmpty()|| resistors.size()== 1)
			return "Too few records";
		List<Record> records=resistors.stream().map(item -> new Record(item, FileService.separator)).collect(Collectors.toList());	//lista rekordów
		List<String> values=records.stream().map(item -> item.getColumnItem(0)).distinct().collect(Collectors.toList()); //lista wartoœci rezystancji w stringu
		Map<Float, String> map = values.stream().collect(Collectors.toMap(item->strResistanceToDouble(item),item->item)); //mapa wartoœci rezystancji w float oraz wartoœci w stringu
		Map<Float, Integer> numberInStock=values.stream().collect(Collectors.toMap(item->strResistanceToDouble(item), item->count(records, item))); //mapa wartoœci w float oraz iloœci w ca³ym magazynie
		float tmp = 0;
		String result = "";
		List<Float> resistances=map.keySet().stream().collect(Collectors.toList());
		float resistor1=0, resistor2=0;
		int available=0;
		for(int i = 0;i< values.size();i++) {
			for(int j = 0;j<values.size();j++) {
				resistor1=resistances.get(i);
				resistor2=resistances.get(j);
				if(!values.get(j).equals(values.get(i))) {
					tmp = input* (resistor1)/ (resistor1+ resistor2);
					if(Math.abs(output- tmp)< error) {
						available=(numberInStock.get(resistor1)>numberInStock.get(resistor2))?numberInStock.get(resistor2):numberInStock.get(resistor1);
						result += String.format("R1: %s"+ohmSymbol+", R2: %s"+ohmSymbol+", Vout: %.3e[V], Available: %d\n", map.get(resistor1), map.get(resistor2), tmp, available);
					}
				}
			}
		}
		if(result.equals(""))
			return "No resistors fulfilling given conditions";
		return result;
	}
	
	/**
	 * Ta funkcja zwraca iloœæ rezystorów w liœcie o podanej wartoœci.
	 * @param list - lista rekordów klasy <b>Record</b>
	 * @param value - wartoœæ rezystora zapisana w stringu (np. 2.2k)
	 * @return iloœæ wskazanego rezystora w liœcie.
	 */
	private int count(List<Record> list, String value) {
		int result=0;
		for(int i=0;i<list.size();i++) {
			if(list.get(i).getColumnItem(0).equals(value))
				result+=Integer.valueOf(list.get(i).getColumnItem(1));
		}
		return result;
	}

	/**
	 * Ta funkcja konwertuje wartoœæ rezystancji zapisan¹ w stringu do liczby zmiennoprzecinkowej. Przyk³adowo 2.3k odpowiada 2300f. Pocz¹tkowo sprawdzany jest znak na ostatnim indeksie (tam jest znak
	 * k,M lub G), który jest konwertowany na odpowiedni¹ liczbê. Nastêpnie w³aœciwa wartoœæ 2.3 w stringu zostaje sparsowana do float i wymno¿ona z rzêdem wielkoœci wczeœniej zdekodowanym.
	 * 
	 * @param value
	 *            - wartoœæ rezystancji zapisana w stringu.
	 * @return rezystancja jako liczba zmiennoprzecinkowa.
	 */
	private float strResistanceToDouble(String value) {
		float result = 1;
		switch (value.charAt(value.length()- 1)) {
			case 'k' :
				result *= 1000;
				break;
			case 'M' :
				result *= 1000000;
				break;
			case 'G' :
				result *= 1000000000;
				break;
		}
		result *= Float.valueOf(value.replaceAll("[kMG]", ""));
		return result;
	}

	/**
	 * Odpowiednik zapytania SELECT-ALL. Pocz¹tkowo wybierane s¹ wszystkie rekordy z pliku. Dodatkowo wyœwietlana jest iloœæ rekordów (aby u¿ytkownik nie musia³ ich liczyæ rêcznie). Funkcja zwraca
	 * string ze wszystkimi rekordami w formacie okreœlonym przez klasê <b>Record</b>.
	 * 
	 * @return No records - jeœli plik jest pusty. <br>
	 *         default - string ze wszystkimi rekordami.
	 * @throws IOException
	 */
	public String selectAll() throws IOException {
		List<String> resistors = fileService.readTxtFile(resistorFileName); // odczytaj wszystkie rekordy
		if(resistors.isEmpty())
			return "No records";
		resistorsNumbersInStock.setText("No: "+ resistors.size()); // wyœwietl iloœæ wszystkich rekordów znajduj¹cych siê w pliku
		return resistors.stream().map(item -> new Record(item, FileService.separator).toString()).collect(Collectors.joining("\n"));
	}

	/**
	 * Ta funkcja realizuje zapytanie SELECT-FROM-WHERE zwracaj¹c rekordy o podanych wartoœciach rezystancji. Pocz¹tkowo pobierana jest lista wszystkich rekordów. Nastêpnie zwracany jest string
	 * zawieraj¹cy dane o iloœci oraz tolerancji rezystorów o danej rezystancji w pliku. String ma formê "<i>tolerance</i> Quantity: <i>quantity</i>".
	 * 
	 * @param resistorValue
	 *            - wartoœæ rezystora, który ma byæ wyszukany w pliku
	 * @return No records - jesli plik jest pusty <br>
	 *         No resistors with given value in stock -jeœli danego rezystora nie ma w pliku <br>
	 *         default - string o podanym formacie zawieraj¹cy dane o iloœci i tolerancji danego rezystora
	 * @throws IOException
	 */
	public String getAmountOfGivenResistor(String resistorValue) throws IOException {
		List<String> resistors = fileService.readTxtFile(resistorFileName); // odczytaj wszystkie rekordy
		if(resistors.isEmpty())
			return "No records";
		String result = resistors	.stream().filter(record -> record.startsWith(resistorValue)) // wybierz te rekordy z ¿¹dan¹ wartoœci¹ rezystancji
									.map(item -> new Record(item, FileService.separator)) // utwórz z nich obiekty Record
									.map(item -> item.getColumnItem(2)+ " Quantity: "+ item.getColumnItem(1)) // utwórz stringi: tolerancja Quantity: iloœæ
									.collect(Collectors.joining("\n")); // po³¹cz znakiem \n
		if(result.equals(""))
			return "No resistors with given value in stock";
		return result;
	}

	/**
	 * Ta funkcja odpowiada zapytaniu DELETE. Pocz¹tkowo pobierane s¹ wszystkie rekordy. Podczas przeszukiwania szukany jest rekord o danej wartoœæ rezystancji i tolerancji, w przypadku znaleznienia
	 * ten rekord jest usuwany, inaczej nic siê dzieje.
	 * 
	 * @param resistorValue
	 *            - wartoœæ rezystora, który chcemy usun¹æ
	 * @param tolerance
	 *            - wartoœæ tolerancji rezystora, który ma byæ usuniêty
	 * @return Successfully deleted - jeœli usuniêto poprawnie <br>
	 *         No such resistor in stock - jeœli rezystora o podanych parametrach nie ma w pliku <br>
	 *         No records - jeœli plik jest pusty
	 * @throws IOException
	 */
	public String deleteGivenResistorRecord(String resistorValue, String tolerance) throws IOException {
		List<String> resistors = fileService.readTxtFile(resistorFileName); // odczytaj wszystkie rekordy
		if(resistors.isEmpty())
			return "No records";
		Record tmp = null;
		boolean deleted = false;
		for(int i = 0;i< resistors.size();i++) {
			tmp = new Record(resistors.get(i), FileService.separator);
			if(tmp.getColumnItem(0).equals(resistorValue)&& tmp.getColumnItem(2).equals(tolerance)) {
				resistors.remove(i);
				deleted = true;
				break;
			}
		}
		if(!deleted)
			return "No such resistor in stock";
		fileService.clearTxtFile(resistorFileName);
		fileService.writeTxtFile(resistorFileName, resistors.stream().collect(Collectors.joining("\n"))+ "\n"); // dodaj now¹ zawartoœæ
		return "Successfully deleted";
	}

	/**
	 * Ta funkcja wykonuje operacjê INSERT lub UPDATE analogicznie do zapytania SQL, tylko na pliku tekstowym. Pobierana jest lista wszystkich rekordów. Jeœli plik jest pusty, dodawany jest nowy
	 * rekord o wartoœciach jak w argumentach funkcji. W innym przypadku sprawdzane jest czy dany rezystor o danej tolerancji ju¿ znajduje siê w pliku. Jeœli nie to wykonywana jest operacja INSERT.
	 * Jeœli ju¿ taki jest to wykonywana jest operacja UPDATE która aktualizuje iloœæ danego rezystora o danej tolerancji.
	 * 
	 * @param resistorValue
	 *            - wartoœæ rezystora
	 * @param quantity
	 *            - iloœæ rezystorów
	 * @param tolerance
	 *            - tolerancja rezystora
	 * @return Success - jeœli poprawnie dodano/zauktualizowano rekord
	 * @throws IOException
	 */
	public String insertOrUpdate(String resistorValue, int quantity, String tolerance) throws IOException {
		List<String> resistors = fileService.readTxtFile(resistorFileName); // odczytaj wszystkie rekordy
		Record record = new Record(resistorValue+ FileService.separator+ quantity+ FileService.separator+ tolerance, FileService.separator);
		if(resistors.isEmpty()) // jeœli plik pusty, dodaj nowy rekord
			fileService.writeTxtFile(resistorFileName, record.toPrimaryFormat(FileService.separator)+ "\n"); // INSERT
		else { // jeœli plik nie jest pusty
			Record inStock = resistors	.stream().filter(item -> item.startsWith(resistorValue)).map(item -> new Record(item, FileService.separator))
										.filter(item -> item.getColumnItem(2).equals(tolerance)).findFirst().orElse(null); // zwróæ, jeœli rekord istnieje, inaczej null
			if(inStock== null)
				fileService.writeTxtFile(resistorFileName, record.toPrimaryFormat(FileService.separator)+ "\n"); // INSERT
			else {
				for(int i = 0;i< resistors.size();i++) {
					if(resistors.get(i).startsWith(resistorValue)) {
						Record rec = new Record(resistors.get(i), FileService.separator);
						if(rec.getColumnItem(2).equals(tolerance)) {
							rec.setColumnItem(1, quantity+ ""); // UPDATE
							resistors.set(i, rec.toPrimaryFormat(FileService.separator));
							break;
						}
					}
				}
				fileService.clearTxtFile(resistorFileName);
				fileService.writeTxtFile(resistorFileName, resistors.stream().collect(Collectors.joining("\n"))+ "\n");
			}
		}
		return "Success";
	}

	// na potrzeby testów
	// public static void main(String[] args) throws IOException {
	// MainWindowController con=new MainWindowController();
	// con.insertOrUpdate("55.6k", 12, MainWindowController.plusMinusSymbol+"10%");
	// con.insertOrUpdate("55.6k", 21, MainWindowController.plusMinusSymbol+"1%");
	// con.insertOrUpdate("55.2k", 2, MainWindowController.plusMinusSymbol+"1%");
	// con.insertOrUpdate("4.5M", 34, MainWindowController.plusMinusSymbol+"5%");
	// }
}
