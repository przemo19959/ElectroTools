package pl.dabrowski.electrotools.files;

import java.util.List;

public interface FileService {
	public final static String pathToMainFolder="D:/java-workspace/ElectroTools/stock"; //�cie�ka do folderu glownego
	public final static String separator=":::";
	
	public void createTxtFile(String fileName);
	public void deleteTxtFile(String fileName);	
	public void writeTxtFile(String fileName, String input);
	public void clearTxtFile(String fileName);
	public List<String> readTxtFile(String fileName);
	public void deleteRecordFromTxtFile(String fileName, int index);
	public void renameTxtFile(String fileName, String newName);
	public boolean txtFileExists(String filename);
}
