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
			
			HashMap<Class<?>, Function<String, Object>> typeParserMap = new HashMap<>();
			typeParserMap.put(double.class, Double::parseDouble);
			typeParserMap.put(Double.class, Integer::parseInt);
			typeParserMap.put(int.class, Integer::parseInt);
			typeParserMap.put(Integer.class, Integer::parseInt);
			typeParserMap.put(String.class, x -> x);
			
			for (String line : lines)
			{
				String[] lineArgs = line.split(",");
				Constructor<T> constructor = type.getConstructor(new Class<?>[0]);
				T object = constructor.newInstance(new Object[0]);
				
				for (Field field : fields)
				{
					String fieldName = field.getName().toLowerCase();
					int argumentIndex = nameIndexPairs.get(fieldName);
					String lineValueAsText = lineArgs[argumentIndex];
					
					Class<?> fieldType = field.getType();
					Object fieldValue = typeParserMap.get(fieldType).apply(lineValueAsText);
					
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
}
