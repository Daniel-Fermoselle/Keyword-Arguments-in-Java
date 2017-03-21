import ist.meic.pa.KeywordArgs;


class Widget {
    int width;
    int height;
    int margin;

    @KeywordArgs("width=100,height=50,margin")
    public Widget(Object... args) {
       /* Class<Widget> classe = Widget.class
        System.out.println("After declarations: ");
        try {
            java.util.ArrayList fieldNames = new java.util.ArrayList();
            java.lang.reflect.Field[] fields = classe.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fieldNames.add(fields[i].getName());
            }
            java.util.ArrayList keywords = new java.util.ArrayList();
            for (int i = 0; i < args.length; i = i + 2) {
                String arg = args[i].toString();
                if (fieldNames.contains(arg)) {
                    if (!keywords.contains(arg)) {
                        keywords.add(arg);
                        args[i] = args[i + 1];
                    } else {
                        throw new RuntimeException("Duplicated keyword: "arg);
                    }
                } else {
                    throw new RuntimeException("Unrecognize keyword: "arg);
                }
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error");
        }*/

/*try {
    java.util.ArrayList fieldNames = new java.util.ArrayList();
    java.lang.reflect.Field[] fields = this.getClass().getFields();
    for (java.lang.reflect.Field field : fields) {
        fieldNames.add(field.getName());
    }
    java.util.HashMap keywords = new java.util.HashMap();
    for (int i = 0; i < args.length; i = i + 2) {
        String arg = args[i].toString();
        if (fieldNames.contains(arg)) {
            if (keywords.get(arg) == null || keywords.containsKey(arg) == false) {
                keywords.put(arg, true);
                args[i] = args[i + 1];
            } else {
                throw new RuntimeException("Duplicated keyword: " + arg);
            }
        } else {
            throw new RuntimeException("Unrecognize keyword: " + arg);
        }
    }
} catch (java.lang.Exception e) {
    e.printStackTrace();
    throw new RuntimeException("Unexpected error");
}*/
    }

    public String toString() {
        return String.format("width:%s, height:%s, margin:%s",
                width, height, margin);
    }
}
