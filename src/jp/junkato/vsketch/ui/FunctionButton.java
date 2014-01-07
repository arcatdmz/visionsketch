package jp.junkato.vsketch.ui;

import javax.swing.JButton;

import jp.junkato.vsketch.function.FunctionTemplate;
import jp.junkato.vsketch.ui.action.SetNewFunctionAction;
import jp.junkato.vsketch.utils.VsketchUtils;

public class FunctionButton extends JButton {
	private static final long serialVersionUID = 8613578127798768681L;
	private FunctionTemplate function;
	public FunctionButton(FunctionTemplate function) {
		super(new SetNewFunctionAction(function));
		this.function = function;
		setFont(VsketchFrame.defaultFont);
		if (VsketchUtils.isMac()) {
			putClientProperty("JButton.buttonType", "square");
		}
	}
	public FunctionTemplate getFunction() {
		return function;
	}
}
