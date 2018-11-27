package musta.belmo.enumhandler.beans;

/**
 * TODO: Compléter la description de cette classe
 *
 * @author toBeSpecified
 * @version 0.1
 * @since 1.0.0.SNAPSHOT
 */
public class EnumValueHolder {

    /**
     * L'attribut {@link #name}.
     */
    private String name;

    /**
     * L'attribut {@link #type}.
     */
    private EnumAttribute type;

    /**
     * Constructeur de la classe EnumValueHolder
     *
     * @param name{@link String}
     * @param type{@link EnumAttribute}
     */
    public EnumValueHolder(String name, EnumAttribute type) {
        this.name = name;
        this.type = type;
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

    /**
     * @return Attribut {@link #type}
     */
    public EnumAttribute getType() {
        return type;
    }

    /**
     * @param type Valeur à affecter à l'attribut {@link #type}
     */
    public void setType(EnumAttribute type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EnumValueHolder))
            return false;
        EnumValueHolder that = (EnumValueHolder) o;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        return type != null ? type.equals(that.type) : that.type == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
