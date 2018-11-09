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
}
