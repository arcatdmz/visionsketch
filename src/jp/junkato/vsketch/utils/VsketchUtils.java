package jp.junkato.vsketch.utils;

import javax.swing.JFileChooser;

import jp.junkato.vsketch.ui.VsketchFrame;
import net.tomahawk.XFileDialog;

public class VsketchUtils {

	public static double distance(double sx, double sy, double ex, double ey) {
		return Math.sqrt((sx - ex)*(sx - ex) + (sy - ey)*(sy - ey));
	}

	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	public static String openFileDialog() {

		if (VsketchFrame.isWindows()) {
			XFileDialog xfd = new XFileDialog(VsketchFrame.getInstance());
			String content = xfd.getFile();
			String folder = xfd.getDirectory();
			xfd.dispose();
			return content == null ? null : folder + content;
		}

		JFileChooser jfc = new JFileChooser();
		if (jfc.showOpenDialog(VsketchFrame.getInstance()) == JFileChooser.APPROVE_OPTION) {
			return jfc.getSelectedFile().getAbsolutePath();
		}

		return null;
	}

	public static String saveFileDialog() {

		if (VsketchFrame.isWindows()) {
			XFileDialog xfd = new XFileDialog(VsketchFrame.getInstance());
			String content = xfd.getSaveFile();
			String folder = xfd.getDirectory();
			xfd.dispose();
			return content == null ? null : folder + content;
		}

		JFileChooser jfc = new JFileChooser();
		jfc.setDialogType(JFileChooser.SAVE_DIALOG);
		if (jfc.showOpenDialog(VsketchFrame.getInstance()) == JFileChooser.APPROVE_OPTION) {
			return jfc.getSelectedFile().getAbsolutePath();
		}

		return null;
	}

	public static String openDirectoryDialog() {

		if (VsketchFrame.isWindows()) {
			XFileDialog xfd = new XFileDialog(VsketchFrame.getInstance());
			String folder = xfd.getFolder();
			xfd.dispose();
			return folder;
		}

		// This subclass is for Mac OS X directory selection issue.
		// http://stackoverflow.com/questions/2883447/jfilechooser-select-directory-but-show-files
		JFileChooser jfc = new JFileChooser() {
			private static final long serialVersionUID = 2375005218638487741L;
			@Override
			public void approveSelection() {
				if (getSelectedFile().isFile()) {
					return;
				}
				super.approveSelection();
			}
		};
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (jfc.showOpenDialog(VsketchFrame.getInstance()) == JFileChooser.APPROVE_OPTION) {
			return jfc.getSelectedFile().getAbsolutePath();
		}

		return null;
	}
}
