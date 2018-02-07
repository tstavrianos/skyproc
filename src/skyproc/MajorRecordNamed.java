package skyproc;

import java.io.Serializable;
import skyproc.SubStringPointer.Files;

/**
 * An extended major record that has a name. (FULL record)
 *
 * @author Justin Swanson
 */
public abstract class MajorRecordNamed extends MajorRecord implements Serializable {

    static final SubPrototype namedProto = new SubPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    add(new SubStringPointer("FULL", Files.STRINGS));
	}
    };

    MajorRecordNamed() {
	super();
    }

    /**
     * Returns the in-game name of the Major Record.
     *
     * @return
     */
    public String getName() {
	return subRecords.getSubStringPointer("FULL").print();
    }

    /**
     * Sets the in-game name of the Major Record.
     *
     * @param in The string to set the in-game name to.
     */
    public void setName(String in) {
	subRecords.setSubStringPointer("FULL", in);
    }

    SubStringPointer getFULL() {return subRecords.getSubStringPointer("FULL");}

    /**
     * Merges Major Records with names. Implements MajorRecord's merger.
     *
     * @param no The new MajorRecordNamed to be merged.
     * @param bo The base MajorRecordNamed, to prevent base data from being
     * re-merged.
     * @return The modified MajorRecordNamed.
     */
    @Override
    public MajorRecord merge(MajorRecord no, MajorRecord bo) {
        super.merge(no, bo);
        MajorRecordNamed m = this;
        if (!(no == null && bo == null && (no instanceof MajorRecordNamed) && (bo instanceof MajorRecordNamed))) {
            final MajorRecordNamed nm = (MajorRecordNamed) no;
            final MajorRecordNamed bm = (MajorRecordNamed) bo;
            m.getFULL().merge(nm.getFULL(), bm.getFULL());
        }
        return m;
    }
}
