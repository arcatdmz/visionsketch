package jp.junkato.vsketch.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jp.junkato.vsketch.ui.VsketchFrame;

public class ShowSettingAction extends AbstractAction {
	private static final long serialVersionUID = 7611634043783447693L;

	public ShowSettingAction() {
		putValue(NAME, "Setting");
		putValue(SHORT_DESCRIPTION, "");
		putValue(
				SMALL_ICON,
				VsketchFrame.getImageIcon("glyphicons_280_settings.png"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

}
