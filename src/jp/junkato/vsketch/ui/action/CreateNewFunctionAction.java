package jp.junkato.vsketch.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.function.FunctionDefinition;
import jp.junkato.vsketch.ui.VsketchFrame;

public class CreateNewFunctionAction extends AbstractAction {
	private static final long serialVersionUID = 6625666659785617381L;

	public CreateNewFunctionAction() {
		putValue(NAME, "New");
		putValue(SHORT_DESCRIPTION, "Create new function.");
		putValue(
				SMALL_ICON,
				VsketchFrame.getImageIcon("glyphicons_036_file.png"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String className = JOptionPane.showInputDialog(
				VsketchFrame.getInstance(), "Class name for this image processing component:");
		if (className == null) {
			return;
		}
		if (VsketchMain.getInstance().getCompiler().getDefinition(className) != null) {
			JOptionPane.showMessageDialog(
					VsketchFrame.getInstance(), "Class with name \"" + className + "\" already exists.");
			return;
		}
		FunctionDefinition def = new FunctionDefinition();
		def.setClassName(className);
		VsketchFrame.getInstance().getStmtPanel().openEditor(def);
	}

}
