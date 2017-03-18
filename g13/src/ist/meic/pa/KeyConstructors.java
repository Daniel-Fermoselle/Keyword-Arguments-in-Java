package ist.meic.pa;
import javassist.*;

public class KeyConstructors {

	public void say(){
		System.out.println("lasnfnasflkn");
	}
	
	public static void main(String[] args) {
		System.out.println("Hello World");
		ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get("KeyConstructors");
        CtMethod m = cc.getDeclaredMethod("say");
        m.insertBefore("{ System.out.println(\"KeyConstructors.say():\"); }");
        Class c = cc.toClass();
        KeyConstructors h = (KeyConstructors)c.newInstance();
        h.say();
	}

}