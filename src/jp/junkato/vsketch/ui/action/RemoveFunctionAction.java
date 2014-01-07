package jp.junkato.vsketch.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jp.junkato.vsketch.ui.VsketchFrame;

public class RemoveFunctionAction extends AbstractAction {
	private static final long serialVersionUID = -8019941637353353036L;

	public RemoveFunctionAction() {
		putValue(NAME, "Remove");
		putValue(SHORT_DESCRIPTION, "Remove this function");
		putValue(
				SMALL_ICON,
				VsketchFrame.getImageIcon("glyphicons_016_bin.png"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		VsketchFrame.getInstance().getStmtPanel().removeFunction();
	}

}
