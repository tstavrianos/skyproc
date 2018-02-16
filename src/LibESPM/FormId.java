package LibESPM;

import java.util.List;
import java.util.Objects;

public class FormId implements Comparable<FormId> {
    private int objectIndex;
    private String pluginName;

    public FormId(String parentPluginName, List<String> masters, int formId) {
        int modIndex = formId >>> 24;
        objectIndex = formId & ~(modIndex << 24);
        if(modIndex >= masters.size()) {
            pluginName = parentPluginName;
        } else {
            pluginName = masters.get(modIndex);
        }
    }

    public int getObjectIndex() {
        return objectIndex;
    }

    public String getPluginName() {
        return pluginName;
    }


    @Override
    public int compareTo(FormId rhs) {
        if(objectIndex != rhs.getObjectIndex()) {
            return Integer.compare(objectIndex, rhs.getObjectIndex());
        }

        return pluginName.toLowerCase().compareTo(rhs.getPluginName().toLowerCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormId formId = (FormId) o;
        return objectIndex == formId.objectIndex &&
                Objects.equals(pluginName, formId.pluginName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(objectIndex, pluginName);
    }
}
