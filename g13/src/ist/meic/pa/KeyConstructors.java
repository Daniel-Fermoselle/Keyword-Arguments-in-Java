package ist.meic.pa;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javassist.*;

public class KeyConstructors {

	//TODO REMOVE THROWS
	public static void main(String[] args) throws NotFoundException, 
	CannotCompileException, InstantiationException, IllegalAccessException, ClassNotFoundException,
	NoSuchMethodException, SecurityException, NoSuchFieldException {
		Class<?> c = Class.forName(args[0]);
		System.out.println("Hello World");
		//Constructor<?> m = c.getDeclaredConstructor(Object[].class);
		Test t = new Test("height",123,"margin",12,"margin",20);
		System.out.println(t.toString());
		/*if(m.isAnnotationPresent(KeywordArgs.class)){
			ArrayList<String> fieldNames = new ArrayList<String>();
			Field[] fields = c.getDeclaredFields();
			for(Field field : fields){
				fieldNames.add(field.getName());
			}
			KeywordArgs ka = m.getAnnotation(KeywordArgs.class);
			String value = ka.value();
			String[] comaSplit = value.split(",");
			for(String temp : comaSplit){
				String[] equalSplit = temp.split("=");
				if(fieldNames.contains((String)equalSplit[0])){
					/*Field setField = c.getDeclaredField(equalSplit[0]);
					setField.getType();
					System.out.println("equalSplit[0]: " + equalSplit[0] + " equalSplit[1]: " + equalSplit[1]);
				}
				
			}
			System.out.println(value);*/
		
		//}
//		else{
//			System.out.println("NOOB");
//		}
	} 
}