package pl.dabrowski.electrotools.initService;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class InitFromFileEngine {
	private static final String FIELD_UNSUPPORTED_TYPE_INFO = "Field is of type {0}. Only fields with List.class are initiated by this engine!";
	private final Object object;

	public void initFields() {
		for(Field field:object.getClass().getDeclaredFields()) {
			if(field.isAnnotationPresent(InitFromProperties.class)) {
				if(field.getType().equals(List.class)) {
					log(field.getGenericType().toString());
					
//					InitFromProperties annotation=field.getAnnotation(InitFromProperties.class);
//					String fileName=annotation.fileName();
//					if(fileName.equals(""))
//						fileName=field.getName(); //by default
//					for(String line: getFileLines(fileName)) {
//						String[] lineElements=line.split(",");
////						List list=(List)field.get(object);
//					}
					
				}else {
					log(MessageFormat.format(FIELD_UNSUPPORTED_TYPE_INFO, field.getType()));
				}
				
				//				if(field.getType().)

				//				InitFromProperties annotation=field.getAnnotation(InitFromProperties.class);

				//				String fileName=annotation.fileName();
				//				object.getClass().getResource(name)

				//				Files.readAllLines(Paths.get())
			}
		}
	}
	
	@SneakyThrows
	private List<String> getFileLines(String fileName){
		return Files.readAllLines(Paths.get(object.getClass().getResource(fileName+".txt").toURI()));
	}

	private void log(String value) {
		System.out.println("[InitFromFileEngine]: " + value);
	}
}
