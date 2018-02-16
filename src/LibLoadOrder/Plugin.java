package LibLoadOrder;

import java.io.File;

public class Plugin extends LibESPM.Plugin {


    private Boolean active;
    private long modTime;

    public Plugin(String filename, GameSettings gameSettings) {
        super(gameSettings.getLibespmId());
        active = false;
        modTime = 0;
        String filePath = new File(gameSettings.getPluginsFolder(), filename).getPath();
        String filePathGhost = new File(gameSettings.getPluginsFolder(), filename + ".ghost").getPath();
        if (new File(filePath).exists() == false && new File(filePathGhost).exists() == true) {
            filePath = filePathGhost;
        }

        modTime = new File(filePath).lastModified();

        load(filePath, true);
    }

    public String getName() {
        return trimGhostExtension(super.getName());
    }

    public long getModTime() {
        return modTime;
    }

    public Boolean isActive() {
        return active;
    }

    public Boolean hasFileChanged(String pluginsFolder) {
        return modTime != new File(pluginsFolder, super.getName()).lastModified();
    }

    public void setModTime(long modificationTime, String pluginsFolder) {
        new File(pluginsFolder, super.getName()).setLastModified(modificationTime);
        modTime = modificationTime;
    }

    public void activate(String pluginsFolder) {
        if(active) return;

        //if ghost

        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public static Boolean isValid(String filename, GameSettings gameSettings) {
        String name = trimGhostExtension(filename);

        return (name.toLowerCase().endsWith(".esp") || name.toLowerCase().endsWith(".esm"))
                && (LibESPM.Plugin.isValid(new File(gameSettings.getPluginsFolder(), name).getPath(), gameSettings.getLibespmId(), true) ||
                LibESPM.Plugin.isValid(new File(gameSettings.getPluginsFolder(), name + ".ghost").getPath(), gameSettings.getLibespmId(), true));
    }

    private static String trimGhostExtension(String filename) {
        if(filename.toLowerCase().endsWith(".ghost")) {
            return filename.substring(0, filename.length() - 6 - 1);
        }
        return filename;
    }
}
