/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyprocstarter;

import lev.gui.LCheckBox;
import lev.gui.LComboBox;
import skyproc.SPGlobal;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SPSettingPanel;
import skyproc.gui.SUMGUI;

/**
 *
 * @author Justin Swanson
 */
public class OtherSettingsPanel extends SPSettingPanel {

    LCheckBox importOnStartup;

    public OtherSettingsPanel(SPMainMenuPanel parent_) {
	super(parent_, "Other Settings", SkyProcStarter.headerColor);
    }

    @Override
    protected void initialize() {
	super.initialize();

	importOnStartup = new LCheckBox("Import Mods on Startup", SkyProcStarter.settingsFont, SkyProcStarter.settingsColor);
	importOnStartup.tie(YourSaveFile.Settings.IMPORT_AT_START, SkyProcStarter.save, SUMGUI.helpPanel, true);
	importOnStartup.setOffset(2);
	importOnStartup.addShadow();
	setPlacement(importOnStartup);
	AddSetting(importOnStartup);

	alignRight();

    }
}
