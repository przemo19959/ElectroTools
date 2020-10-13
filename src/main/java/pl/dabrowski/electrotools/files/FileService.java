package pl.dabrowski.electrotools.files;

import java.io.IOException;
import java.util.List;
/**
 * This interface contains methods, which performs basic I/O file operations. MPproject stores data about
 * all his song lists and song in files specified by <strong>pathToMainFolder</strong>, so that after each application run
 * songs doesn't need to be found again, application just gets data from files in that main folder. In each method parameter
 * <strong>fileName</strong> contains name without format, for example: "myFile" instead of "myFile.txt". All methods perform
 * file operation with <strong>pathToMainFolder</strong> as base path.
 * @author hex
 * @version 1.0
 */
public interface FileService {
	public final static String pathToMainFolder="D:/java-workspace/ElectroTools/stock"; //œcie¿ka do folderu glownego
	public final static String separator=":::";
	
	/**
	 * This method creates new empty file
	 * @param fileName name of new file to be created
	 * @throws IOException
	 */
	public void createTxtFile(String fileName) throws IOException;
	
	/**
	 * This method deletes file
	 * @param fileName name of file to be deleted
	 * @throws IOException
	 */
	public void deleteTxtFile(String fileName) throws IOException;
	
	/**
	 * This method writes(input value is appended to already existing content of file) new data to file
	 * @param fileName name of file on which write operation is to be performed
	 * @param input	data to be written
	 * @throws IOException
	 */
	public void writeTxtFile(String fileName, String input) throws IOException;
	
	/**
	 * This method clears file, after this operation file content is equal to empty string ""
	 * @param fileName name of file to be cleared
	 * @throws IOException
	 */
	public void clearTxtFile(String fileName) throws IOException;
	
	/**
	 * This method reads content of file
	 * @param fileName name of file to be read
	 * @return content of files is returned as list, where each item represents one line of file
	 * @throws IOException
	 */
	public List<String> readTxtFile(String fileName) throws IOException;
	
	/**
	 * This method removes one line from file
	 * @param fileName name of file 
	 * @param index index of line to be removed
	 * @throws IOException
	 */
	public void deleteRecordFromTxtFile(String fileName, int index) throws IOException;
	
	/**
	 * This method renames existing file
	 * @param fileName name of file to be renamed
	 * @param newName new name, which will be given to file
	 * @throws IOException
	 */
	public void renameTxtFile(String fileName, String newName) throws IOException;
	
	/**
	 * This methos checks wether given file exists in mainFolder
	 * @param filename - name of file
	 * @return true if file with given exits in mainFolder
	 */
	public boolean txtFileExists(String filename);
}
