package musta.belmo.enumhandler.beans;

/**
 * TODO: Compléter la description de cette classe
 *
 * @author toBeSpecified
 * @version 0.1
 * @since 1.0.0.SNAPSHOT
 */
public class EnumAttribute {

    /**
     * La constante {@link #STRING} de type {@link EnumAttribute} ayant la valeur new EnumAttribute(null, "String").
     */
    public static final EnumAttribute STRING = new EnumAttribute(null, "String");

    /**
     * La constante {@link #BOOLEAN} de type {@link EnumAttribute} ayant la valeur new EnumAttribute(null, "boolean").
     */
    public static final EnumAttribute BOOLEAN = new EnumAttribute(null, "boolean");

    /**
     * La constante {@link #NUMBER} de type {@link EnumAttribute} ayant la valeur new EnumAttribute(null, "int").
     */
    public static final EnumAttribute NUMBER = new EnumAttribute(null, "int");

    /**
     * L'attribut {@link #name}.
     */
    private String name;

    /**
     * L'attribut {@link #concreteType}.
     */
    private String concreteType;

    /**
     * Constructeur de la classe EnumAttribute
     *
     * @param name{@link         String}
     * @param concreteType{@link String}
     */
    public EnumAttribute(String name, String concreteType) {
        this.concreteType = concreteType;
        this.name = name;
    }

    /**
     * @return Attribut {@link #concreteType}
     */
    public String getConcreteType() {
        return concreteType;
    }

    /**
     * @return Attribut {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Valeur à affecter à l'attribut {@link #name}
     */
    public void setName(String name) {
        this.name = name;
    }
}
