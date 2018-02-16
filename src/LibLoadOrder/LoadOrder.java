package LibLoadOrder;

import java.util.List;

//https://github.com/WrinklyNinja/libloadorder/blob/e73f98b8acc255d2318e28ab72684dfcb74e6015/src/backend/LoadOrder.cpp
public class LoadOrder {
    public static final int maxActivePlugins = 255;
    private PathCache pathCache;
    private List<Plugin> loadOrder;
    private GameSettings gameSettings;

    public LoadOrder(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    public void load() {
        for (int i = 0; i < loadOrder.size();) {
            Plugin lo = loadOrder.get(i);
            if (lo.hasFileChanged(gameSettings.getPluginsFolder())) {
                loadOrder.set(i, new Plugin(lo.getName(), gameSettings));
            }
            ++i;
        }
    }

    public void save() {

    }

    public List<String> getLoadOrder() {

    }

    public int getPosition(String pluginName) {

    }

    public String getPluginAtPosition(int index) {

    }

    public void setLoadOrder(List<String> pluginNames) {

    }

    public void setPosition(String pluginName, int loadOrderIndex) {

    }

    public List<String> getActivePlugins() {

    }

    public Boolean isActive(String pluginName) {

    }

    public void setActivePlugins(List<String> pluginNames) {

    }

    public void activate(String pluginName) {

    }

    public void deactivate(String pluginName) {

    }

    public static Boolean isSynchronised(GameSettings gameSettings) {

    }

    public void clear() {

    }

    private void loadFromFile(String file) {

    }

    private void loadActivePlugins() {

    }

    private void addMissingPlugins() {

    }

    private void addImplicitlyActivePlugins() {

    }

    private void deactivateExcessPlugins() {

    }

    private void saveTimestampLoadOrder() {

    }

    private void saveTextfileLoadOrder() {

    }

    private void saveActivePlugins() {

    }

    private int getMasterPartitionPoint() {

    }

    private int countActivePlugins() {

    }

    private Plugin getPluginObject(String pluginName) {

    }

    private int getAppendPosition(String plugin) {

    }

    private Boolean contains(String pluginName) {

    }


    private int addToLoadOrder(String pluginName) {

    }
}
