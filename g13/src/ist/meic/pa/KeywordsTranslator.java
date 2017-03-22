package ist.meic.pa;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

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
    public static void keywordInjector(CtClass ctClass) throws ClassNotFoundException, CannotCompileException {

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
                String[] comaSplit = keyword.split(",");
                for (String temp : comaSplit) {
                    String[] equalSplit = temp.split("=");
                    if (fieldVerifier.keySet().contains((String) equalSplit[0])) {
                        keywordFields.add(equalSplit[0]);
                        if (equalSplit.length > 1) {
                            fieldVerifier.put(equalSplit[0], equalSplit[1]);
                            //System.out.println("equalSplit[0]: " + equalSplit[0] + " equalSplit[1]: " + equalSplit[1]);
                        }
                    } else {
                        throw new RuntimeException("Unrecognize keyword: " + equalSplit[0]);//TODO INJECT EXCEPTION
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
                        "System.out.println(\"After declarations: \");" +
                        "try {" +
                        "   java.util.ArrayList keywords = new java.util.ArrayList();"+
                        "   java.util.List arguments = java.util.Arrays.asList($1);"+
              //          "   java.util.ArrayList keywordFields = "+ keywordFields +";"+
                		"	boolean isRepeated = false;";

                for (String field : keywordFields) {
                    System.out.println(field);
                    template = template +
                    		"	System.out.println(arguments.toString());" +
                            "	for (int i = 0; i < arguments.size(); i = i + 2) {" +
                            "		if (\"" + field + "\".equals(arguments.get(i))) {" +
                            "			if (!keywords.contains(\"" + field + "\")) {" +
                            "				keywords.add(\"" + field + "\");" +
                            "				" + field + " = ((Integer)arguments.get(i + 1)).intValue();" +
                            "			}"+
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
                //System.out.println(template);
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

}
