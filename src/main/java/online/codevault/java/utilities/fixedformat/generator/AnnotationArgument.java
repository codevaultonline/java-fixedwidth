package online.codevault.java.utilities.fixedformat.generator;

public class AnnotationArgument {

    private String name;
    private Class classType;
    private Object value;

    public AnnotationArgument(String name, Class classType, Object value) {
        this.name = name;
        this.classType = classType;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getClassType() {
        return classType;
    }

    public void setClassType(Class classType) {
        this.classType = classType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
