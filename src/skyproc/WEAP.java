/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;
import skyproc.genenums.Skill;
import skyproc.genenums.SoundVolume;

/**
 * Weapon Records
 *
 * @author Justin Swanson
 */
public class WEAP extends MajorRecordDescription {

    // Static prototypes and definitions
    static final SubPrototype WEAPproto = new SubPrototype(MajorRecordDescription.descProto) {

	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), "EDID");
	    add(new SubData("OBND", new byte[12]));
	    reposition("FULL");
	    add(new Model());
            add(new SubString("ICON"));
            add(new SubString("MICO"));
	    add(new SubForm("EITM"));
	    add(new SubInt("EAMT", 2));
            add(new DestructionData());
	    add(new SubForm("ETYP"));
	    add(new SubForm("BIDS"));
	    add(new SubForm("BAMT"));
            add(new SubForm("YNAM"));
            add(new SubForm("ZNAM"));
	    add(new KeywordSet());
	    reposition("DESC");
	    add(SubString.getNew("NNAM", true));
	    add(new SubForm("INAM"));
	    add(new SubForm("WNAM"));
	    add(new SubList<>(new SubData("ENAM")));
	    add(new SubForm("SNAM"));
	    add(new SubForm("XNAM"));
            add(new SubForm("NAM7"));
	    add(new SubForm("TNAM"));
	    add(new SubForm("UNAM"));
	    add(new SubForm("NAM9"));
	    add(new SubForm("NAM8"));
	    add(new DATA());
	    add(new DNAM());
	    add(new CRDT());
	    add(new SubInt("VNAM", 4));
	    add(new SubForm("CNAM"));
	}
    };
    static final class DNAM extends SubRecord {

	WeaponType wtype;
	byte[] unknown1;
	float speed;
	float reach;
	LFlags flags1 = new LFlags(4);
	float sightFOV;
	byte[] unknown2;
	int vats;
	byte[] attackAnimation;
	int numProjectiles;
	int embeddedWeapActorValue;
	float minRange;
	float maxRange;
	byte[] onHit;
	LFlags flags2 = new LFlags(4);
        float animationAttackMult;
        float unknownFloat;
        float rumbleLeftMotorStrength;
        float rumbleRightMotorStrength;
        float runbleDuration;
	byte[] unknown6;
	//LFlags flags3 = new LFlags(4);
        int skill;
	byte[] unknown7;
	byte[] resist;
	byte[] unknown8;
	float stagger;

	public DNAM() {
	    super();
	}
        
        DNAM(DNAM rhs){
            this();
            wtype = rhs.wtype;
            unknown1 = rhs.unknown1;
            speed = rhs.speed;
            reach = rhs.reach;
            flags1.set(rhs.flags1.export());
            sightFOV = rhs.sightFOV;
            unknown2 = rhs.unknown2;
            vats = rhs.vats;
            attackAnimation = rhs.attackAnimation;
            numProjectiles = rhs.numProjectiles;
            embeddedWeapActorValue = rhs.embeddedWeapActorValue;
            minRange = rhs.minRange;
            maxRange = rhs.maxRange;
            onHit = rhs.onHit;
            flags2.set(rhs.flags2.export());
            animationAttackMult = rhs.animationAttackMult;
            unknownFloat = rhs.unknownFloat;
            rumbleLeftMotorStrength = rhs.rumbleLeftMotorStrength;
            rumbleRightMotorStrength = rhs.rumbleRightMotorStrength;
            runbleDuration = rhs.runbleDuration;
            unknown6 = rhs.unknown6;
            skill = rhs.skill;
            unknown7 = rhs.unknown7;
            resist = rhs.resist;
            unknown8 = rhs.unknown8;
            stagger = rhs.stagger;
        }

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(wtype.ordinal(), 1);
	    out.write(unknown1, 3);
	    out.write(speed);
	    out.write(reach);
	    out.write(flags1.export());
	    out.write(sightFOV);
	    out.write(unknown2, 4);
	    out.write(vats, 1);
	    out.write(attackAnimation, 1);
	    out.write(numProjectiles, 1);
	    out.write(embeddedWeapActorValue, 1);
	    out.write(minRange);
	    out.write(maxRange);
	    out.write(onHit, 4);
	    out.write(flags2.export());
            out.write(animationAttackMult);
            out.write(unknownFloat);
            out.write(rumbleLeftMotorStrength);
            out.write(rumbleRightMotorStrength);
            out.write(runbleDuration);
	    out.write(unknown6, 12);
	    //out.write(flags3.export());
            out.write(skill);
	    out.write(unknown7, 8);
	    out.write(resist, 4);
	    out.write(unknown8, 4);
	    out.write(stagger);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    wtype = WeaponType.values()[in.extractInt(1)];
	    unknown1 = in.extract(3);
	    speed = in.extractFloat();
	    reach = in.extractFloat();
	    flags1.set(in.extract(4));
	    sightFOV = in.extractFloat();
	    unknown2 = in.extract(4);
	    vats = in.extractInt(1);
	    attackAnimation = in.extract(1);
	    numProjectiles = in.extractInt(1);
	    embeddedWeapActorValue = in.extractInt(1);
	    minRange = in.extractFloat();
	    maxRange = in.extractFloat();
	    onHit = in.extract(4);
	    flags2.set(in.extract(4));
            animationAttackMult = in.extractFloat();
            unknownFloat = in.extractFloat();
            rumbleLeftMotorStrength = in.extractFloat();
            rumbleRightMotorStrength = in.extractFloat();
            runbleDuration = in.extractFloat();
	    unknown6 = in.extract(12);
//	    flags3.set(in.extract(4));
            skill = in.extractInt(4);
	    unknown7 = in.extract(8);
	    resist = in.extract(4);
	    unknown8 = in.extract(4);
	    stagger = in.extractFloat();
	    if (SPGlobal.logMods){
		logMod(srcMod, "", "WType: " + wtype + ", speed: " + speed + ", reach: " + reach);
		logMod(srcMod, "", "SightFOV: " + sightFOV + ", vats: " + vats + ", numProjectiles: " + numProjectiles);
		logMod(srcMod, "", "EmbeddedWeapActorVal: " + embeddedWeapActorValue + ", MinRange: " + minRange + ", MaxRange: " + maxRange);
		logMod(srcMod, "", "stagger: " + stagger + ", Bound: " + get(WeaponFlag.BoundWeapon) + ", Cant Drop: " + get(WeaponFlag.CantDrop));
		logMod(srcMod, "", "Hide Backpack: " + get(WeaponFlag.HideBackpack) + ", Ignore Normal Weapon Resistance: " + get(WeaponFlag.IgnoresNormalWeaponResistance) + ", Minor Crime: " + get(WeaponFlag.MinorCrime));
		logMod(srcMod, "", "NPCs Use Ammo: " + get(WeaponFlag.NPCsUseAmmo) + ", No jam after reload: " + get(WeaponFlag.NoJamAfterReload) + ", Non Hostile: " + get(WeaponFlag.NonHostile));
		logMod(srcMod, "", "Non Playable: " + get(WeaponFlag.NonPlayable) + ", Not used in normal combat: " + get(WeaponFlag.NotUsedInNormalCombat) + ", Player Only: " + get(WeaponFlag.PlayerOnly));
	    }
	}

	public boolean get(WeaponFlag flag) {
	    switch (flag.flagSet) {
		case 0:
		    return flags1.get(flag.value);
		case 1:
		    return flags2.get(flag.value);
		default:
		    return false;
	    }
	}

	public void set(WeaponFlag flag, boolean on) {
	    switch (flag.flagSet) {
		case 0:
		    flags1.set(flag.value, on);
		    break;
		case 1:
		    flags2.set(flag.value, on);
		    break;
	    }
	}

	@Override
	SubRecord getNew(String type) {
	    return new DNAM();
	}

	@Override
	boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 100;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DNAM");
	}
        
        public Skill getSkill(){
            return Skill.value(skill);
        }
        
        public void setSkill(Skill skill) {
            this.skill = Skill.value(skill);
        }

		@Override
		public SubRecord merge(SubRecord no, SubRecord bo) {
			DNAM e = this;
			if (!(no == null && bo == null && (no instanceof DNAM) && (bo instanceof DNAM))) {
				final DNAM ne = (DNAM) no;
				final DNAM be = (DNAM) bo;
				Merger.merge(e.wtype, ne.wtype, be.wtype, getType(), "weapon type");
				Merger.merge(e.unknown1, ne.unknown1, be.unknown1, getType(), "unknown");
				Merger.merge(e.speed, ne.speed, be.speed, getType(), "speed");
				Merger.merge(e.reach, ne.reach, be.reach, getType(), "reach");
				Merger.merge(e.sightFOV, ne.sightFOV, be.sightFOV, getType(), "sight FOV");
				Merger.merge(e.unknown2, ne.unknown2, be.unknown2, getType(), "unknown");
				Merger.merge(e.vats, ne.vats, be.vats, getType(), "VATS?");
				Merger.merge(e.attackAnimation, ne.attackAnimation, be.attackAnimation, getType(), "attackAnimation");
				Merger.merge(e.numProjectiles, ne.numProjectiles, be.numProjectiles, getType(), "number of projectiles");
				Merger.merge(e.embeddedWeapActorValue, ne.embeddedWeapActorValue, be.embeddedWeapActorValue, getType(), "embedded weapon actor value");
				Merger.merge(e.minRange, ne.minRange, be.minRange, getType(), "min range");
				Merger.merge(e.maxRange, ne.maxRange, be.maxRange, getType(), "max range");
				Merger.merge(e.onHit, ne.onHit, be.onHit, getType(), "onHit");
				Merger.merge(e.unknown6, ne.unknown6, be.unknown6, getType(), "unknown");
				Merger.merge(e.unknown7, ne.unknown7, be.unknown7, getType(), "unknown");
				Merger.merge(e.resist, ne.resist, be.resist, getType(), "resist");
				Merger.merge(e.unknown8, ne.unknown8, be.unknown8, getType(), "unknown");
				Merger.merge(e.stagger, ne.stagger, be.stagger, getType(), "stagger");
				e.flags1 = Merger.merge(e.flags1, ne.flags1, be.flags1, getType());
				e.flags2 = Merger.merge(e.flags2, ne.flags2, be.flags2, getType());
				//e.flags3.merge(ne.flags3, be.flags3, getType());
				Merger.merge(e.skill, ne.skill, be.skill, getType(), "skill");
			}
			return e;
		}
    }

    static final class DATA extends SubRecord {

	int value = 0;
	float weight = 0;
	int damage = 0;

	public DATA() {
	    super();
	}
        
        DATA(DATA rhs){
            this();
            value = rhs.value;
            weight = rhs.weight;
            damage = rhs.damage;
        }

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(value);
	    out.write(weight);
	    out.write(damage, 2);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    value = in.extractInt(4);
	    weight = in.extractFloat();
	    damage = in.extractInt(2);
	    if (SPGlobal.logMods){
		logMod(srcMod, "", "Value: " + value + ", weight: " + weight + ", damage: " + damage);
	    }
	}

	@Override
	SubRecord getNew(String type) {
	    return new DATA();
	}

	@Override
	boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 10;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DATA");
	}

		@Override
		public SubRecord merge(SubRecord no, SubRecord bo) {
			DATA e = this;
			if (!(no == null && bo == null && (no instanceof DATA) && (bo instanceof DATA))) {
				final DATA ne = (DATA) no;
				final DATA be = (DATA) bo;
				Merger.merge(e.value, ne.value, be.value, getType(), "value");
				Merger.merge(e.weight, ne.weight, be.weight, getType(), "weight");
				Merger.merge(e.damage, ne.damage, be.damage, getType(), "damage");
			}
			return e;
		}
    }

    static final class CRDT extends SubRecord {

	int critDmg;
	byte[] unknown0;
	float critMult;
	int onDeath;
	byte[] unused;
        byte[] unknown;
	FormID critEffect = new FormID();
        byte[] unknown2;

	public CRDT() {
	    super();
	}

        private CRDT(CRDT rhs) {
            this();
            critDmg = rhs.critDmg;
            unknown0 = rhs.unknown0;
            critMult = rhs.critMult;
            onDeath = rhs.onDeath;
            unused = rhs.unused;
            unknown = rhs.unknown;
            critEffect.setTo(rhs.critEffect);
            unknown2 = rhs.unknown2;
        }

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(critDmg);
	    out.write(critMult);
	    out.write(onDeath, 1);
	    out.write(unused, 3);
	    out.write(unknown, 4);
	    critEffect.export(out);
	    out.write(unknown2, 4);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    critDmg = in.extractInt(2);
	    unknown0 = in.extract(2);
	    critMult = in.extractFloat();
	    onDeath = in.extractInt(1);
	    unused = in.extract(3);
	    unknown = in.extract(4);
	    critEffect.parseData(in, srcMod);
	    unknown2 = in.extract(4);
	    if (SPGlobal.logMods){
		logMod(srcMod, "", "critDmg: " + critDmg + ", critMult: " + critMult + ", crit effect: " + critEffect);
	    }
	}

	@Override
	SubRecord getNew(String type) {
	    return new CRDT();
	}

	@Override
	boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 16;
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(1);
	    out.add(critEffect);
	    return out;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("CRDT");
	}

		@Override
		public SubRecord merge(SubRecord no, SubRecord bo) {
			CRDT e = this;
			if (!(no == null && bo == null && (no instanceof CRDT) && (bo instanceof CRDT))) {
				final CRDT ne = (CRDT) no;
				final CRDT be = (CRDT) bo;
				Merger.merge(e.critDmg, ne.critDmg, be.critDmg, getType(), "critical damage");
				Merger.merge(e.unknown0, ne.unknown0, be.unknown0, getType(), "unknown");
				Merger.merge(e.critMult, ne.critMult, be.critMult, getType(), "critical multiplier");
				Merger.merge(e.onDeath, ne.onDeath, be.onDeath, getType(), "on death (?)");
				Merger.merge(e.unknown, ne.unknown, be.unknown, getType(), "unknown");
				e.critEffect.merge(ne.critEffect, be.critEffect, getType());
			}
			return e;
		}
    }

    // Enums
    /**
     * An enum to represent to Weapon Type options
     */
    public enum WeaponType {

	/**
	 *
	 */
	HandToHandMelee,
	/**
	 *
	 */
	OneHSword,
	/**
	 *
	 */
	Dagger,
	/**
	 *
	 */
	OneHAxe,
	/**
	 *
	 */
	OneHBlunt,
	/**
	 *
	 */
	TwoHSword,
	/**
	 *
	 */
	TwoHBluntAxe,
	/**
	 *
	 */
	Bow,
	/**
	 *
	 */
	Staff,
	/**
	 *
	 */
	Crossbow;
    }

    /**
     *
     */
    public enum WeaponFlag {

	/**
	 *
	 */
	IgnoresNormalWeaponResistance(1, 0),
	/**
	 *
	 */
	HideBackpack(4, 0),
	/**
	 *
	 */
	NonPlayable(7, 0),
	/**
	 *
	 */
	CantDrop(3, 0),
	/**
	 *
	 */
	PlayerOnly(0, 1),
	/**
	 *
	 */
	NPCsUseAmmo(1, 1),
	/**
	 *
	 */
	NoJamAfterReload(3, 1),
	/**
	 *
	 */
	MinorCrime(4, 1),
	/**
	 *
	 */
	NotUsedInNormalCombat(6, 1),
	/**
	 *
	 */
	NonHostile(8, 1),
	/**
	 *
	 */
	BoundWeapon(13, 1),;
	int value;
	int flagSet;

	WeaponFlag(int value, int flagSet) {
	    this.value = value;
	    this.flagSet = flagSet;
	}
    }

    // Common Functions
    WEAP() {
	super();
	subRecords.setPrototype(WEAPproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("WEAP");
    }

    @Override
    Record getNew() {
	return new WEAP();
    }

    // Get /set
    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

    /**
     *
     * @return
     */
    public KeywordSet getKeywordSet() {
	return subRecords.getKeywords();
    }

    DATA getDATA() {
	return (DATA) subRecords.get("DATA");
    }

    /**
     *
     * @param value
     */
    public void setValue(int value) {
	getDATA().value = Math.abs(value);
    }

    /**
     *
     * @return
     */
    public int getValue() {
	return getDATA().value;
    }

    /**
     *
     * @param weight
     */
    public void setWeight(float weight) {
	getDATA().weight = weight;
    }

    /**
     *
     * @return
     */
    public float getWeight() {
	return getDATA().weight;
    }

    /**
     *
     * @param damage
     */
    public void setDamage(int damage) {
	getDATA().damage = Math.abs(damage);
    }

    /**
     *
     * @return
     */
    public int getDamage() {
	return getDATA().damage;
    }

    /**
     *
     * @param amount
     */
    public void setEnchantmentCharge(int amount) {
	subRecords.setSubInt("EAMT", amount);
    }

    /**
     *
     * @return
     */
    public int getEnchantmentCharge() {
	return subRecords.getSubInt("EAMT").get();
    }

    /**
     *
     * @param id
     */
    public void setEnchantment(FormID id) {
	subRecords.setSubForm("EITM", id);
    }

    /**
     *
     * @return
     */
    public FormID getEnchantment() {
	return subRecords.getSubForm("EITM").getForm();
    }

    /**
     *
     * @param id
     */
    public void setEquipmentSlot(FormID id) {
	subRecords.setSubForm("ETYP", id);
    }

    /**
     *
     * @return
     */
    public FormID getEquipmentSlot() {
	return subRecords.getSubForm("ETYP").getForm();
    }

    /**
     *
     * @param id
     */
    public void setImpactSet(FormID id) {
	subRecords.setSubForm("INAM", id);
    }

    /**
     *
     * @return
     */
    public FormID getImpactSet() {
	return subRecords.getSubForm("INAM").getForm();
    }

    /**
     * @deprecated use getModelData()
     * @param filename
     */
    public void setModelFilename(String filename) {
	subRecords.getModel().setFileName(filename);
    }

    /**
     * @deprecated use getModelData()
     * @return
     */
    public String getModelFilename() {
	return subRecords.getModel().getFileName();
    }

    /**
     *
     * @param id
     */
    public void setSheathSound(FormID id) {
	subRecords.setSubForm("NAM8", id);
    }

    /**
     *
     * @return
     */
    public FormID getSheathSound() {
	return subRecords.getSubForm("NAM8").getForm();
    }

    /**
     *
     * @param id
     */
    public void setDrawSound(FormID id) {
	subRecords.setSubForm("NAM9", id);
    }

    /**
     *
     * @return
     */
    public FormID getDrawSound() {
	return subRecords.getSubForm("NAM9").getForm();
    }

    /**
     * Field TNAM - Attack Fail Sound
     * @param id
     */
    public void setSwingSound(FormID id) {
	subRecords.setSubForm("TNAM", id);
    }

    /**
     * Field TNAM - Attack Fail Sound
     * @return
     */
    public FormID getSwingSound() {
	return subRecords.getSubForm("TNAM").getForm();
    }

    /**
     *
     * @param id
     */
    public void setBoundWeaponSound(FormID id) {
	subRecords.setSubForm("UNAM", id);
    }

    /**
     *
     * @return
     */
    public FormID getBoundWeaponSound() {
	return subRecords.getSubForm("UNAM").getForm();
    }

    DNAM getDNAM() {
	return (DNAM) subRecords.get("DNAM");
    }

    /**
     *
     * @param in
     */
    public void setWeaponType(WeaponType in) {
	getDNAM().wtype = in;
    }

    /**
     *
     * @return
     */
    public WeaponType getWeaponType() {
	return getDNAM().wtype;
    }

    /**
     *
     * @param speed
     */
    public void setSpeed(float speed) {
	getDNAM().speed = speed;
    }

    /**
     *
     * @return
     */
    public float getSpeed() {
	return getDNAM().speed;
    }

    /**
     *
     * @param reach
     */
    public void setReach(float reach) {
	getDNAM().reach = reach;
    }

    /**
     *
     * @return
     */
    public float getReach() {
	return getDNAM().reach;
    }

    /**
     *
     * @param fov
     */
    public void setSightFOV(float fov) {
	getDNAM().sightFOV = fov;
    }

    /**
     *
     * @return
     */
    public float getSightFOV() {
	return getDNAM().sightFOV;
    }

    /**
     *
     * @param vats
     */
    public void setVATS(int vats) {
	getDNAM().vats = vats;
    }

    /**
     *
     * @return
     */
    public int getVATS() {
	return getDNAM().vats;
    }

    /**
     *
     * @param numProj
     */
    public void setNumProjectiles(int numProj) {
	getDNAM().numProjectiles = numProj;
    }

    /**
     *
     * @return
     */
    public int getNumProjectiles() {
	return getDNAM().numProjectiles;
    }

    /**
     *
     * @param minRange
     */
    public void setMinRange(float minRange) {
	getDNAM().minRange = minRange;
    }

    /**
     *
     * @return
     */
    public float getMinRange() {
	return getDNAM().minRange;
    }

    /**
     *
     * @param maxRange
     */
    public void setMaxRange(float maxRange) {
	getDNAM().maxRange = maxRange;
    }

    /**
     *
     * @return
     */
    public float getMaxRange() {
	return getDNAM().maxRange;
    }

    /**
     *
     * @param stagger
     */
    public void setStagger(float stagger) {
	getDNAM().stagger = stagger;
    }

    /**
     *
     * @return
     */
    public float getStagger() {
	return getDNAM().stagger;
    }

    CRDT getCRDT() {
	return (CRDT) subRecords.get("CRDT");
    }

    /**
     *
     * @param critDmg
     */
    public void setCritDamage(int critDmg) {
	getCRDT().critDmg = critDmg;
    }

    /**
     *
     * @return
     */
    public int getCritDamage() {
	return getCRDT().critDmg;
    }

    /**
     *
     * @param critMult
     */
    public void setCritMult(float critMult) {
	getCRDT().critMult = critMult;
    }

    /**
     *
     * @return
     */
    public float getCritMult() {
	return getCRDT().critMult;
    }

    /**
     *
     * @param onDeath
     */
    public void setCritEffectOnDeath(boolean onDeath) {
	if (onDeath) {
	    getCRDT().onDeath = 1;
	} else {
	    getCRDT().onDeath = 0;
	}
    }

    /**
     *
     * @return
     */
    public boolean getCritEffectOnDeath() {
        return getCRDT().onDeath != 0;
    }

    /**
     *
     * @param critEffect
     */
    public void setCritEffect(FormID critEffect) {
	getCRDT().critEffect = critEffect;
    }

    /**
     *
     * @return
     */
    public FormID getCritEffect() {
	return getCRDT().critEffect;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(WeaponFlag flag, boolean on) {
	getDNAM().set(flag, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(WeaponFlag flag) {
	return getDNAM().get(flag);
    }
    
    /**
     *
     * @return
     */
    public Skill getSkill(){
        return getDNAM().getSkill();
    }
    
    /**
     *
     * @param skill
     */
    public void setSkill(Skill skill){
        getDNAM().setSkill(skill);
    }
    
    /**
     *
     * @return
     */
    public SoundVolume getDetectionSoundLevel() {
        return SoundVolume.values()[subRecords.getSubInt("VNAM").get()];
    }
    
    /**
     *
     * @param level
     */
    public void setDetectionSoundLevel(SoundVolume level){
        subRecords.setSubInt("VNAM", level.ordinal());
    }

    /**
     *
     * @param weap
     */
    public void setTemplate(FormID weap) {
        if (subRecords.contains("CNAM")) {
            subRecords.setSubForm("CNAM", weap);
        } else {
            subRecords.add(new SubForm("CNAM", weap));
        }
    }

    /**
     *
     * @return
     */
    public FormID getTemplate() {
        SubForm sub = subRecords.getSubForm("CNAM");
        return (sub != null) ? sub.getForm() : FormID.NULL;
	//return subRecords.getSubForm("CNAM").getForm();
    }

    /**
     *
     * @return
     */
    public boolean isTemplated() {
	return !FormID.NULL.equals(getTemplate());
    }

    /**
     * Returns the top of the Weapon Template "chain" and returns the top
     * (the one without any template).
     * Only returns null if a template formID is found, but no record exists with
     *  that formid.
     * @return
     */
    public WEAP getTemplateTop () {
	if (!isTemplated()) {
	    return this;
	} else {
	    WEAP template = (WEAP) SPDatabase.getMajor(getTemplate(), GRUP_TYPE.WEAP);
	    if (template != null) {
		return template.getTemplateTop();
	    }
	}
	return null;
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

    /**
     * @deprecated use getModelData()
     * @param rhs
     * @return
     */
    public boolean equalAltTextures(WEAP rhs) {
	return AltTextures.equal(getAltTextures(), rhs.getAltTextures());
    }

    /**
     *
     * @return
     */
    public FormID getFirstPersonModel() {
	return subRecords.getSubForm("WNAM").getForm();
    }

    /**
     *
     * @param id
     */
    public void setFirstPersonModel(FormID id) {
	subRecords.setSubForm("WNAM", id);
    }
    
    /**
     *
     * @return 
     */
    public FormID getBlockBashImpactDataSet() {
        return subRecords.getSubForm("BIDS").getForm();
    }
    
    /**
     *
     * @param id
     */
    public void setBlockBashImpactDataSet(FormID id) {
        subRecords.setSubForm("BIDS", id);
    }
    
    /**
     *
     * @return 
     */
    public FormID getAlternateBlockMaterial() {
        return subRecords.getSubForm("BAMT").getForm();
    }
    
    /**
     *
     * @param id
     */
    public void setAlternateBlockMaterial(FormID id) {
        subRecords.setSubForm("BAMT", id);
    }
    
    /**
     * 
     * @return returns false if there was an error copying from the template
     */
    public boolean unTemplate(){
        if(!isTemplated()){
            return true;
        }
        WEAP template = getTemplateTop();
	if (template == null) {
            return false;
        }
        int value = getValue();

        subRecords.setSubData("OBND", template.subRecords.getSubData("OBND").translate());
        subRecords.add(new Model(template.getModelData()));
        subRecords.setSubString("ICON", template.subRecords.getSubString("ICON").translate());
        subRecords.setSubString("MICO", template.subRecords.getSubString("MICO").translate());
        subRecords.add(new DestructionData()); // skip copying since its unused
        subRecords.setSubForm("ETYP", template.subRecords.getSubForm("ETYP").translate());
        subRecords.setSubForm("BIDS", template.subRecords.getSubForm("BIDS").translate());
        subRecords.setSubForm("BAMT", template.subRecords.getSubForm("BAMT").translate());
        subRecords.setSubForm("YNAM", template.subRecords.getSubForm("YNAM").translate());
        subRecords.setSubForm("ZNAM", template.subRecords.getSubForm("ZNAM").translate());
        subRecords.add(new KeywordSet(template.getKeywordSet()));
        subRecords.setSubStringPointer("DESC", template.getDescription());
        subRecords.setSubForm("INAM", template.subRecords.getSubForm("INAM").translate());
        subRecords.setSubForm("WNAM", template.subRecords.getSubForm("WNAM").translate());
        subRecords.setSubForm("SNAM", template.subRecords.getSubForm("SNAM").translate());
        subRecords.setSubForm("XNAM", template.subRecords.getSubForm("XNAM").translate());
        subRecords.setSubForm("NAM7", template.subRecords.getSubForm("NAM7").translate());
        subRecords.setSubForm("TNAM", template.subRecords.getSubForm("TNAM").translate());
        subRecords.setSubForm("UNAM", template.subRecords.getSubForm("UNAM").translate());
        subRecords.setSubForm("NAM9", template.subRecords.getSubForm("NAM9").translate());
        subRecords.setSubForm("NAM8", template.subRecords.getSubForm("NAM8").translate());
        subRecords.add(new DATA(template.getDATA()));
        subRecords.add(new DNAM(template.getDNAM()));
        subRecords.add(new CRDT(template.getCRDT()));
        subRecords.setSubInt("VNAM", template.subRecords.getSubInt("VNAM").translate());
        subRecords.setSubForm("CNAM", FormID.NULL);
        
        setValue(value);
        return true;
    }

	@Override
	public MajorRecord merge(MajorRecord no, MajorRecord bo) {
		super.merge(no, bo);
		WEAP e = this;
		if (!(no == null && bo == null && (no instanceof WEAP) && (bo instanceof WEAP))) {
			final WEAP ne = (WEAP) no;
			final WEAP be = (WEAP) bo;
			SubRecords sList = e.subRecords;
			SubRecords nsList = ne.subRecords;
			SubRecords bsList = be.subRecords;
			for (SubRecord s : sList) {
				s.merge(nsList.get(s.getType()), bsList.get(s.getType()));
			}
		}
		return e;
	}
}
