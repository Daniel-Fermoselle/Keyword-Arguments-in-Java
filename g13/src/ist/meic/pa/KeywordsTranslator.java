package ist.meic.pa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.Translator;

public class KeywordsTranslator implements Translator {

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
    }

    @Override
    public void onLoad(ClassPool pool, String className) throws NotFoundException, CannotCompileException {
        CtClass ctClass = pool.get(className);
        try {
            keywordInjector(ctClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading class: " + className);
        }
    }

    //TODO REMOVE THROWS
    public static void keywordInjector(CtClass ctClass) throws ClassNotFoundException, CannotCompileException, NotFoundException {
        CtField[] fields = getAllFieldsInHierarchy(ctClass);
        ArrayList<String> fieldVerifier = new ArrayList<String>();
        ArrayList<String> keywordFields = new ArrayList<String>();
        HashMap<String,String> keywordAssignments;
        for (CtField field : fields) {
            fieldVerifier.add(field.getName());
        }
        for (CtConstructor ctMethod : ctClass.getDeclaredConstructors()) {//TODO REVIEW BECAUSE OF getAllKeywordArgs
            Object[] annotations = ctMethod.getAnnotations();
            Object annotation = getCorrectAnnotation(annotations);
            if (annotation != null) {
                KeywordArgs ka = (KeywordArgs) annotation;
                String keyword = getAllKeywordArgs(ctClass);
                //System.out.println("snflksaf: " + keyword);
                keywordAssignments = getMap(keyword);
                if(keyword.equals("")){//EMPTY KEYWORD CASE
                	String template="{}";
                	ctMethod.setBody(template);
                	return;
                }
                for (String tempKeywordIc : keywordAssignments.keySet()) {
                	//System.out.println("keyword: " + tempKeywordIc + " : " + keywordAssignments.get(tempKeywordIc));
                    if (fieldVerifier.contains(tempKeywordIc)) {
                        keywordFields.add(tempKeywordIc);
                    } else {
                        throw new RuntimeException("Unrecognize keyword: " + tempKeywordIc);
                    }

                }
                //TO INJECT
                String template = "";
                for (String s : keywordAssignments.keySet()) {//TODO NEED TO INITIALIZE NON PRIMITIVE TYPES
                    if (!keywordAssignments.get(s).equals("")) {
                        template = template + "this." + s + " = " + keywordAssignments.get(s) + ";";
                    }
                }
                template = template +
                        "try {" +
                        "   java.util.ArrayList readKeywords = new java.util.ArrayList();" +
                        "   java.util.List arguments = java.util.Arrays.asList($1);" +
                        "	boolean inKeyword = false;" +
                        "	for (int i = 0; i < arguments.size(); i = i + 2) {" +
                        "       inKeyword = false;" +
                        "       if(!readKeywords.contains(arguments.get(i))){" +
                        "           readKeywords.add(arguments.get(i));" +
                        "       }" +
                        "       else {" +
                        "           throw new RuntimeException(\"Duplicated Keyword in constructor args: \" + arguments.get(i));" +
                        "       }";

                for (String field : keywordFields) {
                    template = template +
                            "       if(\"" + field + "\".equals(arguments.get(i))){" +
                            "			inKeyword = true;" +
                            "       }";
                }
                template = template +
                        "       if(!inKeyword){" +
                        "           throw new RuntimeException(\"Unrecognized Keyword: \" + arguments.get(i));" +
                        "       }" +
                        "   }";


                for (String field : keywordFields) {
                    //System.out.println(fie ld);
                    CtField fieldType = getSpecificFields(fields,field);
                    String stringFieldType = fieldType.getType().getName();
                    //System.out.println("Field name: " + fieldType.getName() + " type name: " + stringFieldType);
                    template = template +
                            "	for (int i = 0; i < arguments.size(); i = i + 2) {" +
                            "		if (\"" + field + "\".equals(arguments.get(i))) {" + 
                    		getCorrectAssignment(field, stringFieldType) + 
                                "		}" +
                            "	}";
                }
                template = template +
                        "} catch (java.lang.Exception e) {" +
                        "	e.printStackTrace();" +
                        "	throw new RuntimeException(\"Unexpected error\");" +
                        "}";

                template = "{"
                        + template
                        + "}";
                ctMethod.setBody(template);
            }
        }
    }

    public static Object getCorrectAnnotation(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof KeywordArgs) {
                return args[i];
            }
        }
        return null;
    }
    
    public static String getCorrectAssignment(String field, String fieldString){
    	String r = "";
    	if(fieldString.equals("int")){
    		r= field + "=((Number)arguments.get(i + 1)).intValue();";
    	}
    	else if(fieldString.equals("byte")){
    		r= field + "=((Number)arguments.get(i + 1)).byteValue();";
    	}
    	else if(fieldString.equals("short")){
    		r= field + "=((Number)arguments.get(i + 1)).shortValue();";
    	}
    	else if(fieldString.equals("long")){
    		r= field + "=((Number)arguments.get(i + 1)).longValue();";
    	}
    	else if(fieldString.equals("float")){
    		r= field + "=((Number)arguments.get(i + 1)).floatValue();";
    	}
    	else if(fieldString.equals("double")){
    		r= field + "=((Number)arguments.get(i + 1)).doubleValue();";
    	}
    	else if(fieldString.equals("boolean")){
    		r= field + "=((Boolean)arguments.get(i + 1)).booleanValue();";
    	}
    	else if(fieldString.equals("char")){
    		r= field + "=((Character)arguments.get(i + 1)).charValue();";
    	}
    	else{
    		r= field + "= (" + fieldString + ")arguments.get(i + 1);";
    	}
    	
    	
    	return r;
    }
    
    public static CtField[] getAllFieldsInHierarchy(CtClass objectClass) {//TODO MAYBE DOESNT NEED TO BE INJECTED
    	try {
        Set<CtField> allFields = new HashSet<>();
        CtField[] declaredFields = objectClass.getDeclaredFields();
        //CtField[] Fields = objectClass.getFields();
		if (objectClass.getSuperclass() != null) {
		    CtClass superClass = objectClass.getSuperclass();
		    CtField[] superClassMethods = getAllFieldsInHierarchy(superClass);
		    allFields.addAll(Arrays.asList(superClassMethods));
		}
        allFields.addAll(Arrays.asList(declaredFields));
        //allFields.addAll(Arrays.asList(Fields));
        CtField[] temp = allFields.toArray(new CtField[allFields.size()]);
        return temp;
    	} catch (NotFoundException e) {
    		e.printStackTrace();
    		throw new RuntimeException(e.getMessage());
    	}
    }
	
	public static String getAllKeywordArgs(CtClass objectClass) {// TODO MAYBE DOESNT NEED TO BE INJECTED
		try {
			String keyword = "";
			for (CtConstructor ctMethod : objectClass.getDeclaredConstructors()) {
				Object[] annotations = ctMethod.getAnnotations();
				Object annotation = getCorrectAnnotation(annotations);
				if (annotation != null) {
					KeywordArgs ka = (KeywordArgs) annotation;
					keyword = ka.value();
					if (objectClass.getSuperclass() != null) {
						CtClass superClass = objectClass.getSuperclass();
						keyword += "," + getAllKeywordArgs(superClass);
					}
				}
			}
			return keyword;
		} catch (SecurityException | NotFoundException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	public static CtField getSpecificFields(CtField[] cf, String n) {//TODO MAYBE DOESNT NEED TO BE INJECTED
    	for(CtField f : cf){
    		if(f.getName().equals(n)){
    			return f;
    		}
    	}
    	throw new RuntimeException("No such field named: " + n);
    }
	//a=10,b
	public static HashMap<String,String> getMap(String keyword){
		HashMap<String,String> map = new HashMap<String,String>();
		String[] coma = keyword.split(",");
		for(String s : coma){
			String[] equal = s.split("=");
			if(!map.containsKey(equal[0]) || (map.get(equal[0]).equals("") && equal.length>1)){
				if(equal.length>1){
					map.put(equal[0], equal[1]);
				}
				else{
					map.put(equal[0], "");
				}
			}
		}
		return map;
		
		
	}
	
}
