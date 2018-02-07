/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class AMMO extends MajorRecordDescription {

    // Static prototypes and definitions
    static final SubPrototype AMMOprototype = new SubPrototype(MajorRecordDescription.descProto) {

	@Override
	protected void addRecords() {
	    add(new SubData("OBND", new byte[12]));
	    reposition("FULL");
	    add(new Model());
            add(new SubString("ICON"));
            add(new SubString("MICO"));
            add(new DestructionData());
	    add(new SubForm("YNAM"));
	    add(new SubForm("ZNAM"));
	    reposition("DESC");
	    add(new KeywordSet());
	    add(new DATA());
	    add(SubString.getNew("ONAM", true));
	}
    };
    static final class DATA extends SubRecord {

	FormID projectile = new FormID();
	LFlags flags = new LFlags(1);
	float damage = 0;
	int value = 0;

	DATA() {
	    super();
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    projectile.export(out);
	    out.write(flags.export(), 4);
	    out.write(damage);
	    out.write(value);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    projectile.parseData(in, srcMod);
	    flags.set(in.extract(4));
	    damage = in.extractFloat();
	    value = in.extractInt(4);
	}

	@Override
	SubRecord getNew(String type) {
	    return new DATA();
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 16;
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(1);
	    out.add(projectile);
	    return out;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DATA");
	}

		@Override
		public SubRecord merge(SubRecord no, SubRecord bo) {
			DATA d = this;
			if (!(no == null && bo == null && getClass() != no.getClass() && getClass() != bo.getClass())) {
				final DATA nd = (DATA) no;
				final DATA bd = (DATA) bo;
				d.projectile.merge(nd.projectile, bd.projectile, getType());
				Merger.merge(d.damage, nd.damage, bd.damage, getType(), "damage");
				Merger.merge(d.value, nd.value, bd.value, getType(), "value");
				d.flags = Merger.merge(d.flags, nd.flags, bd.flags, getType());
			}
			return d;
		}
    }

    // Enums
    /**
     *
     */
    public enum AMMOFlag {

	/**
	 *
	 */
	IgnoresWeaponResistance,
	/**
	 *
	 */
	NonPlayable,
	/**
	 *
	 */
	//VanishesWhenNotInFlight,
        /*
         *
         */
        NonBolt;
    }

    // Common Functions
    AMMO() {
	super();
	subRecords.setPrototype(AMMOprototype);
    }

    @Override
    Record getNew() {
	return new AMMO();
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("AMMO");
    }

    //Get/Set
    /**
     *
     * @return
     */
    public KeywordSet getKeywordSet() {
	return subRecords.getKeywords();
    }

    /**
     * @deprecated use getModelData()
     * @param path
     */
    public void setModel(String path) {
	subRecords.getModel().setFileName(path);
    }

    /**
     * @deprecated use getModelData()
     * @return
     */
    public String getModel() {
	return subRecords.getModel().getFileName();
    }

    /**
     *
     * @param sound
     */
    public void setPickupSound(FormID sound) {
	subRecords.setSubForm("YNAM", sound);
    }

    /**
     *
     * @return
     */
    public FormID getPickupSound() {
	return subRecords.getSubForm("YNAM").getForm();
    }

    /**
     *
     * @param sound
     */
    public void setDropSound(FormID sound) {
	subRecords.setSubForm("ZNAM", sound);
    }

    /**
     *
     * @return
     */
    public FormID getDropSound() {
	return subRecords.getSubForm("ZNAM").getForm();
    }

    DATA getData() {
	return (DATA) subRecords.get("DATA");
    }

    /**
     *
     * @param projectile
     */
    public void setProjectile(FormID projectile) {
	getData().projectile = projectile;
    }

    /**
     *
     * @return
     */
    public FormID getProjectile() {
	return getData().projectile;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(AMMOFlag flag, boolean on) {
	getData().flags.set(flag.ordinal(), on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(AMMOFlag flag) {
	return getData().flags.get(flag.ordinal());
    }

    /**
     *
     * @param damage
     */
    public void setDamage(float damage) {
	getData().damage = damage;
    }

    /**
     *
     * @return
     */
    public float getDamage() {
	return getData().damage;
    }

    /**
     *
     * @param gold
     */
    public void setValue(int gold) {
	getData().value = gold;
    }

    /**
     *
     * @return
     */
    public int getValue() {
	return getData().value;
    }

    /**
     * @deprecated use getModelData()
     * @return List of the AltTextures applied.
     */
    public ArrayList<AltTextures.AltTexture> getAltTextures() {
	return subRecords.getModel().getAltTextures();
    }

    /**
     * 
     * @return
     */
    public Model getModelData() {
	return subRecords.getModel();
    }

	SubForm getYNAM(){return subRecords.getSubForm("YNAM");}
	SubForm getZNAM(){return subRecords.getSubForm("ZNAM");}
	SubData getOBND(){return subRecords.getSubData("OBND");}

	//SkyBash merger
	@Override
	public MajorRecord merge(MajorRecord no, MajorRecord bo) {
		super.merge(no, bo);
		AMMO a = this;
		if (!(no == null && bo == null && (no instanceof AMMO) && (bo instanceof AMMO))) {
			final AMMO na = (AMMO) no;
			final AMMO ba = (AMMO) bo;
			a.getData().merge(na.getData(), ba.getData());
			a.getModelData().merge(na.getModelData(), ba.getModelData());
			a.getYNAM().merge(na.getYNAM(), ba.getYNAM());
			a.getZNAM().merge(na.getZNAM(), ba.getZNAM());
			a.getKeywordSet().merge(na.getKeywordSet(), ba.getKeywordSet());
			a.getOBND().merge(na.getOBND(), ba.getOBND());
		}
		return a;
	}
}
