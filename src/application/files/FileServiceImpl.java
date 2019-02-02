package application.files;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class FileServiceImpl implements FileService {
	@Override
	public void clearTxtFile(String fileName) throws IOException{
		Path path = Paths.get(pathToMainFolder+"/"+fileName+".txt");
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"),StandardOpenOption.TRUNCATE_EXISTING)) {
			writer.write("");
		}
	}

	@Override
	public void deleteTxtFile(String fileName) throws IOException {
		Path path=Paths.get(pathToMainFolder+ "/"+ fileName+".txt");
		Files.delete(path);
	}

	@Override
	public void createTxtFile(String fileName) throws IOException {
		Files.createFile(Paths.get(pathToMainFolder+ "/"+ fileName+ ".txt"));
	}

	@Override
	public void writeTxtFile(String fileName, String input) throws IOException {
		Path path = Paths.get(pathToMainFolder+"/"+fileName+".txt");
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"),StandardOpenOption.APPEND,StandardOpenOption.WRITE)) {
			writer.write(input);
		}
	}

	@Override
	public List<String> readTxtFile(String fileName) throws IOException{
		Path path=Paths.get(pathToMainFolder+"/"+fileName+".txt");
		return Files.readAllLines(path);
	}
	
	@Override
	public void deleteRecordFromTxtFile(String fileName, int index) throws IOException {
		List<String> content=readTxtFile(fileName);
		content.remove(index);
		clearTxtFile(fileName);
		writeTxtFile(fileName, content.stream().collect(Collectors.joining("\n")));
	}
	
	@Override
	public void renameTxtFile(String fileName, String newName) throws IOException {
		Path path=Paths.get(FileService.pathToMainFolder+"/"+fileName+".txt");
		path.toFile().renameTo(Paths.get(FileService.pathToMainFolder+"/"+newName+".txt").toFile());
	}
	
	@Override
	public boolean txtFileExists(String filename) {
		Path path=Paths.get(pathToMainFolder+"/"+filename+".txt");
		return path.toFile().exists();
	}
}
