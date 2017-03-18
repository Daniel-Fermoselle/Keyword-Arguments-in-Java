package ist.meic.pa;
import javassist.*;

public class KeyConstructors {


	public static void main(String[] args) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
		System.out.println("Hello World");
		ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get("ist.meic.pa.Coco");
        CtMethod m = cc.getDeclaredMethod("say");
        m.insertBefore("{ System.out.println(\"Coco.say():\"); }");
        Class c = cc.toClass();
        Coco h = (Coco)c.newInstance();
        h.say();
	} 
}