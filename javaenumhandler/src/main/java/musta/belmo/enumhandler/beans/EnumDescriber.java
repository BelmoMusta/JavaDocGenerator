package musta.belmo.enumhandler.beans;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO: Compléter la description de cette classe
 *
 * @author toBeSpecified
 * @version 0.1
 * @since 1.0.0.SNAPSHOT
 */
public class EnumDescriber {

    /**
     * L'attribut {@link #name}.
     */
    private String name;

    // private List<EnumAttribute> enumAttributes;
    /**
     * L'attribut {@link #map}.
     */
    private Map<String, List<EnumValueHolder>> map;

    /**
     * Constructeur de la classe EnumDescriber
     *
     * @param name{@link String}
     */
    public EnumDescriber(String name) {
        map = new LinkedHashMap<>();
        // enumAttributes = new ArrayList<>();
        this.name = name;
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
     * Add elements
     *
     * @param key             {@link String}
     * @param enumValueHolder {@link List}
     * @return List
     */
    public List<EnumValueHolder> addElements(String key, List<EnumValueHolder> enumValueHolder) {
        return map.put(key, enumValueHolder);
    }

    /**
     * Add element
     *
     * @param key             {@link String}
     * @param enumValueHolder {@link EnumValueHolder}
     */
    public void addElement(String key, EnumValueHolder enumValueHolder) {
        List<EnumValueHolder> enumValueHolders = map.get(key);
        if (enumValueHolders == null) {
            enumValueHolders = new ArrayList<>();
            map.put(key, enumValueHolders);
        }
        enumValueHolders.add(enumValueHolder);
    }

    /**
     * @param key {@link Object}
     * @return Attribut {@link #}
     */
    public List<EnumValueHolder> get(Object key) {
        return map.get(key);
    }

    /**
     * @return Attribut {@link #}
     */
    public Map<String, List<EnumValueHolder>> get() {
        return map;
    }

    /**
     * get Enum Attributes
     *
     * @return List
     */
    public List<EnumAttribute> getEnumAttributes() {
        Set<EnumAttribute> set = new LinkedHashSet<>();
        map.forEach((key, value) -> set.addAll(value.stream().map(EnumValueHolder::getType).collect(Collectors.toList())));
        return new ArrayList<>(set);
    }
}
