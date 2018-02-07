/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * @author Justin Swanson
 */
class SubListSorted<S extends SubRecord<T>, T> extends SubList<S, T> {

    TreeSet<T> sorter = new TreeSet<>();

    SubListSorted(S prototype_) {
        super(prototype_);
    }

    SubListSorted(SubListSorted rhs) {
	super(rhs);
	sorter.addAll(rhs.sorter);
    }

    @Override
    SubRecord getNew(String type) {
	return new SubListSorted(this);
    }

    @Override
    public T get(int i) {
        return (T) sorter.toArray()[i];
    }

    @Override
    public boolean add(T item) {
        sorter.add(item);
	return super.add(item);
    }

    @Override
    public boolean remove(T item) {
        sorter.remove(item);
        return super.remove(item);
    }

    @Override
    public void remove(int i) {
        sorter.remove(collection.get(i));
        super.remove(i);
    }

    @Override
    public void clear() {
        super.clear();
        sorter.clear();
    }

    @Override
    public void setRecordsTo(ArrayList<T> in) {
        super.setRecordsTo(in);
        sorter.clear();
        sorter.addAll(in);
    }

    @Override
    ArrayList<S> translate() {
	ArrayList<S> out = new ArrayList<>(sorter.size());
	for (T t : sorter) {
	    out.add((S) prototype.translate(t));
	}
	return out;
    }

    public Iterator<S> unsortedIterator() {
	return super.translate().iterator();
    }

    /*
     * SkyBash methods.
     */
    /**
     * Merges SubSortedLists with logging capabilities. Does not check what
     * types of objects are in the list, so merge appropriately.
     *
     * @param no The new SubSortedList to be merged.
     * @param bo The base SubSortedList, to prevent base data from being
     * re-merged.
     * @return The modified SubSortedList.
     */
    @Override
    public SubRecord merge(SubRecord no, SubRecord bo) {
        SubListSorted<S, T> o = this;
        if (!(no == null && bo == null && (no instanceof SubListSorted) && (bo instanceof SubListSorted))) {
            final SubListSorted<S, T> newlist = (SubListSorted<S, T>) no;
            final SubListSorted<S, T> baselist = (SubListSorted<S, T>) bo;
            o.sorter = this.merge(newlist.sorter, baselist.sorter);
        }
        return o;
    }

    /**
     * Merges TreeSets with logging capabilities, from SubSortedLists.
     *
     * @param tempListNew The new TreeSet to be merged.
     * @param tempListBase The base TreeSet, to prevent base data from being
     * re-merged.
     * @return The modified TreeSet.
     */
    TreeSet<T> merge(TreeSet<T> tempListNew, TreeSet<T> tempListBase) {
        TreeSet<T> tempList = this.sorter;
        if (!tempList.equals(tempListNew) && !tempListBase.equals(tempListNew)) {
            Iterator it = tempListBase.iterator();
            while (it.hasNext()) {
                T t = (T) it.next();
                if ((!tempListNew.contains(t) && tempList.contains(t))) {
                    tempList.remove(t);
                    if (Merger.fullLogging) {
                        SPGlobal.log(getType() + ": ", "Removed " + getType() + " " + t + " to "
                                + Merger.currentRecord + " from Mod " + Merger.currentMod);
                    }
                }
            }
            it = tempListNew.iterator();
            while (it.hasNext()) {
                T t = (T) it.next();
                if (!tempList.contains(t) && !tempListBase.contains(t)) {
                    tempList.add(t);
                    if (Merger.fullLogging) {
                        SPGlobal.log(getType() + ": ", "Merged " + getType() + " " + t + " to "
                                + Merger.currentRecord + " from Mod " + Merger.currentMod);
                    }
                }
            }
        }
        return tempList;
    }
}
