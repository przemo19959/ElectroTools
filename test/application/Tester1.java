package application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import application.files.FileService;
import application.files.FileServiceImpl;

class Tester1 {
	private MainWindowController mockController=new MainWindowController();
	private FileService mockService=new FileServiceImpl();
	private final String mockData="55.6k:::12:::±10%\n" + 
			"55.6k:::21:::±1%\n" + 
			"55.2k:::2:::±1%\n4.5M:::34:::±5%\n";
	private final String mockData2="55.6k:::1234:::±10%\n" + 
			"55.6k:::2:::±1%\n" + 
			"55.2k:::245:::±1%\n4.5M:::3:::±5%\n";
	private final String mockData3="55.6k:::12:::±10%\n"+ 
			"55.2k:::2:::±1%\n4.5M:::34:::±5%\n";
	private final String fileName="mock";
	
	@BeforeEach
	void init() throws IOException {
		mockService.createTxtFile(fileName);
	}
	
	@Test
	@DisplayName("inserting to empty file works fine")
	void test1() throws IOException {
		mockController.setResistorFileName(fileName);
		mockController.insertOrUpdate("55.6k", 12, MainWindowController.plusMinusSymbol+"10%");
		mockController.insertOrUpdate("55.6k", 21, MainWindowController.plusMinusSymbol+"1%");
		mockController.insertOrUpdate("55.2k", 2, MainWindowController.plusMinusSymbol+"1%");
		mockController.insertOrUpdate("4.5M", 34, MainWindowController.plusMinusSymbol+"5%");	
		
		assertEquals(mockData, mockService.readTxtFile(fileName).stream().collect(Collectors.joining("\n"))+"\n");
	}
	
	@Test
	@DisplayName("updating already existing record works")
	void test2() throws IOException {
		mockController.setResistorFileName(fileName);
		mockController.insertOrUpdate("55.6k", 12, MainWindowController.plusMinusSymbol+"10%");
		mockController.insertOrUpdate("55.6k", 21, MainWindowController.plusMinusSymbol+"1%");
		mockController.insertOrUpdate("55.2k", 2, MainWindowController.plusMinusSymbol+"1%");
		mockController.insertOrUpdate("4.5M", 34, MainWindowController.plusMinusSymbol+"5%");
		
		mockController.insertOrUpdate("55.6k", 1234, MainWindowController.plusMinusSymbol+"10%");
		mockController.insertOrUpdate("55.6k", 2, MainWindowController.plusMinusSymbol+"1%");
		mockController.insertOrUpdate("55.2k", 245, MainWindowController.plusMinusSymbol+"1%");
		mockController.insertOrUpdate("4.5M", 3, MainWindowController.plusMinusSymbol+"5%");
		
		assertEquals(mockData2, mockService.readTxtFile(fileName).stream().collect(Collectors.joining("\n"))+"\n");
	}
	
	@Test
	@DisplayName("deleting record works fine")
	void test3() throws IOException {
		mockController.setResistorFileName(fileName);
		mockController.insertOrUpdate("55.6k", 12, MainWindowController.plusMinusSymbol+"10%");
		mockController.insertOrUpdate("55.6k", 21, MainWindowController.plusMinusSymbol+"1%");
		mockController.insertOrUpdate("55.2k", 2, MainWindowController.plusMinusSymbol+"1%");
		mockController.insertOrUpdate("4.5M", 34, MainWindowController.plusMinusSymbol+"5%");
		
		mockController.deleteGivenResistorRecord("55.6k", MainWindowController.plusMinusSymbol+"1%"); //usuñ
		
		assertEquals(mockData3, mockService.readTxtFile(fileName).stream().collect(Collectors.joining("\n"))+"\n");
	}
	
	@Test
	@DisplayName("calculating function works fine")
	void test4() throws IOException{
		mockController.setResistorFileName(fileName);
		mockController.insertOrUpdate("1.1k", 12, MainWindowController.plusMinusSymbol+"10%");
		mockController.insertOrUpdate("2.2M", 21, MainWindowController.plusMinusSymbol+"1%");
		mockController.insertOrUpdate("3.44k", 2, MainWindowController.plusMinusSymbol+"1%");
		mockController.insertOrUpdate("1G", 34, MainWindowController.plusMinusSymbol+"5%");
		
		System.out.println(mockController.calculate(1f,5,3.3f));
	}
	
	@AfterEach
	void clean() throws IOException {
		mockService.deleteTxtFile(fileName);
	}

}
