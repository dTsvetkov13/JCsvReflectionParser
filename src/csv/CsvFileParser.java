package csv;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.lang.reflect.*;

public class CsvFileParser
{
	private final HashMap<Class<?>, Function<String, Object>> typeParserMap;
	private final HashMap<String, Function<String, Object>> fieldNameParserMap;
	
	public CsvFileParser()
	{
		typeParserMap = new HashMap<>();
		fieldNameParserMap = new HashMap<>();
		initializeTypeParserMap();
	}
	
	public void addFieldNameParser(String fieldName, Function<String, Object> parser)
	{
		if (fieldName == null || fieldName.length() == 0 || parser == null)
		{
			throw new IllegalArgumentException("Parameters cannot be null");
		}
		
		fieldNameParserMap.put(fieldName, parser);
	}
	
	public <T> List<T> parse(Class<T> type, String path)
	{
		List<T> list = new ArrayList<T>();
		
		try
		{
			List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
			
			String[] columns = lines.remove(0).split(",");
			HashMap<String, Integer> nameIndexPairs = new HashMap<>();
			
			for (int index = 0; index < columns.length; index++)
			{
				nameIndexPairs.put(columns[index].replaceAll("_", "").toLowerCase(), index);
			}
			
			Field[] fields = type.getDeclaredFields();
			
			for (String line : lines)
			{
				String[] lineArgs = line.split(",");
				Constructor<T> constructor = type.getConstructor(new Class<?>[0]);
				T object = constructor.newInstance(new Object[0]);
				
				for (Field field : fields)
				{	
					String fieldName = field.getName().toLowerCase();
					CsvName csvName = field.getAnnotation(CsvName.class);
					
					if (csvName != null)
					{
						fieldName = csvName.value();
					}
					
					int argumentIndex = nameIndexPairs.get(fieldName);
					String lineValueAsText = lineArgs[argumentIndex];
					
					Class<?> fieldType = field.getType();
					Function<String, Object> parser = typeParserMap.get(fieldType);
					
					if (fieldNameParserMap.containsKey(fieldName))
					{
						parser = fieldNameParserMap.get(fieldName);
					}
					
					Object fieldValue = parser.apply(lineValueAsText);
					
					field.set(object, fieldValue);
				}
				
				list.add(object);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	private void initializeTypeParserMap()
	{
		typeParserMap.put(byte.class, Byte::parseByte);
		typeParserMap.put(Byte.class, Byte::parseByte);
		
		typeParserMap.put(short.class, Short::parseShort);
		typeParserMap.put(Short.class, Short::parseShort);
		
		typeParserMap.put(int.class, Integer::parseInt);
		typeParserMap.put(Integer.class, Integer::parseInt);
		
		typeParserMap.put(long.class, Long::parseLong);
		typeParserMap.put(Long.class, Long::parseLong);
		
		typeParserMap.put(double.class, Double::parseDouble);
		typeParserMap.put(Double.class, Integer::parseInt);
		
		typeParserMap.put(float.class, Float::parseFloat);
		typeParserMap.put(Float.class, Float::parseFloat);
		
		typeParserMap.put(boolean.class, Boolean::parseBoolean);
		typeParserMap.put(Boolean.class, Boolean::parseBoolean);
		
		typeParserMap.put(char.class, x -> x.charAt(0));
		typeParserMap.put(Character.class, x -> x.charAt(0));
		
		typeParserMap.put(String.class, x -> x);
	}
}
