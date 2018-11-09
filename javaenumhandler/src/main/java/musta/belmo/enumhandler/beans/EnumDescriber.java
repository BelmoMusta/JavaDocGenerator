package musta.belmo.enumhandler.beans;

import java.util.*;

public class EnumDescriber {

    private String name;

    List<EnumAttribute> enumAttributes;

    private Map<String, List<EnumValueHolder>> map;

    public EnumDescriber(String name) {
        map = new LinkedHashMap<>();
        enumAttributes = new ArrayList<>();
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<EnumValueHolder> addElements(String key, List<EnumValueHolder> enumValueHolder) {
        return map.put(key, enumValueHolder);
    }

    public void addElement(String key, EnumValueHolder enumValueHolder) {
        List<EnumValueHolder> enumValueHolders = map.get(key);
        if (enumValueHolders == null) {
            enumValueHolders = new ArrayList<>();
            map.put(key, enumValueHolders);
        }
        enumValueHolders.add(enumValueHolder);
    }

    public List<EnumValueHolder> get(Object key) {
        return map.get(key);
    }

    public Map<String, List<EnumValueHolder>> get() {
        return map;
    }


    public List<EnumAttribute> getEnumAttributes() {
        return enumAttributes;
    }

    public void setEnumAttributes(List<EnumAttribute> enumAttributes) {
        this.enumAttributes = enumAttributes;
    }

    public boolean add(EnumAttribute enumAttribute) {
        return this.enumAttributes.add(enumAttribute);
    }
}
