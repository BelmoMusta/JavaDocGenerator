package musta.belmo.enumhandler.beans;

public class EnumAttribute {
    public static final EnumAttribute STRING = new EnumAttribute("value", "String");
    public static final EnumAttribute BOOLEAN = new EnumAttribute("value", "boolean");
    public static final EnumAttribute NUMBER = new EnumAttribute("value", "int");

    private String name;
    private String concreteType;

    public EnumAttribute(String name, String concreteType) {
        this.concreteType = concreteType;
        this.name = name;
    }

    public String getConcreteType() {
        return concreteType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
