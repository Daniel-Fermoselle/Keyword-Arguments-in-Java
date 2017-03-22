package ist.meic.pa;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javassist.*;

public class Test {
	int width;
	int height;
	int margin;

	@KeywordArgs("width=100,height=50,margin")
	public Test(Object... args) {
		
	}

	public String toString() {
		return String.format("width:%s,height:%s,margin:%s", width, height, margin);
	}

	
	public static Field[] getAllFieldsInHierarchy(Class<?> objectClass) {//TODO MAYBE DOESNT NEED TO BE INJECTED
        Set<Field> allFields = new HashSet<>();
        Field[] declaredFields = objectClass.getDeclaredFields();
        Field[] Fields = objectClass.getFields();
        if (objectClass.getSuperclass() != null) {
            Class<?> superClass = objectClass.getSuperclass();
            Field[] superClassMethods = getAllFieldsInHierarchy(superClass);
            allFields.addAll(Arrays.asList(superClassMethods));
        }
        allFields.addAll(Arrays.asList(declaredFields));
        allFields.addAll(Arrays.asList(Fields));
        return allFields.toArray(new Field[allFields.size()]);
    }
	
	public static String getAllKeywordArgs(Class<?> objectClass){//TODO MAYBE DOESNT NEED TO BE INJECTED
		Constructor<?> c;
		try {
			c = objectClass.getConstructor(Object[].class);
			KeywordArgs ka = c.getAnnotation(KeywordArgs.class);
			String keyword = ka.value();
			if (objectClass.getSuperclass() != null) {
				Class<?> superClass = objectClass.getSuperclass();
				keyword += getAllKeywordArgs(superClass);
			}
		return keyword;
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
}
