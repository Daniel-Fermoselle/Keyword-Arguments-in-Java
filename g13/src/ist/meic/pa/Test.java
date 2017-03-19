package ist.meic.pa;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

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
			//"width", 80, "height", 30
			HashMap<String, Boolean> keywords = new HashMap<String, Boolean>();
			for(int i=0; i<args.length;i=i+2){
				String arg = args[i].toString();
				if(fieldNames.contains(arg)){
						if(keywords.get(arg)==null ||keywords.get(arg)==false){
							keywords.put(arg, true);
							Field setField = c.getDeclaredField(arg);
							setField.set(this, new Integer(args[i+1].toString()));//TODO HOW TO GENERATE THAT MUCH POWER
						}
						else{
							throw new RuntimeException("Duplicated keyword: " + arg);
						}
				}
				else{
					throw new RuntimeException("Unrecognize keyword: " + arg);
				}
			}
		} catch (NoSuchMethodException | SecurityException | NoSuchFieldException | IllegalArgumentException
				| IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return String.format("width:%s,height:%s,margin:%s", width, height, margin);
	}

}
