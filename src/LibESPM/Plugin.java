package LibESPM;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Plugin {
    private int gameId;
    private String name;
    private Record headerRecord;
    private Set<FormId> formIds;

    public Plugin(int gameId) {
        gameId = gameId;
    }

    public void load(String filepath, Boolean loadHeaderOnly) {
        name = new File(filepath).getName();

    }

    public void load(String filepath) {
        load(filepath, false);
    }

    public String getName() {
        return name;
    }

    public Boolean isMasterFile() {
        return false;
    }

    public static Boolean isValid(String filepath, int gameId, Boolean loadHeaderOnly ) {
        Plugin p = new Plugin(gameId);
        try {
            p.load(filepath);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static Boolean isValid(String filepath, int gameId) {
        return isValid(filepath, gameId, false);
    }

    public List<String> getMasters() {
        return new ArrayList<>();
    }

    public String getDescription() {
        return "";
    }

    public FormId[] getFormIds() {
        return formIds.toArray(new FormId[formIds.size()]);
    }

    public int getRecordAndGroupCount() {
        return 0;
    }

    private static String trimToEspm(String filename) {
        return filename;
    }
}
