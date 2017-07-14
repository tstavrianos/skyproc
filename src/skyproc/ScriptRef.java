/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A script reference to be added/found in a major record's ScriptPackage
 * object.
 *
 * @author Justin Swanson
 */
public class ScriptRef extends Record implements Iterable<String> {

    StringNonNull name = new StringNonNull();
    int unknown = 0;
    ArrayList<ScriptProperty> properties = new ArrayList<>();

    ScriptRef() {
    }

    /**
     * Creates a script reference with the given name. Adding this to a major
     * record's ScriptPackage will attach the script to the record. Of course,
     * there must be a script with that name in the Data/Scripts/ folder for it
     * to actually do anything in-game.
     *
     * @param name
     */
    public ScriptRef(String name) {
	this.name.set(name);
    }

    ScriptRef(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	parseData(in, srcMod);
    }

    @Override
    final void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	name.set(in.extractString(in.extractInt(2)));
	unknown = in.extractInt(1);
	int propertyCount = in.extractInt(2);
	if (SPGlobal.logMods){
	    logMod(srcMod, "VMAD", "  Script " + name.toString() + " with " + propertyCount + " properties. Unknown: " + unknown);
	}
	for (int i = 0; i < propertyCount; i++) {
	    properties.add(new ScriptProperty(in, srcMod));
	}
    }

    @Override
    void export(ModExporter out) throws IOException {
	name.export(out);
	out.write(unknown, 1);
	out.write(properties.size(), 2);
	for (ScriptProperty p : properties) {
	    p.export(out);
	}
    }

    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>();
	for (ScriptProperty s : properties) {
	    out.addAll(s.allFormIDs());
	}
	return out;
    }

    @Override
    boolean isValid() {
	return name != null;
    }

    @Override
    public String toString() {
	return print();
    }

    @Override
    public String print() {
	return name.toString();
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("VMAD");
    }

    @Override
    Record getNew() {
	return new ScriptRef();
    }

    @Override
    int getHeaderLength() {
	return 0;
    }

    @Override
    int getSizeLength() {
	return 0;
    }

    @Override
    int getFluffLength() {
	return 0;
    }

    @Override
    int getContentLength(ModExporter out) {
	int len = name.getTotalLength(out) + 3;
	for (ScriptProperty p : properties) {
	    len += p.getTotalLength(out);
	}
	return len;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final ScriptRef other = (ScriptRef) obj;
	if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
	    return false;
	}
	return true;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
	int hash = 7;
	hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
	return hash;
    }

    /**
     *
     * @return Iterator over the Property names in the script.
     */
    @Override
    public Iterator<String> iterator() {
	return getProperties().iterator();
    }

    // Get/set
    /**
     * Sets the name of the script. This MUST match the name of the script file
     * you wish to attach.
     *
     * @param name
     */
    public void setName(String name) {
	this.name.set(name);
    }

    /**
     * Gets the name of the script
     *
     * @return
     */
    public String getName() {
	return name.print();
    }

    /**
     *
     * @return A copy of all the property names in the script.
     */
    public ArrayList<String> getProperties() {
	ArrayList<String> out = new ArrayList<>(properties.size());
	for (ScriptProperty p : properties) {
	    out.add(p.name.data);
	}
	return out;
    }

    void removeProperty(ScriptProperty property) {
	properties.remove(property);
    }

    /**
     * Removes a property with the given name from the script, if one exists.
     *
     * @param propertyName
     */
    public void removeProperty(String propertyName) {
	removeProperty(new ScriptProperty(propertyName, 0));
    }

    /**
     * Adds a boolean property to the script, does not check for duplicates.
     *
     * @param scriptName Script name to add property to
     * @param propertyName Name of the boolean property to add
     * @param booleanProperty What to set the boolean property to.
     */
    void addProperty(String propertyName, boolean booleanProperty) {
	properties.add(new ScriptProperty(propertyName, booleanProperty));
    }

    /**
     * Adds an integer property to the script, does not check for duplicates.
     *
     * @param scriptName Script name to add property to
     * @param propertyName Name of the integer property to add
     * @param integerProperty What to set the integer property to.
     */
    void addProperty(String propertyName, int integerProperty) {
	properties.add(new ScriptProperty(propertyName, integerProperty));
    }

    /**
     * Adds a FormID property to the script, does not check for duplicates.
     *
     * @param scriptName Script name to add property to
     * @param propertyName Name of the FormID property to add
     * @param idProperty What to set the FormID property to.
     */
    void addProperty(String propertyName, FormID idProperty) {
	properties.add(new ScriptProperty(propertyName, idProperty));
    }

    /**
     * Adds a float property to the script, does not check for duplicates.
     *
     * @param scriptName Script name to add property to (must already exist)
     * @param propertyName Property name to add
     * @param floatProperty Float value to assign to property
     */
    void addProperty(String propertyName, Float floatProperty) {
	properties.add(new ScriptProperty(propertyName, floatProperty));
    }

    void addProperty(String propertyName, Integer... in) {
	properties.add(new ScriptProperty(propertyName, in));
    }

    void addProperty(String propertyName, Float... in) {
	properties.add(new ScriptProperty(propertyName, in));
    }

    void addProperty(String propertyName, Boolean... in) {
	properties.add(new ScriptProperty(propertyName, in));
    }

    void addProperty(String propertyName, FormID... in) {
	properties.add(new ScriptProperty(propertyName, in));
    }

    void addProperty(String propertyName, String... in) {
	properties.add(new ScriptProperty(propertyName, in));
    }

    /**
     * Adds a boolean property to the script, checks for duplicates.
     *
     * @param propertyName Property name to add
     * @param booleanProperty Boolean value to assign to property
     */
    public void setProperty(String propertyName, boolean booleanProperty) {
	removeProperty(propertyName);
	addProperty(propertyName, booleanProperty);
    }

    /**
     * Adds a int property to the script, checks for duplicates.
     *
     * @param propertyName Property name to add
     * @param integerProperty Integer value to assign to property
     */
    public void setProperty(String propertyName, int integerProperty) {
	removeProperty(propertyName);
	addProperty(propertyName, integerProperty);
    }

    /**
     * Adds a FormID property to the script, checks for duplicates.
     *
     * @param propertyName Property name to add
     * @param idProperty FormID value to assign to property
     */
    public void setProperty(String propertyName, FormID idProperty) {
	removeProperty(propertyName);
	addProperty(propertyName, idProperty);
    }

    /**
     * Adds a float property to the script, checks for duplicates.
     *
     * @param propertyName Property name to add
     * @param floatProperty Float value to assign to property
     */
    public void setProperty(String propertyName, float floatProperty) {
	removeProperty(propertyName);
	addProperty(propertyName, floatProperty);
    }

    /**
     * Adds a float array property to the script, checks for duplicates.
     *
     * @param propertyName Property name to add
     * @param floatProperty Float array to assign to property
     */
    public void setProperty(String propertyName, Float... floatProperty) {
	removeProperty(propertyName);
	addProperty(propertyName, floatProperty);
    }

    /**
     * Adds a int array property to the script, checks for duplicates.
     *
     * @param propertyName Property name to add
     * @param intProperty Int array to assign to property
     */
    public void setProperty(String propertyName, Integer... intProperty) {
	removeProperty(propertyName);
	addProperty(propertyName, intProperty);
    }

    /**
     * Adds a boolean array property to the script, checks for duplicates.
     *
     * @param propertyName Property name to add
     * @param boolProperty
     */
    public void setProperty(String propertyName, Boolean... boolProperty) {
	removeProperty(propertyName);
	addProperty(propertyName, boolProperty);
    }

    /**
     * Adds a string array property to the script, checks for duplicates.
     *
     * @param propertyName Property name to add
     * @param stringProperty String array to assign to property
     */
    public void setProperty(String propertyName, String... stringProperty) {
	removeProperty(propertyName);
	addProperty(propertyName, stringProperty);
    }

    /**
     * Adds a FormID array property to the script, checks for duplicates.
     *
     * @param propertyName Property name to add
     * @param formProperty FormID array to assign to property
     */
    public void setProperty(String propertyName, FormID... formProperty) {
	removeProperty(propertyName);
	addProperty(propertyName, formProperty);
    }
}