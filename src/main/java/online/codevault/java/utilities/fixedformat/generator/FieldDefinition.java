package online.codevault.java.utilities.fixedformat.generator;

import online.codevault.java.utilities.fixedformat.Padding;
import online.codevault.java.utilities.fixedformat.annotations.BooleanDisplay;

public class FieldDefinition {

    public enum SignedValueIndicator {
        LEFT,
        RIGHT,
        UNDEFINED
    }

    private int ordinal;
    private String fieldName;
    private Class type = String.class;
    private int startPosition;
    private int endPosition;
    private int length;
    private Padding padding;
    private Character paddingCharacter;
    private String description;
    private String additionalInformation;
    private String extendedInformation;
    private String formatString;
    private BooleanDisplay booleanDisplay;
    private boolean mandatory = false;
    private boolean filler = false;
    private SignedValueIndicator signedValueIndicator;

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Padding getPadding() {
        return padding;
    }

    public void setPadding(Padding padding) {
        this.padding = padding;
    }

    public Character getPaddingCharacter() {
        return paddingCharacter;
    }

    public void setPaddingCharacter(Character paddingCharacter) {
        this.paddingCharacter = paddingCharacter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getExtendedInformation() {
        return extendedInformation;
    }

    public void setExtendedInformation(String extendedInformation) {
        this.extendedInformation = extendedInformation;
    }

    public String getFormatString() {
        return formatString;
    }

    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }

    public BooleanDisplay getBooleanDisplay() {
        return booleanDisplay;
    }

    public void setBooleanDisplay(BooleanDisplay booleanDisplay) {
        this.booleanDisplay = booleanDisplay;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isFiller() {
        return filler;
    }

    public void setFiller(boolean filler) {
        this.filler = filler;
    }

    public SignedValueIndicator getSignedValueIndicator() {
        return signedValueIndicator;
    }

    public void setSignedValueIndicator(SignedValueIndicator signedValueIndicator) {
        this.signedValueIndicator = signedValueIndicator;
    }
}