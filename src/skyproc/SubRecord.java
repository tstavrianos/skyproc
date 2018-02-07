/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import lev.LOutFile;

/**
 * An abstract class outlining the functionality of subrecords, which are
 * records within Major Records.
 *
 * @author Justin Swanson
 */
abstract class SubRecord<T> extends Record {

    @Override
    public String print() {
	return "No " + getType().toString();
    }

    @Override
    public String toString() {
	return getType().toString() + "[" + getClass().getSimpleName() + "]";
    }

    @Override
    int getSizeLength() {
	return 2;
    }

    @Override
    int getFluffLength() {
	return 0;
    }

    @Override
    void export(ModExporter out) throws IOException {
	out.write(getType().toString());
	out.write(getContentLength(out), getSizeLength());
    }

    abstract SubRecord getNew(String type);

    boolean confirmLink() {
	return true;
    }

    ArrayList<FormID> allFormIDs() {
	return new ArrayList<>(0);
    }

    T translate() {
	return (T) this;
    }

    SubRecord<T> translate(T in) {
	return this;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	SubRecord other = (SubRecord) obj;
	if (!other.getType().equals(getType())) {
	    return false;
	}
	return subRecordEquals(other);
    }

    boolean subRecordEquals(SubRecord subRecord) {
	throw new UnsupportedOperationException("Equals functionality not yet supported for subrecord: " + this.getClass().getSimpleName());
    }

    @Override
    Record getNew() {
	return getNew("NULL");
    }

    /**
     * Merges straight SubRecords with logging capabilities.
     *
     * @param no The new SubRecord to be merged in.
     * @param bo The base SubRecord, to prevent base data from being re-merged.
     * @return The modified SubRecord.
     */
    public SubRecord merge(SubRecord no, SubRecord bo) {
        SubRecord o = this;
        if (!o.equals(no) && !no.equals(bo)) {
            o = no;
            if (Merger.fullLogging) {
                Merger.logMerge((String)(getTypes().get(0)), o.toString());
            }
        }
        return o;
    }
}
