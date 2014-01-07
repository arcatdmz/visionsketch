package jp.junkato.vsketch.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jp.junkato.vsketch.tool.Tool;
import jp.junkato.vsketch.ui.VsketchFrame;

public class ToolAction extends AbstractAction {
	private static final long serialVersionUID = -681638557905280015L;
	public final static String PREFIX = "/icons/";
	private Tool tool;

	public ToolAction(Tool tool) {
		this.tool = tool;
		putValue(NAME, tool.getName());
		putValue(SHORT_DESCRIPTION, tool.getDescription());
		putValue(
				SMALL_ICON,
				VsketchFrame.getImageIcon(tool.getIconFileName()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		VsketchFrame.getInstance().getStmtPanel()
				.getInputPanel().setTool(tool);
	}

}
