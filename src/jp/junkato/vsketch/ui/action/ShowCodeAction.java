package jp.junkato.vsketch.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jp.junkato.vsketch.ui.VsketchFrame;

public class ShowCodeAction extends AbstractAction {
	private static final long serialVersionUID = -6560684440780981827L;

	public ShowCodeAction() {
		putValue(NAME, "Canvas");
		putValue(SHORT_DESCRIPTION, "");
		putValue(SMALL_ICON,
				VsketchFrame.getImageIcon("glyphicons_216_circle_arrow_left.png"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		VsketchFrame frame = VsketchFrame.getInstance();
		frame.showCodePanel();
	}

}
