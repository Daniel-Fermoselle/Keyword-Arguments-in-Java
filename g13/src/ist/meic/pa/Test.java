package ist.meic.pa;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javassist.*;

public class Test {
	int width;
	int height;
	int margin;

	@KeywordArgs("width=100,height=50,margin")
	public Test(Object... args) {
		Constructor<?> m;
		try {
			Class<?> c = this.getClass();
			m = c.getDeclaredConstructor(Object[].class);
			ArrayList<String> fieldNames = new ArrayList<String>();
			Field[] fields = c.getDeclaredFields();
			for (Field field : fields) {
				fieldNames.add(field.getName());
			}
			KeywordArgs ka = m.getAnnotation(KeywordArgs.class);
			String keyword = ka.value();
			String[] comaSplit = keyword.split(",");
			for (String temp : comaSplit) {
					String[] equalSplit = temp.split("=");
					if (fieldNames.contains((String) equalSplit[0])) {
						if(equalSplit.length>1){
							Field setField = c.getDeclaredField(equalSplit[0]);
							setField.set(this, new Integer(equalSplit[1]));//TODO HOW TO GENERATE THAT MUCH POWER
	
							System.out.println("equalSplit[0]: " + equalSplit[0] + " equalSplit[1]: " + equalSplit[1]);
						}
					}
					else{
						throw new RuntimeException("Unrecognize keyword: " + equalSplit[0]);
					}

			}
		} catch (NoSuchMethodException | SecurityException | NoSuchFieldException | IllegalArgumentException
				| IllegalAccessException e) {
			e.printStackTrace();
		}
		System.out.println(this.toString());
	}

	public String toString() {
		return String.format("width:%s,height:%s,margin:%s", width, height, margin);
	}

}
