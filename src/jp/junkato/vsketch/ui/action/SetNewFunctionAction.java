package jp.junkato.vsketch.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jp.junkato.vsketch.function.FunctionTemplate;
import jp.junkato.vsketch.ui.VsketchFrame;

public class SetNewFunctionAction extends AbstractAction {
	private static final long serialVersionUID = -681638557905280015L;
	private FunctionTemplate functionTemplate;

	public SetNewFunctionAction(FunctionTemplate function) {
		this.functionTemplate = function;
		putValue(NAME, function.getName());
		putValue(SHORT_DESCRIPTION, function.getDescription());
		putValue(
				SMALL_ICON,
				VsketchFrame.getImageIcon(function.getIconFileName()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		VsketchFrame.getInstance().getStmtPanel().setNewFunction(functionTemplate);
	}

}
