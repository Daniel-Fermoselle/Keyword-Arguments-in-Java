import ist.meic.pa.KeywordArgs;


class Widget {
    int width;
    int height;
    int margin;

    @KeywordArgs("width=100,height=50,margin")
    public Widget(Object... args) {
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
