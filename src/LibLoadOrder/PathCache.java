package LibLoadOrder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PathCache {
    private Map<String, Long> modificationTimes;

    public PathCache() {
        modificationTimes = new HashMap<>();
    }

    public Boolean isModified(String file) {
        File f = new File(file);
        if (f.exists() == false)
        {
            return false;
        }

        if(modificationTimes.containsKey(file))
        {
            return f.lastModified() != modificationTimes.get(file).longValue();
        }

        return true;
    }

    public void updateCachedState(String file) {
        File f = new File(file);
        if (f.exists() == false)
        {
            return;
        }

        if(modificationTimes.containsKey(file)) {
            modificationTimes.remove(file);
        }

        modificationTimes.put(file, new Long(f.lastModified()));
    }

    public void clear()
    {
        modificationTimes.clear();
    }
}
