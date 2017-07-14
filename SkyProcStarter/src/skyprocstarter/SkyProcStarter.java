package skyprocstarter;

import java.awt.Color;
import java.awt.Font;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lev.gui.LSaveFile;
import skyproc.*;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SUM;
import skyproc.gui.SUMGUI;
import skyprocstarter.YourSaveFile.Settings;

/**
 *
 * @author Your Name Here
 */
public class SkyProcStarter implements SUM {

    /*
     * The important functions to change are:
     * - getStandardMenu(), where you set up the GUI
     * - runChangesToPatch(), where you put all the processing code and add records to the output patch.
     */

    /*
     * The types of records you want your patcher to import. Change this to
     * customize the import to what you need.
     */
    GRUP_TYPE[] importRequests = new GRUP_TYPE[]{
	GRUP_TYPE.INGR,
	GRUP_TYPE.WEAP
    };
    public static String myPatchName = "My Patch";
    public static String authorName = "Me";
    public static String version = "1.0";
    public static String welcomeText = "This is the standard starter project for SkyProc.  "
	    + "I hope it helps you get on your way to making an awesome patcher!";
    public static String descriptionToShowInSUM = "A brand new SkyProc patcher. Does lots of stuff.";
    public static Color headerColor = new Color(66, 181, 184);  // Teal
    public static Color settingsColor = new Color(72, 179, 58);  // Green
    public static Font settingsFont = new Font("Serif", Font.BOLD, 15);
    public static SkyProcSave save = new YourSaveFile();

    // Do not write the bulk of your program here
    // Instead, write your patch changes in the "runChangesToPatch" function
    // at the bottom
    public static void main(String[] args) {
	try {
	    SPGlobal.createGlobalLog();
	    SUMGUI.open(new SkyProcStarter(), args);
	} catch (Exception e) {
	    // If a major error happens, print it everywhere and display a message box.
	    System.err.println(e.toString());
	    SPGlobal.logException(e);
	    JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution: '" + e + "'  Check the debug logs or contact the author.");
	    SPGlobal.closeDebug();
	}
    }

    @Override
    public String getName() {
	return myPatchName;
    }

    // This function labels any record types that you "multiply".
    // For example, if you took all the armors in a mod list and made 3 copies,
    // you would put ARMO here.
    // This is to help monitor/prevent issues where multiple SkyProc patchers
    // multiply the same record type to yeild a huge number of records.
    @Override
    public GRUP_TYPE[] dangerousRecordReport() {
	// None
	return new GRUP_TYPE[0];
    }

    @Override
    public GRUP_TYPE[] importRequests() {
	return importRequests;
    }

    @Override
    public boolean importAtStart() {
	return false;
    }

    @Override
    public boolean hasStandardMenu() {
	return true;
    }

    // This is where you add panels to the main menu.
    // First create custom panel classes (as shown by YourFirstSettingsPanel),
    // Then add them here.
    @Override
    public SPMainMenuPanel getStandardMenu() {
	SPMainMenuPanel settingsMenu = new SPMainMenuPanel(getHeaderColor());

	settingsMenu.setWelcomePanel(new WelcomePanel(settingsMenu));
	settingsMenu.addMenu(new OtherSettingsPanel(settingsMenu), false, save, Settings.OTHER_SETTINGS);

	return settingsMenu;
    }

    // Usually false unless you want to make your own GUI
    @Override
    public boolean hasCustomMenu() {
	return false;
    }

    @Override
    public JFrame openCustomMenu() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasLogo() {
	return false;
    }

    @Override
    public URL getLogo() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasSave() {
	return true;
    }

    @Override
    public LSaveFile getSave() {
	return save;
    }

    @Override
    public String getVersion() {
	return version;
    }

    @Override
    public ModListing getListing() {
	return new ModListing(getName(), false);
    }

    @Override
    public Mod getExportPatch() {
	Mod out = new Mod(getListing());
	out.setAuthor(authorName);
	return out;
    }

    @Override
    public Color getHeaderColor() {
	return headerColor;
    }

    // Add any custom checks to determine if a patch is needed.
    // On Automatic Variants, this function would check if any new packages were
    // added or removed.
    @Override
    public boolean needsPatching() {
	return false;
    }

    // This function runs when the program opens to "set things up"
    // It runs right after the save file is loaded, and before the GUI is displayed
    @Override
    public void onStart() throws Exception {
    }

    // This function runs right as the program is about to close.
    @Override
    public void onExit(boolean patchWasGenerated) throws Exception {
    }

    // Add any mods that you REQUIRE to be present in order to patch.
    @Override
    public ArrayList<ModListing> requiredMods() {
	return new ArrayList<>(0);
    }

    @Override
    public String description() {
	return descriptionToShowInSUM;
    }

    // This is where you should write the bulk of your code.
    // Write the changes you would like to make to the patch,
    // but DO NOT export it.  Exporting is handled internally.
    @Override
    public void runChangesToPatch() throws Exception {

	Mod patch = SPGlobal.getGlobalPatch();

	Mod merger = new Mod(getName() + "Merger", false);
	merger.addAsOverrides(SPGlobal.getDB());

	// Write your changes to the patch here.
    }

}
