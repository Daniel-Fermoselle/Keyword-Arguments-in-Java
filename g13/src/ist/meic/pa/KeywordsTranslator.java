package ist.meic.pa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import javassist.Translator;

public class KeywordsTranslator implements Translator {

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
    }

    @Override
    public void onLoad(ClassPool pool, String className) {
    	try {
    		CtClass ctClass = pool.get(className);
            keywordInjector(ctClass);
        } catch (ClassNotFoundException | IOException | NotFoundException | CannotCompileException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading class: " + className);
        }
    }

    public static void keywordInjector(CtClass ctClass) throws ClassNotFoundException, CannotCompileException, NotFoundException, IOException {

    	//Create default constructor for this class
    	addDefaultConstructor(ctClass);

    	//Initialize variables
    	TreeMap<String, String> keywordAssignments;
    	CtField[] fields = getAllFieldsInHierarchy(ctClass);
        ArrayList<String> fieldVerifier = new ArrayList<String>();
        ArrayList<String> keywordFields = new ArrayList<String>();

        //Add fields names to ArrayList
        for (CtField field : fields) {
            fieldVerifier.add(field.getName());
        }

        for (CtConstructor ctMethod : ctClass.getDeclaredConstructors()) {
            Object[] annotations = ctMethod.getAnnotations();
            Object annotation = getCorrectAnnotation(annotations);

            if (annotation != null) {

                //Get all keywords and put them in a map from superclasses
                String keywordsInString = getAllKeywordArgs(ctClass);
                keywordAssignments = getMap(keywordsInString);

                //Case for "   " as keyword
                keywordsInString.replaceAll("\\s+","");
                if (keywordsInString.equals("")) {//EMPTY KEYWORD CASE
                    String template = "{}";
                    ctMethod.setBody(template);
                    return;
                }

                //See if all keywords are fields of the class
                for (String tempKeywordIc : keywordAssignments.keySet()) {
                    if (fieldVerifier.contains(tempKeywordIc)) {
                        keywordFields.add(tempKeywordIc);
                    } else {
                        throw new RuntimeException("Unrecognize keyword: " + tempKeywordIc);
                    }
                }

                //Template to inject
                String template = "";

                //Default values of the Keywords Fields
                for (String s : keywordAssignments.keySet()) {//TODO NEED TO INITIALIZE NON PRIMITIVE TYPES
                    if (!keywordAssignments.get(s).equals("")) {
                        template = template + "this." + s + " = " + keywordAssignments.get(s) + ";";
                    }
                }

                //For loop to inject verification if the arguments are a key word and if they are not Duplicated
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
                        "           throw new RuntimeException(\"Unrecognized keyword: \" + arguments.get(i));" +
                        "       }" +
                        "   }";
                //For loop to assign values in arguments to the keywords
                for (String field : keywordFields) {
                    CtField fieldType = getSpecificFields(fields, field);
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
                        "	throw new RuntimeException(e.getMessage());" +
                        "}";

                template = "{"
                        + template
                        + "}";
                ctMethod.setBody(template);
                break;
            }
        }
    }
    
    public static void addDefaultConstructor(CtClass ctClass) throws CannotCompileException{
    	boolean b = false;
    	for(CtConstructor cons : ctClass.getDeclaredConstructors()) {
            if (cons.getSignature().equals("()V"))
                b=true;
        }
        if(!b) {
            ctClass.addConstructor(CtNewConstructor.defaultConstructor(ctClass));
        }
    }

    public static Object getCorrectAnnotation(Object[] args) {
        for (Object annotation : args) {
            if (annotation instanceof KeywordArgs) {
                return annotation;
            }
        }
        return null;
    }

    public static CtField[] getAllFieldsInHierarchy(CtClass objectClass) {//TODO MAYBE DOESNT NEED TO BE INJECTED
        try {
            Set<CtField> allFields = new HashSet<>();
            CtField[] declaredFields = objectClass.getDeclaredFields();

            //Recursion
            if (objectClass.getSuperclass() != null) {
                CtClass superClass = objectClass.getSuperclass();
                CtField[] superClassMethods = getAllFieldsInHierarchy(superClass);
                allFields.addAll(Arrays.asList(superClassMethods));
            }
            allFields.addAll(Arrays.asList(declaredFields));

            return allFields.toArray(new CtField[allFields.size()]);

        } catch (NotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public static CtField getSpecificFields(CtField[] cf, String n) {
        for (CtField f : cf) {
            if (f.getName().equals(n)) {
                return f;
            }
        }
        throw new RuntimeException("No such field named: " + n);
    }

    public static String getAllKeywordArgs(CtClass objectClass) {
        try {
            String keyword = "";

            for (CtConstructor ctMethod : objectClass.getDeclaredConstructors()) {
                Object[] annotations = ctMethod.getAnnotations();
                Object annotation = getCorrectAnnotation(annotations);

                if (annotation != null) {

                    KeywordArgs ka = (KeywordArgs) annotation;
                    keyword = ka.value();

                    //Recursion
                    if (objectClass.getSuperclass() != null) {
                        CtClass superClass = objectClass.getSuperclass();
                        String allKeyWords = getAllKeywordArgs(superClass);
                        if(!allKeyWords.equals("")) {
                            keyword += "," + getAllKeywordArgs(superClass);
                        }
                    }
                }
            }
            return keyword;

        } catch (SecurityException | NotFoundException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public static String[] splitComma(String keyword){
        String otherThan = " [^)}\"({] ";
        String ignoredString = String.format(" [(|\"|{] %s* [)|\"|}]", otherThan);
        String regex = String.format("(?x) "+ // enable comments, ignore white spaces
                                     ",                         "+ // match a comma
                                     "(?=                       "+ // start positive look ahead
                                     "  (?:                     "+ //   start non-capturing group 1
                                     "    %s*                   "+ //     match 'otherThanQuote' zero or more times
                                     "    %s                    "+ //     match 'ignoredString'
                                     "  )*                      "+ //   end group 1 and repeat it zero or more times
                                     "  %s*                     "+ //   match 'otherThanQuote'
                                     "  $                       "+ // match the end of the string
                                     ")                         ", // stop positive look ahead
                                     otherThan, ignoredString, otherThan);
        return keyword.split(regex);
    }
    
    public static String[] splitEqual(String keyword){
        String otherThanQuote = " [^\"] ";
        String quotedString = String.format(" \" %s* \" ", otherThanQuote);
        String regex = String.format("(?x) "+ // enable comments, ignore white spaces
                                      "=                         "+ // match a comma
                                      "(?=                       "+ // start positive look ahead
                                      "  (?:                     "+ //   start non-capturing group 1
                                      "    %s*                   "+ //     match 'otherThanQuote' zero or more times
                                      "    %s                    "+ //     match 'quotedString'
                                      "  )*                      "+ //   end group 1 and repeat it zero or more times
                                      "  %s*                     "+ //   match 'otherThanQuote'
                                      "  $                       "+ // match the end of the string
                                      ")                         ", // stop positive look ahead
                                      otherThanQuote, quotedString, otherThanQuote);
        return keyword.split(regex);
    }    

    public static TreeMap<String, String> getMap(String keyword) {
        TreeMap<String, String> map = new TreeMap<String, String>();

        String[] coma = splitComma(keyword);

        for (String s : coma) {
            String[] equal = splitEqual(s);

            if (!map.containsKey(equal[0]) || (map.get(equal[0]).equals("") && equal.length > 1)) {
                if (equal.length > 1) {
                    map.put(equal[0], equal[1]);
                } else {
                    map.put(equal[0], "");
                }
            }
        }

        return map;
    }
    
    public static String getCorrectAssignment(String field, String fieldString) {
        String r = "";
        if (fieldString.equals("int")) {
            r = "this." + field + "=((Number)arguments.get(i + 1)).intValue();";
        } else if (fieldString.equals("byte")) {
            r = "this." + field + "=((Number)arguments.get(i + 1)).byteValue();";
        } else if (fieldString.equals("short")) {
            r = "this." + field + "=((Number)arguments.get(i + 1)).shortValue();";
        } else if (fieldString.equals("long")) {
            r = "this." + field + "=((Number)arguments.get(i + 1)).longValue();";
        } else if (fieldString.equals("float")) {
            r = "this." + field + "=((Number)arguments.get(i + 1)).floatValue();";
        } else if (fieldString.equals("double")) {
            r = "this." + field + "=((Number)arguments.get(i + 1)).doubleValue();";
        } else if (fieldString.equals("boolean")) {
            r = "this." + field + "=((Boolean)arguments.get(i + 1)).booleanValue();";
        } else if (fieldString.equals("char")) {
            r = "this." + field + "=((Character)arguments.get(i + 1)).charValue();";
        } else {
            r = "this." + field + "= (" + fieldString + ")arguments.get(i + 1);";
        }

        return r;
    }

}
