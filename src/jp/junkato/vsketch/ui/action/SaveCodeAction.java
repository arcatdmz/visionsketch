package jp.junkato.vsketch.ui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.interpreter.Code;
import jp.junkato.vsketch.ui.VsketchFrame;
import jp.junkato.vsketch.utils.VsketchUtils;

public class SaveCodeAction extends AbstractAction {
	private static final long serialVersionUID = -8019941637353353036L;

	public SaveCodeAction() {
		putValue(NAME, "Save");
		putValue(SHORT_DESCRIPTION, "");
		putValue(
				SMALL_ICON,
				VsketchFrame.getImageIcon("glyphicons_134_inbox_in.png"));
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		String dirPath = VsketchUtils.saveFileDialog();
		if (dirPath != null) {
			Code code = VsketchMain.getInstance().getCode();
			code.save(new File(dirPath));
		}
//		String name = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss SS").format(new Date());
//		File dir = new File(FunctionCompiler.getDataDir(), name);
//		Code code = VsketchMain.getInstance().getInterpreter().getCode();
//		code.save(dir);
	}

}
