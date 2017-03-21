package ist.meic.pa;

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

public class KeywordsTranslator implements Translator{

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
	public static void keywordInjector(CtClass ctClass) throws ClassNotFoundException, CannotCompileException{
		//final String template ="System.out.println(\"HelloWorld!\");";
		//System.out.println("asbjfasnfl ml mçk: " + ctClass.getName());
		CtField[] fields = ctClass.getFields();
		HashMap<String,String>fieldVerifier= new HashMap<String,String>();
		for (CtField field : fields) {
			fieldVerifier.put(field.getName(), "");
		}
		for (CtConstructor ctMethod : ctClass.getConstructors()) {
		    Object[] annotations = ctMethod.getAnnotations();
		    Object annotation = getCorrectAnnotation(annotations);
		    if (annotation!=null) {
		    	KeywordArgs ka = (KeywordArgs) annotation;
		    	String keyword = ka.value();
		    	String[] comaSplit = keyword.split(",");
				for (String temp : comaSplit) {
						String[] equalSplit = temp.split("=");
						if (fieldVerifier.keySet().contains((String) equalSplit[0])) {
							if(equalSplit.length>1){
								fieldVerifier.put(equalSplit[0], equalSplit[1]);
								//System.out.println("equalSplit[0]: " + equalSplit[0] + " equalSplit[1]: " + equalSplit[1]);
							}
						}
						else{
							throw new RuntimeException("Unrecognize keyword: " + equalSplit[0]);//TODO INJECT EXCEPTION
						}

				}
				//TO INJECT
				String template = "";
				for(String s: fieldVerifier.keySet()){//TODO NEED TO INITIALIZE NON PRIMITIVE TYPES
					if(!fieldVerifier.get(s).equals("")){
						template = template + "this." + s + " = " + fieldVerifier.get(s) + ";\n";
					}
				}
				template = template + "Constructor<?> m;\n"+
				"try {\n"+
				"m = c.getConstructor(Object[].class);\n"+
				"ArrayList<String> fieldNames = new ArrayList<String>();\n"+
				"Field[] fields = c.getFields();\n"+
				"for (Field field : fields) {\n"+
				"fieldNames.add(field.getName());\n"+
				"}\n"+
				"HashMap<String, String> keywords = new HashMap<String, String>();\n"+
				"for(int i=0; i<$args.length;i=i+2){\n"+
					"String arg = $args[i].toString();\n"+
					"if(fieldNames.contains(arg)){\n"+
							"if(keywords.get(arg)==null ||keywords.get(arg)==false){\n"+
								"keywords.put(arg, true);\n"+
								"this.$args[i]=$args[i+1];\n"+
							"}\n"+
							"else{\n"+
								"throw new RuntimeException(\"Duplicated keyword: \" + arg);\n"+
							"}\n"+
					"}\n"+
					"else{\n"+
						"throw new RuntimeException(\"Unrecognize keyword: \" + arg);\n"+
					"}\n"+
				"}\n"+
				"catch (NoSuchMethodException | SecurityException "
				+ "| NoSuchFieldException | IllegalArgumentException"
				+ "| IllegalAccessException e) {\n" + 
					"e.printStackTrace();\n" + 
				"}\n";
				template = "{\n" 
				+ template 
				+ "}";
		    	ctMethod.setBody(template);
		    }
		}
	}
	
	public static Object getCorrectAnnotation(Object[] args){
		for(int i = 0; i<args.length; i++){
			if(args[i] instanceof KeywordArgs){
				return args[i];
			}
		}
		return null;
	}
	
}
