package jp.junkato.vsketch.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.ui.VsketchFrame;

public class UpdateFunctionAction extends AbstractAction {
	private static final long serialVersionUID = -7307191536690223019L;

	public UpdateFunctionAction() {
		putValue(NAME, "Update");
		putValue(SHORT_DESCRIPTION, "Update the definition of this function.");
		putValue(
				SMALL_ICON,
				VsketchFrame.getImageIcon("glyphicons_081_refresh.png"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		VsketchFrame.getInstance().getStmtPanel()
				.getEditorPanel().save();
		VsketchMain.getInstance().getInterpreter().reload(
				VsketchFrame.getInstance().getStmtPanel()
				.getEditorPanel().getFunctionDefinition());
	}

}
