/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyprocstarter;

import lev.gui.LTextPane;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SPSettingPanel;

/**
 *
 * @author Justin Swanson
 */
public class WelcomePanel extends SPSettingPanel {

    LTextPane introText;

    public WelcomePanel(SPMainMenuPanel parent_) {
	super(parent_, SkyProcStarter.myPatchName, SkyProcStarter.headerColor);
    }

    @Override
    protected void initialize() {
	super.initialize();

	introText = new LTextPane(settingsPanel.getWidth() - 40, 400, SkyProcStarter.settingsColor);
	introText.setText(SkyProcStarter.welcomeText);
	introText.setEditable(false);
	introText.setFont(SkyProcStarter.settingsFont);
	introText.setCentered();
	setPlacement(introText);
	Add(introText);

	alignRight();
    }
}
