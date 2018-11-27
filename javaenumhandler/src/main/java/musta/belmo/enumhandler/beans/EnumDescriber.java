package musta.belmo.enumhandler.beans;

import java.util.*;
import java.util.stream.Collectors;

public class EnumDescriber {

    private String name;

    // private List<EnumAttribute> enumAttributes;

    private Map<String, List<EnumValueHolder>> map;

    public EnumDescriber(String name) {
        map = new LinkedHashMap<>();
        //enumAttributes = new ArrayList<>();
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
        Set<EnumAttribute> set = new LinkedHashSet<>();
        map.forEach((key, value) -> set.addAll(value.stream().map(EnumValueHolder::getType).collect(Collectors.toList())));
        return new ArrayList<>(set);
    }
}
