package pl.dabrowski.electrotools.initService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class InitFromFileEngine {
	private static final String CLASS_WITH_NAME_CANT_BE_FOUND = "Class with name {0} can''t be found!";
	private static final String TEMPLATE = "[{0}]: {1}";
	private static final String FILE_CANT_BE_FOUND_IN_CLASSPATH = "File with name {0} can''t be found in {1} resource classpath!";
	private static final String PROCESSING_TIME_INFO = "Processing fields took: {0}[ms].";
	private static final String PARAMETRIZED_TYPE_DOESN_T_HAVE_CONSTRUCTOR_WITH_STRING_ARGUMENTS = "Parametrized type {0} doesn''t have constructor with String[] arguments!";
	private static final String FIELD_PARAMETRIZED_TYPES_ARRAY_IS_EMPTY = "Field {0} parametrized types array is empty!";
	private static final String FIELD_UNSUPPORTED_TYPE_INFO = "Field is of type {0}. Only fields with {1}.class are initiated by this engine!";

	private final Class<?> supportedFieldCls = List.class;
	private final Class<?> defaultImpl = ArrayList.class;

	private final Object object;
	private long time;

	public void initFields() {
		time = System.currentTimeMillis();
		for(Field field:object.getClass().getDeclaredFields()) {
			if(field.isAnnotationPresent(InitFromFile.class)) {
				if(field.getType().equals(supportedFieldCls)) {
					Object list = getActualField(field);
					Type parametrizedType = getParametrizedType(field);
					if(parametrizedType != null) {
						Constructor<?> cons = getParametrizedTypeConstructor(parametrizedType);
						if(cons != null) {
							InitFromFile annotation = field.getAnnotation(InitFromFile.class);
							String fileName = getFileName(annotation, field);
							String[] prefixes = annotation.prefixes();

							for(String line:getFileLines(fileName)) {
								String[] lineElements = applyPrefixes(line.split(","), prefixes); //default pattern param1,param2,...
								Object obj = getNewInstanceOfParametrizedType(cons, lineElements);
								if(obj != null)
									add(list, obj);
							}
						}
					}
				} else {
					log(MessageFormat.format(FIELD_UNSUPPORTED_TYPE_INFO, field.getType(), supportedFieldCls.getSimpleName()));
				}
			}
		}
		log(MessageFormat.format(PROCESSING_TIME_INFO, (System.currentTimeMillis() - time)));
	}

	private String[] applyPrefixes(String[] values, String[] prefixes) {
		for(int i = 0;i < prefixes.length;i++) {
			values[i] = prefixes[i] + values[i];
		}
		return values;
	}

	private String getFileName(InitFromFile annotation, Field field) {
		String fileName = annotation.fileName();
		if(fileName.equals(""))
			fileName = field.getName(); //by default
		return fileName;
	}

	@SneakyThrows
	private void add(Object list, Object obj) {
		list.getClass().getDeclaredMethod("add", Object.class).invoke(list, obj);
	}

	private Object getNewInstanceOfParametrizedType(Constructor<?> cons, String[] lineElements) {
		try {
			return cons.newInstance(new Object[]{lineElements});
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SneakyThrows
	private Object getActualField(Field field) {
		try {
			field.setAccessible(true);
			Object list = field.get(object);
			if(list == null) {
				Object newInstance = defaultImpl.newInstance(); //default impl
				field.set(object, newInstance); //important, otherwise code is not changed at runtime
				return newInstance;
			}
			return list;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private Constructor<Type> getParametrizedTypeConstructor(Type parametrizedType) {
		try {
			Class<?> parametrizedTypeCls = getClassFromString(parametrizedType.getTypeName());
			if(parametrizedTypeCls != null)
				return (Constructor<Type>) parametrizedTypeCls.getConstructor(String[].class);
			return null;
		} catch (NoSuchMethodException e) {
			log(MessageFormat.format(PARAMETRIZED_TYPE_DOESN_T_HAVE_CONSTRUCTOR_WITH_STRING_ARGUMENTS, parametrizedType.getTypeName()));
			return null;
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Class<?> getClassFromString(String canonicalName) {
		try {
			return Class.forName(canonicalName);
		} catch (ClassNotFoundException e) {
			log(MessageFormat.format(CLASS_WITH_NAME_CANT_BE_FOUND, canonicalName));
			return null;
		}
	}

	private Type getParametrizedType(Field field) {
		Type[] parameterizedTypes = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
		if(parameterizedTypes.length == 0) {
			log(MessageFormat.format(FIELD_PARAMETRIZED_TYPES_ARRAY_IS_EMPTY, field.getName()));
			return null;
		}
		return parameterizedTypes[0]; //return first, others are ignored
	}

	@SneakyThrows
	private List<String> getFileLines(String fileName) {
		URL url = object.getClass().getResource(fileName + ".txt");
		if(url != null)
			return Files.readAllLines(Paths.get(url.toURI()));
		log(MessageFormat.format(FILE_CANT_BE_FOUND_IN_CLASSPATH, fileName, object.getClass().getCanonicalName()));
		return Collections.emptyList();
	}

	private void log(String value) {
		System.out.println(MessageFormat.format(TEMPLATE, InitFromFile.class.getSimpleName(), value));
	}
}
