package jp.junkato.vsketch.ui;

import javax.swing.JButton;

import jp.junkato.vsketch.tool.Tool;
import jp.junkato.vsketch.ui.action.ToolAction;

public class ToolButton extends JButton {
	private static final long serialVersionUID = 8613578127798768681L;
	private Tool tool;
	public ToolButton(Tool tool) {
		super(new ToolAction(tool));
		this.tool = tool;
		setFont(VsketchFrame.defaultFont);
	}
	public Tool getTool() {
		return tool;
	}
}
