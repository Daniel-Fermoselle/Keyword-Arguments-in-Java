package ist.meic.pa;

import java.util.ArrayList;

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
			throw new RuntimeException("sdjfbasbf");
		}
	}

	public static void keywordInjector(CtClass ctClass) throws ClassNotFoundException, CannotCompileException{
		final String template ="System.out.println(\"HelloWorld!\");";
		System.out.println("asbjfasnfl ml mçk: " + ctClass.getName());
		CtField[] fields = ctClass.getFields();
		ArrayList<String>fieldNames = new ArrayList<String>();
		//HashMap<String, >
		for (CtField field : fields) {
			fieldNames.add(field.getName());
		}
		for (CtConstructor ctMethod : ctClass.getConstructors()) {
		    Object[] annotations = ctMethod.getAnnotations();
		    Object annotation = getCorrectAnnotation(annotations);
		    if (annotation!=null) {
		    	KeywordArgs ka = (KeywordArgs) annotation;
		    	String keyword = ka.value();
		    	/*String[] comaSplit = keyword.split(",");
				for (String temp : comaSplit) {
						String[] equalSplit = temp.split("=");
						if (fieldNames.contains((String) equalSplit[0])) {
							if(equalSplit.length>1){
								Field setField = c.getField(equalSplit[0]);
								setField.setAccessible(true);
								setField.set(this, new Integer(equalSplit[1]));//TODO HOW TO GENERATE THAT MUCH POWER
		
								System.out.println("equalSplit[0]: " + equalSplit[0] + " equalSplit[1]: " + equalSplit[1]);
							}
						}
						else{
							throw new RuntimeException("Unrecognize keyword: " + equalSplit[0]);
						}

				}*/
		    	ctMethod.setBody("System.out.println(\"" + keyword + "\");");
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
