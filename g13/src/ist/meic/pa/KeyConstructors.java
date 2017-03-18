package ist.meic.pa;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javassist.*;

public class KeyConstructors {

	//TODO REMOVE THROWS
	public static void main(String[] args) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, SecurityException {
		System.out.println("Hello World");
		Class<?> c = Class.forName(args[0]);
		Constructor<?> m = c.getConstructor();
		if(m.isAnnotationPresent(KeywordArgs.class)){
			KeywordArgs ka = m.getAnnotation(KeywordArgs.class);
			String value = ka.value();
			System.out.println(value);
		}
		else{
			System.out.println("NOOB");
		}
	} 
}