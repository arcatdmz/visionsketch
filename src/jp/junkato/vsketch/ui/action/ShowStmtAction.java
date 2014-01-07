package jp.junkato.vsketch.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import jp.junkato.vsketch.ui.VsketchFrame;

public class ShowStmtAction extends AbstractAction {
	private static final long serialVersionUID = 8112108873267583535L;

	@Override
	public void actionPerformed(ActionEvent e) {
		VsketchFrame frame = VsketchFrame.getInstance();
		frame.showStmtPanel();
	}

}
