package musta.belmo.enumhandler.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumDescriber {

    private String name;

    private boolean string;

    private Map<String, List<EnumValueHolder>> map;
    private boolean entriesOnly;

    public void setEntriesOnly(boolean entriesOnly) {
        this.entriesOnly = entriesOnly;
    }

    public boolean isEntriesOnly() {
        return entriesOnly;
    }

    public boolean isString() {
        return string;
    }

    public void setString(boolean string) {
        this.string = string;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnumDescriber(String name) {
        map = new HashMap<>();
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
        } else {
            enumValueHolders.add(enumValueHolder);
        }

    }

    public List<EnumValueHolder> get(Object key) {
        return map.get(key);
    }

    public Map<String, List<EnumValueHolder>> get() {
        return map;
    }
}
