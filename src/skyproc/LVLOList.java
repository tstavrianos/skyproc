/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author pc tech
 */
public class LVLOList {

    HashMap<LeveledEntry, LVLOCount> list = new HashMap<>();
    ArrayList<LeveledEntry> listLVLO = new ArrayList<>();

    LVLOList() {
    }

    LVLOList(ArrayList<LeveledEntry> in) {
        for (int i = 0; i < in.size(); i++) {
            if (!listLVLO.contains(in.get(i))) {
                listLVLO.add(in.get(i));
                list.put(in.get(i), new LVLOCount(in.get(i)));
            } else {
                list.get(in.get(i)).add();
            }
        }
    }

    public int getCount(LeveledEntry in) {
        return list.get(in).count;
    }

    public void setCount(LeveledEntry in, int num) {
        list.get(in).count = num;
    }

    public boolean contains(LeveledEntry in) {
        return list.containsKey(in);
    }

    public LVLOCount get(LeveledEntry in) {
        return list.get(in);
    }

    public void put(LeveledEntry in, LVLOCount lc, int count) {
        list.put(in, lc);
        lc.count = count;
    }

    public void add(LeveledEntry in) {
        if (list.containsKey(in)) {
            list.get(in).add();
        } else {
            list.put(in, new LVLOCount(in));
        }
    }

    public void remove(LeveledEntry in) {
        if (list.containsKey(in)) {
            list.get(in).remove();
        }
    }

    public void modify(LeveledEntry in, int num) {
        list.get(in).modify(num);
    }
}
