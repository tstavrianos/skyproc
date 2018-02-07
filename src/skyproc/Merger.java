package skyproc;

import lev.LFlags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Merger {
    public static boolean fullLogging;
    public static String currentRecord;
    public static String currentMod;
    public static HashMap<FormID, LVLOList> leveledRecordMap = new HashMap<>();
    public static ModTags mTags;

    public static float merge(float f, float newfloat, float basefloat, String type, String s) {
        if (f != newfloat && newfloat != basefloat) {
            f = newfloat;
            if (Merger.fullLogging) {
                SPGlobal.log(type + ": ", "Merged " + s + " to " + f + " for "
                        + Merger.currentRecord + " from Mod " + Merger.currentMod);
            }
        }
        return f;
    }

    public static int merge(int integer, int newint, int baseint, String type, String s) {
        if (integer != newint && newint != baseint) {
            integer = newint;
            if (Merger.fullLogging) {
                SPGlobal.log(type + ": ", "Merged " + s + " to " + integer + " for "
                        + Merger.currentRecord + " from Mod " + Merger.currentMod);
            }
        }
        return integer;
    }

    public static byte[] merge(byte[] d, byte[] newd, byte[] based, String type, String s) {
        if (!Arrays.equals(d, newd) && !Arrays.equals(newd, based)) {
            d = newd;
            if (Merger.fullLogging) {
                SPGlobal.log(type + ": ", "Merged " + s + " to " + d + " for "
                        + Merger.currentRecord + " from Mod " + Merger.currentMod);
            }
        }
        return d;
    }

    public static Object merge(Object o, Object no, Object bo, String type, String s) {
        if (!o.equals(no) && !no.equals(bo)) {
            o = no;
            if (Merger.fullLogging) {
                SPGlobal.log(type + ": ", "Merged " + s + " to " + o + " for "
                        + Merger.currentRecord + " from Mod " + Merger.currentMod);
            }
        }
        return o;
    }

    public static Enum merge(Enum e, Enum ne, Enum be, String type, String s) {
        if (!e.equals(ne) && !ne.equals(be)) {
            e = ne;
            if (Merger.fullLogging) {
                SPGlobal.log(type + ": ", "Merged " + s + " to " + e + " for "
                        + Merger.currentRecord + " from Mod " + Merger.currentMod);
            }
        }
        return e;
    }

    public static boolean merge(boolean b, boolean nb, boolean bb, String type, String s) {
        if (b != nb && !nb != bb) {
            b = nb;
            if (Merger.fullLogging) {
                SPGlobal.log(type + ": ", "Merged " + s + " to " + b + " for "
                        + Merger.currentRecord + " from Mod " + Merger.currentMod);
            }
        }
        return b;
    }

    public static String merge(String so, String ns, String bs, String type, String s) {
        if (!so.equals(ns) && !ns.equals(bs)) {
            so = ns;
            if (Merger.fullLogging) {
                SPGlobal.log(type + ": ", "Merged " + type.toString() + " to " + s + " for "
                        + Merger.currentRecord + " from Mod " + Merger.currentMod);
            }
        }
        return so;
    }

    /**
     * Merges ArrayLists with logging capabilities, from SubLists.
     *
     * @param tempListNew The new ArrayList to be merged.
     * @param tempListBase The base ArrayList, to prevent base data from being
     * re-merged.
     * @return The modified ArrayList.
     */
    public static <T extends Object> ArrayList<T> merge(ArrayList<T> tempList, ArrayList<T> tempListNew, ArrayList<T> tempListBase, String type) {
        if (!tempList.equals(tempListNew) && !tempListBase.equals(tempListNew)) {
            for (int i = 0; i < tempListBase.size(); i++) {
                T t = tempListBase.get(i);
                if ((!tempListNew.contains(t) && tempList.contains(t))) {
                    tempList.remove(t);
                    if (Merger.fullLogging) {
                        SPGlobal.log(type + ": ", "Removed " + type + " " + t + " to "
                                + Merger.currentRecord + " from Mod " + Merger.currentMod);
                    }
                }
            }
            for (int i = 0; i < tempListNew.size(); i++) {
                T t = tempListNew.get(i);
                if (!tempList.contains(t) && !tempListBase.contains(t)) {
                    tempList.add(t);
                    if (Merger.fullLogging) {
                        SPGlobal.log(type + ": ", "Merged " + type + " " + t + " to "
                                + Merger.currentRecord + " from Mod " + Merger.currentMod);
                    }
                }
            }
        }
        return tempList;
    }

    public static LFlags merge(LFlags lf, LFlags newf, LFlags basef, String type) {
        if (!lf.equals(newf) && !newf.equals(basef)) {
            lf = newf;
            if (Merger.fullLogging) {
                SPGlobal.log(type + ": ", "Merged " + type + " to " + lf.toString() + " for "
                        + Merger.currentRecord + " from Mod " + Merger.currentMod);
            }
        }
        return lf;
    }

    public static LFlags merge(LFlags lf, LFlags newf, LFlags basef) {
        if (!lf.equals(newf) && !newf.equals(basef)) {
            lf = newf;
        }
        return lf;
    }

    public static void logMerge(String type, String o) {
        SPGlobal.log(type + ": ", "Merged " + type + " to " + o + " for "
                + Merger.currentRecord + " from Mod " + Merger.currentMod);
    }

    public static void logMerge(String customHeader, String type, String o) {
        SPGlobal.log(customHeader + ": ", "Merged " + type + " for "
                + Merger.currentRecord + " from Mod " + Merger.currentMod);
    }

    public static void logMerge(String type, SubFormData sfi) {
        SPGlobal.log(type + ": ", "Merged data for " + type + " for ID " + sfi.ID + " to "
                + Merger.currentRecord + " from Mod " + Merger.currentMod);
    }
}

