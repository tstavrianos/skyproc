package skyproc;

import java.util.ArrayList;

/**
 * Leveled List.  A list of entries used for spawnpoints when choosing which actor to
 * spawn.  Each entry contains a FormID of an actor, a level, and a count to spawn.
 * @author Justin Swanson
 */
public class LVLN extends LeveledRecord {

    // Static prototypes and definitions
    static final SubPrototype LVLNproto = new SubPrototype(LeveledRecord.LeveledProto){

	@Override
	protected void addRecords() {
	    remove("FULL");
	    add(new Model());
	}
    };

    // Get/Set
    /**
     * Creates a Leveled List with no entries and default settings.
     * LVLN_Flags set to 0x01=All levels
     * Chance none set to 0.
     * Empty MODL, MODT, and COED.
     */
    LVLN() {
        super();
	subRecords.setPrototype(LVLNproto);
    }

    /**
     * Creates a new LVLN record with a FormID originating from the mod parameter.
     * @param edid EDID to assign the record.  Make sure it's unique.
     */
    public LVLN(String edid) {
        super(edid);
	subRecords.setPrototype(LVLNproto);
    }

    @Override
    ArrayList<String> getTypes() {
        return Record.getTypeList("LVLN");
    }

    /**
     * Prints out the contents of the LVLN to the asynchronous log.
     * @return The empty string.
     */
    @Override
    public String print() {
        super.print();
        logMod(srcMod, getTypes().toString(), "Chance none: " + getChanceNone() + ", Flags: " + subRecords.getSubFlag("LVLF").print());
        for (LeveledEntry entry : getEntries()) {
            entry.toString();
        }
        return "";
    }

    @Override
    Record getNew() {
        return new LVLN();
    }

    // Get/set
    /**
     * @deprecated use getModelData()
     * @return Model path associated with the LVLN.
     */
    public String getModelPath() {
        return subRecords.getModel().getFileName();
    }

    /**
     * @deprecated use getModelData()
     * @param in String to set the LVLN model path to.
     */
    public void setModelPath(String in) {
        subRecords.getModel().setFileName(in);
    }
    
    /**
     * 
     * @return
     */
    public Model getModelData() {
	return subRecords.getModel();
    }

    /**
     * Merges Major Records.
     *
     * @param no The new MajorRecord to be merged.
     * @param bo The base MajorRecord, to prevent base data from being
     * re-merged.
     * @return The modified MajorRecord.
     */
    @Override
    public MajorRecord merge(MajorRecord no, MajorRecord bo) {
        super.merge(no, bo);
        LVLN l = this;
        if (!(no == null && bo == null && (no instanceof LVLN) && (bo instanceof LVLN))) {
            final LVLN nl = (LVLN) no;
            final LVLN bl = (LVLN) bo;
            l.getModelData().merge(nl.getModelData(), bl.getModelData());
            //l.MODL.merge(nl.MODL, bl.MODL);
            //l.MODT.merge(nl.MODT, bl.MODT);
        }
        return l;
    }
}
