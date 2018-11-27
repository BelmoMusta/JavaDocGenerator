package musta.belmo.enumhandler.beans;

public class EnumValueHolder {
    private String name;
    private EnumAttribute type;

    public EnumValueHolder(String name, EnumAttribute type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnumAttribute getType() {
        return type;
    }

    public void setType(EnumAttribute type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnumValueHolder)) return false;

        EnumValueHolder that = (EnumValueHolder) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return type != null ? type.equals(that.type) : that.type == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
