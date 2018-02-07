/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skybash;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import lev.gui.LSaveFile;
import org.ini4j.Config;
import org.ini4j.Ini;
import skyproc.*;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SPProgressBarPlug;
import skyproc.gui.SUM;
import skyproc.gui.SUMGUI;


/**
 *
 * @author pc tech
 */
public class SkyBash implements SUM {

    public static String SkyBashlanguage;
    static Ini skyBashIni;
    public static LSaveFile save = new SkyBashSaveFile();
    public static final String version = "0.0.1";
    static public SPMainMenuPanel settingsMenu;
    static public Color blue = new Color(0, 147, 196);

    Map<JCheckBox, Boolean> guiButtons = new HashMap<>();
    Map settingsMap;
    Map<String, String> modTagsMap;
    //Stores a map of a mods tags with the mod.  Passed to merger classes via public.
    HashMap<Mod, ModTags> mods = new HashMap<>();
    static ModTags mTags;
    //Number of progress steps.  numSteps is overridden with the number of mods to parse.
    static int numSteps = 5;
    static int steps = 0;
    //Storage mod used in counter.
    Mod temp;
    //Lists forms w/ NPC's.
    HashMap<FormID, MajorRecord> list = new HashMap<>();
    //Lists all the base NPC's from the Skyrim.esm, so base data isn't considered changed at any point.
    HashMap<FormID, MajorRecord> baseList = new HashMap<>();

    public static void main(String[] args) throws Exception {
        try {
            SPGlobal.createGlobalLog();
            SUMGUI.open(new SkyBash(), args);
        } catch (Exception e) {
            // If a major error happens, print it everywhere and display a message box.
            System.err.println(e.toString());
            SPGlobal.logException(e);
            JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution: '" + e + "'  Check the debug logs or contact the author.");
            SPGlobal.closeDebug();
        }
    }

    @Override
    public String description() {
        return "The patcher for SkyBash.";
    }
    @Override
    public Mod getExportPatch() {
        Mod patch = new Mod(getListing());
        patch.setFlag(Mod.Mod_Flags.STRING_TABLED, false);
        patch.setAuthor("Leviathan1753");
        return patch;
    }
    @Override
    public boolean needsPatching() {
        return true;
    }
    @Override
    public ModListing getListing() {
        return new ModListing("SkyBash", false);
    }
    @Override
    public boolean importAtStart() {
        return false;
    }
    @Override
    public void onStart() throws Exception {
        processINI();
        SPGlobal.Language lang;
        try {
            lang = SPGlobal.Language.valueOf(SkyBashlanguage);
        } catch (EnumConstantNotPresentException e) {
            lang = SPGlobal.Language.English;
        }
        SPGlobal.language = lang;
    }
    private void processINI() throws IOException {
        SPProgressBarPlug.setStatus("SkyBash: Processing INI");
        SPProgressBarPlug.incrementBar();
        //Sets up the file reader for the ini file.
        skyBashIni = new Ini();
        Config c = skyBashIni.getConfig();
        c.setEmptyOption(true);
        c.setEmptySection(true);
        c.setEscape(false);
        skyBashIni.load(new File("SkyBash.ini"));

        Ini.Section lang = skyBashIni.get("LANGUAGE");
        try {
            Set<String> langKeys = lang.keySet();
            SkyBashlanguage = langKeys.iterator().next();
        } catch (Exception e) {
            SkyBashlanguage = "English";
        }

        Ini.Section settings = skyBashIni.get("Settings");
        try {
            Merger.fullLogging = Boolean.parseBoolean(settings.get("full logging"));
        } catch (Exception e) {
            Merger.fullLogging = false;
        }
    }
    @Override
    public LSaveFile getSave() {
        return save;
    }
    @Override
    public URL getLogo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public JFrame openCustomMenu() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public GRUP_TYPE[] dangerousRecordReport() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public String getVersion() {
        return version;
    }
    @Override
    public GRUP_TYPE[] importRequests() {
        return new GRUP_TYPE[]{
                GRUP_TYPE.NPC_,
                GRUP_TYPE.INGR,
                GRUP_TYPE.MGEF,
                GRUP_TYPE.FACT,
                GRUP_TYPE.ALCH,
                GRUP_TYPE.AMMO,
                GRUP_TYPE.ARMO,
                GRUP_TYPE.ARMA,
                GRUP_TYPE.COBJ,
                GRUP_TYPE.ENCH,
                GRUP_TYPE.FLST,
                GRUP_TYPE.PERK,
                GRUP_TYPE.RACE,
                GRUP_TYPE.SPEL,
                GRUP_TYPE.LVLI,
                GRUP_TYPE.LVLN
        };
    }
    @Override
    public String getName() {
        return "SkyBash";
    }
    @Override
    public void onExit(boolean patchWasGenerated) throws Exception {
        /*if ( (finishedSound != null) && !finishedSound.isEmpty()){
            try {
                File soundFile = new File(finishedSound);
                playClip(soundFile);
            } catch (IOException | InterruptedException | LineUnavailableException | UnsupportedAudioFileException ex){
                SPGlobal.log("OnExit", "Could not play sound: " + ex);
            }
        }*/
        SPGlobal.logMain("EXIT", "Closing SkyBash.");
    }
    @Override
    public boolean hasSave() {
        return true;
    }
    @Override
    public ArrayList<ModListing> requiredMods() {
        return new ArrayList<>(/*Arrays.asList(new ModListing("ASIS-Dependency.esp"))*/);
    }
    @Override
    public boolean hasCustomMenu() {
        return false;
    }
    @Override
    public boolean hasLogo() {
        return false;
    }
    @Override
    public SPMainMenuPanel getStandardMenu() {
        settingsMenu = new SPMainMenuPanel(blue);
        settingsMenu.setVersion(version, new Point(80, 88));
        SUMGUI.helpPanel.setHeaderFont(new Font("Serif", Font.BOLD, 18));

        /*settingsIncreasedSpawns = new SettingsIncreasedSpawns(settingsMenu);
        settingsMenu.addMenu(settingsIncreasedSpawns, true, save, GUISettings.INCREASEDSPAWNS_ON);

        settingsAutomaticPerks = new SettingsAutomaticPerks(settingsMenu);
        settingsMenu.addMenu(settingsAutomaticPerks, true, save, GUISettings.AUTOMATICPERKS_ON);

        settingsAutomaticSpells = new SettingsAutomaticSpells(settingsMenu);
        settingsMenu.addMenu(settingsAutomaticSpells, true, save, GUISettings.AUTOMATICSPELLS_ON);

        settingsCustomizedAI = new SettingsCustomizedAI(settingsMenu);
        settingsMenu.addMenu(settingsCustomizedAI, true, save, GUISettings.CUSTOMIZEDAI_ON);

        settingsCustomizedGMSTs = new SettingsCustomizedGMSTs(settingsMenu);
        settingsMenu.addMenu(settingsCustomizedGMSTs, true, save, GUISettings.CUSTOMIZEDGMSTS_ON);

        settingsNPCPotions = new SettingsNPCPotions(settingsMenu);
        settingsMenu.addMenu(settingsNPCPotions, true, save, GUISettings.NPCPOTIONS_ON);

        settingsSpawnRandomizer = new SettingsSpawnRandomizer(settingsMenu);
        settingsMenu.addMenu(settingsSpawnRandomizer, true, save, GUISettings.SPAWNRANDOMIZER_ON);

        settingsNPCEnchantFix = new SettingsNPCEnchantFix(settingsMenu);
        settingsMenu.addMenu(settingsNPCEnchantFix, true, save, GUISettings.NPCENCHANTFIX_ON);*/

//        otherSettingsPanel = new OtherSettingsPanel(settingsMenu);
//        settingsMenu.addMenu(otherSettingsPanel, false, save, GUISettings.OTHER_SETTINGS);
        return settingsMenu;
    }
    @Override
    public boolean hasStandardMenu() {
        return true;
    }
    @Override
    public Color getHeaderColor() {
        return blue;
    }
    @Override
    public void runChangesToPatch() throws Exception {
        SPGlobal.loggingSync(true);
        SPGlobal.logging(true);
//        SPGlobal.getGlobalPatch().setFlag(Mod.Mod_Flags.STRING_TABLED, false);

        SPProgressBarPlug.setMax(numSteps);
        SPProgressBarPlug.setStatus("Initializing ASIS");

        SkyBashMerger();
    }

    public void SkyBashMerger(/*boolean fullLogging*/) {

        MajorRecord m;
        MajorRecord newM;
        MajorRecord baseM;

        //Set full logging.
        //Merger.fullLogging = fullLogging;

        //Progress bar update.
        SPProgressBarPlug.setStatus("Processing Base Records");
        SPProgressBarPlug.incrementBar();

        //Iterate through mod lists and grab base records.
        for (GRUP g : SPGlobal.getDB().getMod(new ModListing("Skyrim", true)).getGRUPs().values()) {
            for (Object o : g.getRecords()) {
                m = (MajorRecord) o;
                baseList.put(m.getForm(), m);
            }
        }
        for (GRUP g : SPGlobal.getDB().getMod(new ModListing("Update", true)).getGRUPs().values()) {
            for (Object o : g.getRecords()) {
                m = (MajorRecord) o;
                baseList.put(m.getForm(), m);
            }
        }

        //Iterate through the mods, starting after Skyrim/Update masters.
        for (int i = SPGlobal.getDB().modIndex(new ModListing("Skyrim", true)) + 2; i < SPGlobal.getDB().numMods(); i++) {

            //Grab the mod being used.
            temp = SPGlobal.getDB().getMod(i);
            Merger.currentMod = temp.getNameNoSuffix();

            //Update progress bar with the mod it is processing.
            SPProgressBarPlug.setStatus("Processing Records from " + temp.getName());
            SPProgressBarPlug.incrementBar();

            //Get the mod tags for the merger.
            if (mods != null && !mods.isEmpty() && mods.containsKey(temp)) {
                mTags = mods.get(temp);
            } else {
                mTags = new ModTags(true);
            }

            //If the tags say to deactivate, it skips that mod.
            if (!mTags.Deactivate) {
                //Iterates through the GRUP's for each mod.
                for (GRUP g : temp.getGRUPs().values()) {
                    //Iterates through the records for each mod.
                    for (Object o : g.getRecords()) {
                        //Casts to a MajorRecord as listRecords returns Objects.  Silly java.
                        m = (MajorRecord) o;
                        //If it's the first instance of the NPC, adds to list and leaves it alone.
                        // Base NPC's are assumed overwritten, so not counted here.
                        if (!list.containsKey(m.getForm())) {
                            list.put(m.getForm(), m);
                        } else {
                            //Grab the record off the list if it's already there, so the new ones can be merged.
                            newM = list.get(m.getForm());

                            if (baseList.containsKey(m.getForm())) {
                                baseM = baseList.get(m.getForm());
                            } else {
                                baseM = m;
                            }
                            SPGlobal.log(g.toString() + " MERGER: ", "Merging " + m.getEDID() + " from " + temp.getName());
                            Merger.currentRecord = m.getEDID();
                            m.merge(newM, baseM);
                            SPGlobal.getGlobalPatch().addRecord(m);
                        }
                    }
                }
            }
        }
    }
}
