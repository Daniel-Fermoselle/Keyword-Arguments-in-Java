package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
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
		for (CtConstructor ctMethod : ctClass.getConstructors()) {
		    Object[] annotations = ctMethod.getAnnotations();
		    if ((annotations.length == 1) &&
		        (annotations[0] instanceof KeywordArgs)) {
		    	ctMethod.setBody(template);
		    }
		}
	}
	
}
