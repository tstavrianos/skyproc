/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import skyproc.SubStringPointer.Files;

/**
 *
 * @author Justin Swanson
 */
public abstract class MajorRecordDescription extends MajorRecordNamed {

    static final SubPrototype descProto = new SubPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    SubStringPointer description = new SubStringPointer("DESC", Files.DLSTRINGS);
	    description.forceExport = true;
	    forceExport("DESC");
	    add(description);
	}
    };

    MajorRecordDescription() {
	super();
    }

    /**
     *
     * @return Description associated with the Major Record, or <NO TEXT> if
     * empty.
     */
    public String getDescription() {
	return subRecords.getSubStringPointer("DESC").print();
    }

    /**
     *
     * @param description String to set as the Major Record description.
     */
    public void setDescription(String description) {
	subRecords.setSubStringPointer("DESC", description);
    }

    SubStringPointer getDESC() {return subRecords.getSubStringPointer("DESC");}

    /**
     * Merges Major Records with descriptions. Implements MajorRecordNamed's
     * merger.
     *
     * @param no The new MajorRecordDescription to be merged.
     * @param bo The base MajorRecordDescription, to prevent base data from
     * being re-merged.
     * @return The modified MajorRecordDescription.
     */
    @Override
    public MajorRecord merge(MajorRecord no, MajorRecord bo) {
        super.merge(no, bo);
        MajorRecordDescription m = this;
        if (!(no == null && bo == null && (no instanceof MajorRecordDescription) && (bo instanceof MajorRecordDescription))) {
            final MajorRecordDescription nm = (MajorRecordDescription) no;
            final MajorRecordDescription bm = (MajorRecordDescription) bo;
            if (Merger.mTags.names) {
                m.getDESC().merge(nm.getDESC(), bm.getDESC());
            }
        }
        return m;
    }
}
