package ibb.api.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OBOStanza {

    public static enum Type {
        TERM, TYPEDEF, INSTANCE, UNKNOWN
    }

    private Type type;
    private Map<String, List<String>> tags;

    public OBOStanza(Type type) {
        this.type = type;
        tags = new HashMap<>();
    }

    public void addAttribute(String tag, String value) {
        tags.computeIfAbsent(tag, t -> new ArrayList<>()).add(value);
    }

    public Optional<String> getAttributeOptional(String tag) {
        return getAttributes(tag).stream().findFirst();
    }

    public String getAttribute(String tag) {
        try {
            return getAttributes(tag).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public List<String> getAttributes(String tag) {
        return tags.getOrDefault(tag, Collections.emptyList());
    }

    public Type getType() {
        return type;
    }

    public String getId() {
        return getAttribute("id");
    }

    @Override
    public String toString() {
        return "[" + type + "] " + getId();
    }
}
