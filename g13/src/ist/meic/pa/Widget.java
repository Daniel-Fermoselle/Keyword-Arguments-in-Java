import ist.meic.pa.KeywordArgs;


class Widget {
    int width;
    int height;
    int margin;

    @KeywordArgs("width=100,height=50,margin")
    public Widget(Object... args) {

    }

    public String toString() {
        return String.format("width:%s, height:%s, margin:%s",
                width, height, margin);
    }
}
