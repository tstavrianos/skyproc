/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.*;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A specialized collection of subrecords. Think of it as a special SkyProc
 * ArrayList used for subrecords.
 *
 * @param //S The type of subrecord the group contains.
 * @author Justin Swanson
 */
class SubList<S extends SubRecord<T>, T> extends SubRecord<ArrayList<S>> implements Iterable<S> {

    ArrayList<T> collection = new ArrayList<>();
    S prototype;
    S last;
    boolean unique = false;

    SubList(S prototype_) {
	super();
	prototype = prototype_;
    }

    SubList(S prototype_, boolean unique) {
	this(prototype_);
	setUnique(unique);
    }

    SubList(SubList rhs) {
	super();
	prototype = (S) rhs.prototype;
	unique = rhs.unique;
	collection.addAll(rhs.collection);
    }

    @Override
    int getHeaderLength() {
	return 0;
    }

    @Override
    boolean isValid() {
	return !collection.isEmpty();
    }

    public boolean contains(T s) {
	return collection.contains(s);
    }

    /**
     *
     * @param i Index of the item to retrieve.
     * @return The ith item.
     */
    public T get(int i) {
	return collection.get(i);
    }

    public boolean add(T item) {
	if (allow(item)) {
	    collection.add(item);
	    return true;
	} else {
	    return false;
	}
    }

    boolean allow(T item) {
	return !unique || !contains(item);
    }

    public boolean addAtIndex(T item, int i) {
	if (allow(item)) {
	    collection.add(i, item);
	    return true;
	} else {
	    return false;
	}
    }

    public boolean remove(T item) {
	return collection.remove(item);
    }

    /**
     * Removes an item based on its index in the list.
     *
     * @param i Index of the item to remove.
     */
    public void remove(int i) {
	collection.remove(i);
    }

    /**
     *
     * @return The number of items currently in the list.
     */
    public int size() {
	return collection.size();
    }

    public final void setUnique(boolean unique) {
	this.unique = unique;
    }

    public void sort() {
	TreeSet<T> sorter = new TreeSet<>();
	sorter.addAll(collection);
	collection.clear();
	collection.addAll(sorter);
    }

    public void clear() {
	collection.clear();
    }

    /**
     *
     * @return True if list is empty, and size == 0.
     */
    public boolean isEmpty() {
	return collection.isEmpty();
    }

    /**
     * This function will replace all records in the SubRecordList with the ones
     * given.<br><br> WARNING: All existing records will be lost.
     *
     * @param in ArrayList of records to replace the current ones.
     */
    public void setRecordsTo(ArrayList<T> in) {
	collection = in;
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	parseData(in, srcMod, getNextType(in));
    }

	/**
	 * This function will add all records given to the list using add().
	 *
	 * @param in ArrayList of records to add in.
	 */
	public void addRecordsTo(ArrayList<T> in) {
		for (T t : collection) {
			collection.add(t);
		}
	}

    void parseData(LImport in, Mod srcMod, String nextType) throws BadRecord, DataFormatException, BadParameter {
	if (nextType.equals(getType())) {
	    S newRecord = (S) prototype.getNew(getType());
	    last = newRecord;
	    newRecord.parseData(in, srcMod);
	    add(newRecord.translate());
	} else {
	    last.parseData(in, srcMod);
	}
    }

    @Override
    SubRecord getNew(String type) {
	return new SubList(this);
    }

    @Override
    int getContentLength(ModExporter out) {
	int length = 0;
	for (S s : translate()) {
	    length += s.getTotalLength(out);
	}
	return length;
    }

    @Override
    void export(ModExporter out) throws IOException {
	if (isValid()) {
	    for (S s : translate()) {
		s.export(out);
	    }
	}
    }

    ArrayList<T> toPublic() {
	return collection;
    }

    /**
     *
     * @return An iterator of all records in the SubRecordList.
     */
    @Override
    public Iterator<S> iterator() {
	return translate().iterator();
    }

    public void addAll(Collection<T> items) {
	collection.addAll(items);
    }
    
    @Override
    ArrayList<FormID> allFormIDs() {
	if (prototype.getClass().equals(SubForm.class)) {
	    return (ArrayList<FormID>) collection;
	}
	ArrayList<FormID> out = new ArrayList<>();
	for (S item : translate()) {
	    out.addAll(item.allFormIDs());
	}
	return out;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 89 * hash + Objects.hashCode(this.collection);
	return hash;
    }

    @Override
    public int getRecordLength(LImport in) {
	return prototype.getRecordLength(in);
    }

    @Override
    public boolean equals(Object o) {
	if (this == o) {
	    return true;
	}
	if (o == null) {
	    return false;
	}
	if (!(o instanceof SubList)) {
	    return false;
	}
	SubList s = (SubList) o;
	return (Ln.equals(collection, s.collection, true));
    }

    @Override
    ArrayList<String> getTypes() {
	return prototype.getTypes();
    }

    @Override
    ArrayList<S> translate() {
	T trans = prototype.translate();
	if (trans != null && prototype.getClass().equals(trans.getClass())) {
	    return (ArrayList<S>) collection;
	}
	ArrayList<S> out = new ArrayList<>(collection.size());
	for (T t : collection) {
	    out.add((S) prototype.translate(t));
	}
	return out;
    }

    @Override
    SubRecord<ArrayList<S>> translate(ArrayList<S> in) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

	/*
	 * SkyBash methods.
	 */
	/**
	 * Merges SubLists with logging capabilities. Does not check to see if list
	 * objects are the same.
	 *
	 * @param no The new SubList to be merged.
	 * @param bo The base SubList, to prevent base data from being re-merged.
	 * @return The modified SubList.
	 */
	@Override
	public SubRecord merge(SubRecord no, SubRecord bo) {
		SubList<S,T> o = this;
		if (!(no == null && bo == null && (no instanceof SubList) && (bo instanceof SubList))) {
			final SubList<S,T> newlist = (SubList<S,T>) no;
			final SubList<S,T> baselist = (SubList<S,T>) bo;
			Merger.merge(o.collection, newlist.collection, baselist.collection, (String)(getTypes().get(0)));
		}
		return o;
	}

	/**
	 * Replaces entries in a SubList, with logging capabilities.
	 *
	 * @param tempListNew The new ArrayList to be replaced for the SubList.
	 * @param tempListBase The base ArrayList, to prevent a base list from
	 * replaced a new one.
	 * @return The modified ArrayList.
	 */
	public ArrayList<T> replace(ArrayList<T> tempListNew, ArrayList<T> tempListBase) {
		ArrayList<T> tempList = this.collection;
		if (!tempList.equals(tempListNew)) {
			if (!tempListBase.equals(tempListNew)) {
				tempList.clear();
				for (int i = 0; i < tempListNew.size(); i++) {
					tempList.add(tempListNew.get(i));
					if (Merger.fullLogging) {
						SPGlobal.log((String)(getTypes().get(0)).toString() + ": ", "Replaced list for " + (String)(getTypes().get(0)).toString() + " from "
								+ Merger.currentRecord + " from Mod " + Merger.currentMod);
					}
				}
			}
		}
		return tempList;
	}

	/**
	 * Replaces entries in a SubList, with logging capabilities.
	 *
	 * @param tempListNew The new SubList to replace the old SubList.
	 * @param tempListBase The base SubList, to prevent a base list from
	 * replaced a new one.
	 * @return The modified SubList.
	 */
	SubList<S,T> replace(SubList<S,T> tempListNew, SubList<S,T> tempListBase) {
		SubList<S,T> tempList = this;
		if (!tempList.equals(tempListNew)) {
			if (!tempListBase.equals(tempListNew)) {
				tempList.clear();
				for (int i = 0; i < tempListNew.size(); i++) {
					tempList.add(tempListNew.get(i));
					if (Merger.fullLogging) {
						SPGlobal.log((String)(getTypes().get(0)) + ": ", "Replaced list for " + (String)(getTypes().get(0)) + " from "
								+ Merger.currentRecord + " from Mod " + Merger.currentMod);
					}
				}
			}
		}
		return tempList;
	}

	/**
	 * A specialized merger for a list of SubFormInts. Used for perks to
	 * preserve the initial integer.
	 *
	 * @param tempListNew The new SubList<SubFormInt> to replace the old
	 * SubList.
	 * @param tempListBase The base SubList<SubFormInt>, to prevent a base list
	 * from replaced a new one.
	 * @return The modified SubList<SubFormInt>.
	 */
	SubList mergeListSFISpecial(SubList tempListNew, SubList tempListBase) {

		SubList tempList = (SubList) this;

		//Integer used to hold the number for synchronization.
		int tempnum = 0;

		SubFormIntStorage first = new SubFormIntStorage(tempList);
		SubFormIntStorage newlist = new SubFormIntStorage(tempListNew);
		SubFormIntStorage baselist = new SubFormIntStorage(tempListBase);

		//Just a method to grab any one of the initial numbers.
		for (int i = 0; i < first.size; i++) {
			try {
				tempnum = first.i.get(i);
				break;
			} catch (NullPointerException e) {
				tempnum = 0;
				break;
			}
		}

		for (int i = 0; i < baselist.size; i++) {
			FormID baseID = baselist.id.get(i);
			if (!newlist.id.contains(baseID) && first.id.contains(baseID)) {
				first.remove(baseID);
				if (Merger.fullLogging) {
					SPGlobal.log((String)(getTypes().get(0)) + ": ", "Removed " + (String)(getTypes().get(0)) + " " + baseID + " to "
							+ Merger.currentRecord + " from Mod " + Merger.currentMod);
				}
			}
		}
		for (int i = 0; i < newlist.size; i++) {
			FormID newID = newlist.id.get(i);
			if (!first.id.contains(newID) && !baselist.id.contains(newID)) {
				first.add(new SubFormInt(prototype.getType(), newID, tempnum));
				if (Merger.fullLogging) {
					SPGlobal.log((String)(getTypes().get(0)) + ": ", "Merged " + (String)(getTypes().get(0)) + " " + newID + " to "
							+ Merger.currentRecord + " from Mod " + Merger.currentMod);
				}
			}
		}

		tempList.clear();
		tempList.addRecordsTo(first.sfi);
		return tempList;
	}

	/**
	 * A specialized merger for a list of SubFormInts. Used for items/factions
	 * to update the integers associated when forms are matching..
	 *
	 * @param tempListNew The new SubList<SubFormInt> to replace the old
	 * SubList.
	 * @param tempListBase The base SubList<SubFormInt>, to prevent a base list
	 * from replaced a new one.
	 * @return The modified SubList<SubFormInt>.
	 */
	SubList mergeList(SubList tempListNew, SubList tempListBase) {

		SubList tempList = (SubList) this;

		SubFormIntStorage first = new SubFormIntStorage(tempList);
		SubFormIntStorage newlist = new SubFormIntStorage(tempListNew);
		SubFormIntStorage baselist = new SubFormIntStorage(tempListBase);

		//Removes any entries that were removed from the original NPC.
		for (int i = 0; i < baselist.size; i++) {
			FormID baseID = baselist.id.get(i);
			if (!newlist.id.contains(baseID) && first.id.contains(baseID)) {
				first.remove(baseID);
				if (Merger.fullLogging) {
					SPGlobal.log((String)(getTypes().get(0)) + ": ", "Removed " + (String)(getTypes().get(0)) + " " + baseID + " to "
							+ Merger.currentRecord + " from Mod " + Merger.currentMod);
				}
			}
		}

		for (int i = 0; i < newlist.size; i++) {
			FormID newID = newlist.id.get(i);
			int newValue = newlist.i.get(i);
			//Checks if the value exists, in which case it updates the count as per below.
			if (first.id.contains(newID)) {
				//If the base list contains it, and the new one is different, or if the base list doesn't contain it
				//  and the new one does, it updates the integer.
				if ((baselist.map.get(newID) != null && newValue != baselist.map.get(newID))
						|| first.map.get(newID).equals(newValue)) {
					first.remove(newID);
					first.add(new SubFormInt(this.prototype.getType(), newID, newValue));
					if (Merger.fullLogging) {
						SPGlobal.log((String)(getTypes().get(0)) + ": ", "Merged integer for + " + (String)(getTypes().get(0)) + " for ID " + newID + " to "
								+ Merger.currentRecord + " from Mod " + Merger.currentMod
								+ " to count " + newValue);
					}
				}
				//If the base list doesn't contain it, and implicitly the value isn't on the first list,
				//  it adds the new value.
			} else if (!baselist.id.contains(newID)) {
				first.add(new SubFormInt(this.prototype.getType(), newID, newValue));
				if (Merger.fullLogging) {
					SPGlobal.log((String)(getTypes().get(0)) + ": ", "Added " + (String)(getTypes().get(0)) + " " + newID + " to "
							+ Merger.currentRecord + " from Mod " + Merger.currentMod
							+ " with integer " + newValue);
				}
				//If the base and new list contain it, and the values are different, it re-adds the value.
			} else if (!baselist.sfi.get(i).equals(newlist.sfi.get(i))) {
				first.add(new SubFormInt(this.prototype.getType(), newID, newValue));
				if (Merger.fullLogging) {
					SPGlobal.log((String)(getTypes().get(0)) + ": ", "Merged integer for " + (String)(getTypes().get(0)) + " for ID " + newID + " to "
							+ Merger.currentRecord + " from Mod " + Merger.currentMod
							+ " to integer " + newValue);
				}
			}
		}

		tempList.clear();
		tempList.addRecordsTo(first.sfi);
		return tempList;
	}

	SubList mergeListLVLO(SubList newSubList, SubList baseSubList, MajorRecord m) {
		SubList subList = (SubList) this;

		/*
		 * Note to self: possibly use maps? Can rework to use specialized maps,
		 * one for each LVLI/LVLN, of the elements and the integer number of
		 * each element. Allows for easy checks of existence and counts.
		 *
		 * Reconsider for another day, or if this proves to NOT work.
		 */

		//Directly accessing the collections to clarify code, rework into specialized
		//classes set up of lists of LVLO's w/ numbers of elements.
		LVLOList list = new LVLOList(subList.collection);
		LVLOList newList = new LVLOList(newSubList.collection);
		LVLOList baseList = new LVLOList(baseSubList.collection);

		LVLOList removedEntries;

		//List of entries that have previously been removed from the base list.
		//This is to prevent removal of multiple entries, and to provide
		//consistency over a series of mergers.
		if (Merger.leveledRecordMap.containsKey(m.getForm())) {
			removedEntries = Merger.leveledRecordMap.get(m.getForm());
		} else {
			removedEntries = new LVLOList();
			Merger.leveledRecordMap.put(m.getForm(), removedEntries);
		}

		for (LeveledEntry entry : newList.list.keySet()) {
			LeveledEntry l = entry;
			//If the base list doesn't contain an entry on the new list, add it.
			if (!baseList.contains(l)) {
				for (int i = 0; i < newList.getCount(l); i++) {
					list.add(l);
				}
			} //If it does contain it, add the difference between them.
			else {
				if (baseList.getCount(l) < newList.getCount(l)) {
					for (int i = 0; i < (newList.getCount(l) - baseList.getCount(l)); i++) {
						list.add(l);
					}
				}
			}
		}
		//Only delete elements off the list w/ the delev tag.
		if (Merger.mTags.Delev) {
			//Iterate through the base list, cross-check with the newer list.
			for (LeveledEntry entry : baseList.list.keySet()) {
				LeveledEntry l = entry;
				//If the new list does not contain this record, it needs entirely removed.
				if (!newList.contains(l)) {
					//If the list of removed entries contains it, it's already been fully or
					//partially removed.
					//Also checks that the base num is greater than the current number of
					//removed entries - if it's not, then more than needed has already been
					//fully removed.
					if (removedEntries.contains(l) && baseList.getCount(l) > removedEntries.getCount(l)) {
						for (int i = 0; i < (baseList.getCount(l) - removedEntries.getCount(l)); i++) {
							list.remove(l);
							removedEntries.add(l);
						}
					} //If it doesn't contain it, then it needs to be listed in the removed entries
					//so it's not removed multiple times (or attempted to do that).
					else {
						for (int i = 0; i < baseList.getCount(l); i++) {
							list.remove(l);
							removedEntries.add(l);
						}
					}
				} //If the new list does contain this record, then it only needs partially removed.
				else {
					//Check that the new list has less than the base (if it has more, there's no need
					//to remove elements from the list!).
					if (newList.getCount(l) < baseList.getCount(l)) {
						//If it has less than the base, check that the difference is greater than
						//the number of already removed entries.
						if (removedEntries.contains(l) && (baseList.getCount(l) - newList.getCount(l)) > removedEntries.getCount(l)) {
							//If the difference is greater, remove the difference and note the additional removal
							//in the removed entries section.
							for (int i = 0; i < ((baseList.getCount(l) - newList.getCount(l)) - removedEntries.getCount(l)); i++) {
								list.remove(l);
								removedEntries.add(l);
							}
						} //If there is no current note of removed entries, just remove the difference
						//and add that difference to the list of removed entries.
						else {
							for (int i = 0; i < (baseList.getCount(l) - newList.getCount(l)); i++) {
								list.remove(l);
								removedEntries.add(l);
							}
						}
					}
				}
			}
		}
		return subList;
	}

	/**
	 * A specialized class that stores information for the above mergers,
	 * involving a sublist of SubFormInts.
	 */
	class SubFormIntStorage {

		ArrayList<SubFormInt> sfi = new ArrayList<>();
		ArrayList<FormID> id = new ArrayList<>();
		ArrayList<Integer> i = new ArrayList<>();
		HashMap<FormID, Integer> map = new HashMap<>();
		int size = 0;

		SubFormIntStorage(SubList list) {
			for (int j = 0; j < list.size(); j++) {
				this.add((SubFormInt)(list.get(j)));
				map.put(((SubFormInt)(list.get(j))).getForm(), ((SubFormInt)(list.get(j))).getNum());
			}

		}

		SubFormIntStorage(SubFormInt sfi) {
			this.sfi.add(sfi);
			this.id.add(sfi.ID);
			this.i.add(sfi.num);
			size = 1;
		}

		private void add(SubFormInt sfi) {
			this.sfi.add(sfi);
			this.id.add(sfi.ID);
			this.i.add(sfi.num);
			size++;
		}

		private void remove(FormID id) {
			int index = this.id.indexOf(id);
			this.sfi.remove(index);
			this.id.remove(index);
			this.i.remove(index);
			size--;
		}
	}    
}
