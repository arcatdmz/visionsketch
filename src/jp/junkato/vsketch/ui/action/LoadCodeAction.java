package jp.junkato.vsketch.ui.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.interpreter.Code;
import jp.junkato.vsketch.ui.VsketchFrame;
import jp.junkato.vsketch.utils.VsketchUtils;

public class LoadCodeAction extends AbstractAction {
	private static final long serialVersionUID = -8019941637353353036L;

	public LoadCodeAction() {
		putValue(NAME, "Load");
		putValue(SHORT_DESCRIPTION, "");
		putValue(SMALL_ICON,
				VsketchFrame.getImageIcon("glyphicons_135_inbox_out.png"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String dirPath = VsketchUtils.openDirectoryDialog();
		if (dirPath != null) {
			load(new File(dirPath));
		}
	}

	private void load(File dir) {
		Code code = Code.load(dir);
		if (code != null) {
			VsketchMain.getInstance().setCode(code);
		}
	}
}
