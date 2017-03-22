package ist.meic.pa;

import java.util.ArrayList;
import java.util.HashMap;

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

        CtField[] fields = ctClass.getFields();
        HashMap<String, String> fieldVerifier = new HashMap<String, String>();
        ArrayList<String> keywordFields = new ArrayList<String>();
        for (CtField field : fields) {
            fieldVerifier.put(field.getName(), "");
        }
        for (CtConstructor ctMethod : ctClass.getConstructors()) {
            Object[] annotations = ctMethod.getAnnotations();
            Object annotation = getCorrectAnnotation(annotations);
            if (annotation != null) {
                KeywordArgs ka = (KeywordArgs) annotation;
                String keyword = ka.value();
                if(keyword.equals("")){//EMPTY KEYWORD CASE
                	String template="{}";
                	ctMethod.setBody(template);
                	return;
                }
                String[] comaSplit = keyword.split(",");
                for (String temp : comaSplit) {
                    String[] equalSplit = temp.split("=");
                    if (fieldVerifier.keySet().contains((String) equalSplit[0])) {
                        if (!keywordFields.contains(equalSplit[0])) {
                            keywordFields.add(equalSplit[0]);
                        } else {
                            throw new RuntimeException("Duplicated Keyword in annotation: " + equalSplit[0]);
                        }
                        if (equalSplit.length > 1) {
                            fieldVerifier.put(equalSplit[0], equalSplit[1]);
                            //System.out.println("equalSplit[0]: " + equalSplit[0] + " equalSplit[1]: " + equalSplit[1]);
                        }
                    } else {
                        throw new RuntimeException("Unrecognize keyword: " + equalSplit[0]);
                    }

                }
                //TO INJECT
                String template = "";
                for (String s : keywordFields) {//TODO NEED TO INITIALIZE NON PRIMITIVE TYPES
                    if (!fieldVerifier.get(s).equals("")) {
                        template = template + "this." + s + " = " + fieldVerifier.get(s) + ";";
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
                    CtField fieldType = ctClass.getDeclaredField(field);
                    String stringFieldType = fieldType.getType().getName();
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

}
