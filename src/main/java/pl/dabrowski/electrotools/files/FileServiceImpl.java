package pl.dabrowski.electrotools.files;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import lombok.SneakyThrows;

public class FileServiceImpl implements FileService {
	@Override
	@SneakyThrows
	public void clearTxtFile(String fileName) {
		Path path = Paths.get(pathToMainFolder + "/" + fileName + ".txt");
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING)) {
			writer.write("");
		}
	}

	@Override
	@SneakyThrows
	public void deleteTxtFile(String fileName) {
		Path path = Paths.get(pathToMainFolder + "/" + fileName + ".txt");
		Files.delete(path);
	}

	@Override
	@SneakyThrows
	public void createTxtFile(String fileName) {
		Files.createFile(Paths.get(pathToMainFolder + "/" + fileName + ".txt"));
	}

	@Override
	@SneakyThrows
	public void writeTxtFile(String fileName, String input) {
		Path path = Paths.get(pathToMainFolder + "/" + fileName + ".txt");
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"), StandardOpenOption.APPEND, StandardOpenOption.WRITE)) {
			writer.write(input);
		}
	}

	@Override
	@SneakyThrows
	public List<String> readTxtFile(String fileName) {
		Path path = Paths.get(pathToMainFolder + "/" + fileName + ".txt");
		return Files.readAllLines(path);
	}

	@Override
	@SneakyThrows
	public void deleteRecordFromTxtFile(String fileName, int index) {
		List<String> content = readTxtFile(fileName);
		content.remove(index);
		clearTxtFile(fileName);
		writeTxtFile(fileName, content.stream().collect(Collectors.joining("\n")));
	}

	@Override
	@SneakyThrows
	public void renameTxtFile(String fileName, String newName) {
		Path path = Paths.get(FileService.pathToMainFolder + "/" + fileName + ".txt");
		path.toFile().renameTo(Paths.get(FileService.pathToMainFolder + "/" + newName + ".txt").toFile());
	}

	@Override
	public boolean txtFileExists(String filename) {
		Path path = Paths.get(pathToMainFolder + "/" + filename + ".txt");
		return path.toFile().exists();
	}
}
