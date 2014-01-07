package jp.junkato.vsketch.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import jp.junkato.vsketch.ui.VsketchFrame;

public class ShowCodeAction extends AbstractAction {
	private static final long serialVersionUID = -6560684440780981827L;

	public ShowCodeAction() {
		putValue(NAME, "Go back to Canvas");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		VsketchFrame frame = VsketchFrame.getInstance();
		frame.showCodePanel();
	}

}
