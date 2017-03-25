package ist.meic.pa;

import javassist.*;

public class KeyConstructors {

	public static void main(String[] args) throws Throwable {
		Translator translator = new KeywordsTranslator();
        ClassPool pool = ClassPool.getDefault();
        Loader classLoader = new Loader();
        classLoader.addTranslator(pool, translator);
        classLoader.run(args);
	} 
}