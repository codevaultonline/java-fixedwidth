package online.codevault.java.utilities.fixedformat.generator;

import java.util.ArrayList;
import java.util.List;

public class RecordDefinition {

    private String packageName;
    private String className;
    private String length;
    private String description;

    private List<FieldDefinition> fields = new ArrayList<>();

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public void addField(FieldDefinition field) {
        fields.add(field);
    }

    public void setFields(List<FieldDefinition> fields) {
        this.fields = fields;
    }

}
