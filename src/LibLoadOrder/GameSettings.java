package LibLoadOrder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameSettings {
    private int id;
    private int loMethod;
    String masterFile;
    String appdataFolderName;
    String pluginsFolderName;
    String pluginsFileName;
    String gamePath;
    String pluginsPath;
    String loadorderPath;

    public GameSettings(int id, String gamePath, String localPath) {
        this.id = id;
        this.gamePath = gamePath;

        if(id == Constants.LIBLO_GAME_TES3) {
            loMethod = Constants.LIBLO_METHOD_TIMESTAMP;
            masterFile = "Morrowind.esm";

            appdataFolderName = "";
            pluginsFolderName = "Data Files";
            pluginsFileName = "Morrowind.ini";
        }
        else if(id == Constants.LIBLO_GAME_TES4) {
            loMethod = Constants.LIBLO_METHOD_TIMESTAMP;
            masterFile = "Oblivion.esm";

            appdataFolderName = "Oblivion";
            pluginsFolderName = "Data";
            pluginsFileName = "plugins.txt";
        }
        else if(id == Constants.LIBLO_GAME_TES5) {
            loMethod = Constants.LIBLO_METHOD_TEXTFILE;
            masterFile = "Skyrim.esm";

            appdataFolderName = "Skyrim";
            pluginsFolderName = "Data";
            pluginsFileName = "plugins.txt";
        }
        else if(id == Constants.LIBLO_GAME_TES5SE) {
            loMethod = Constants.LIBLO_METHOD_ASTERISK;
            masterFile = "Skyrim.esm";

            appdataFolderName = "Skyrim Special Edition";
            pluginsFolderName = "Data";
            pluginsFileName = "plugins.txt";
        }
        else if(id == Constants.LIBLO_GAME_FO3) {
            loMethod = Constants.LIBLO_METHOD_TIMESTAMP;
            masterFile = "Fallout3.esm";

            appdataFolderName = "Fallout3";
            pluginsFolderName = "Data";
            pluginsFileName = "plugins.txt";
        }
        else if(id == Constants.LIBLO_GAME_FNV) {
            loMethod = Constants.LIBLO_METHOD_TIMESTAMP;
            masterFile = "FalloutNV.esm";

            appdataFolderName = "FalloutNV";
            pluginsFolderName = "Data";
            pluginsFileName = "plugins.txt";
        }
        else if(id == Constants.LIBLO_GAME_FO4) {
            loMethod = Constants.LIBLO_METHOD_ASTERISK;
            masterFile = "Fallout4.esm";

            appdataFolderName = "Fallout4";
            pluginsFolderName = "Data";
            pluginsFileName = "plugins.txt";
        }

        if (localPath.isEmpty())
        {
            initPaths(new File(getLocalAppDataPath(), appdataFolderName).getPath());
        }
        else
        {
            initPaths(localPath);
        }
    }

    public GameSettings(int id, String gamePath) {
        this(id, gamePath, "");
    }

    public int getId() {
        return id;
    }

    public int getLibespmId() {
        if(id == Constants.LIBLO_GAME_TES3) {
            return LibESPM.Constants.GAME_ID_MORROWIND;
        } else if(id == Constants.LIBLO_GAME_TES4) {
            return LibESPM.Constants.GAME_ID_OBLIVION;
        } else {
            return LibESPM.Constants.GAME_ID_SKYRIM;
        }
    }

    public String getMasterFile() {
        return masterFile;
    }

    public int getLoadOrderMethod() {
        return loMethod;
    }

    public List<String> getImplicitlyActivePlugins() {
        if(id == Constants.LIBLO_GAME_TES5) {
            return Arrays.asList(masterFile, "Update.esm");
        }
        else if (id == Constants.LIBLO_GAME_FO4) {
            return Arrays.asList(masterFile, "DLCRobot.esm","DLCworkshop01.esm","DLCCoast.esm","DLCworkshop02.esm","DLCworkshop03.esm","DLCNukaWorld.esm");
        }
        else if (id == Constants.LIBLO_GAME_TES5SE) {
            return Arrays.asList(masterFile, "Update.esm","Dawnguard.esm","Hearthfires.esm","Dragonborn.esm");
        }
        else {
            return new ArrayList<>();
        }
    }

    public Boolean isImplicitlyActive(String pluginName) {
        List<String> implicitlyActivePlugins = getImplicitlyActivePlugins();
        return implicitlyActivePlugins.stream().filter(s -> s.equalsIgnoreCase(pluginName)).findFirst().isPresent();
    }

    public String getPluginsFolder() {
        return new File(gamePath, pluginsFolderName).getPath();
    }

    public String getActivePluginsFile() {
        return pluginsPath;
    }

    public String getLoadOrderFile() {
        return loadorderPath;
    }

    private void initPaths(String localPath) {
        /*if(id == Constants.LIBLO_GAME_TES4 && new File(gamePath, "Oblivion.ini").exists()) {
            String iniContent = new File();
        }*/
        if (id == Constants.LIBLO_GAME_TES3) {
            pluginsPath = new File(gamePath, pluginsFileName).getPath();
        }
        else {
            pluginsPath = new File(localPath , pluginsFileName).getPath();
            loadorderPath = new File(localPath , "loadorder.txt").getPath();
        }
    }

    private String getLocalAppDataPath() {
        return System.getenv("LOCALAPPDATA");
    }
}
