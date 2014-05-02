package jp.junkato.vsketch.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.interpreter.Interpreter;
import jp.junkato.vsketch.interpreter.InterpreterListener;
import jp.junkato.vsketch.utils.VsketchUtils;

public class PlaybackControlPanel extends JPanel implements InterpreterListener {
	private static final long serialVersionUID = 5465021939000658345L;
	public static final String LABEL_STOP = "Stop";
	public static final String LABEL_START = "Start";
	public static final String LABEL_SUPERSTART = "Super Start";
	private JButton btnStartStop;
	private JButton btnSuperStartStop;
	private JButton btnPreviousFrame;
	private PlaybackSeekPanel pnlSeek;
	private JButton btnNextFrame;
	private final Action startOrStopAction = new StartOrStopAction();
	private final Action superStartOrStopAction = new SuperStartOrStopAction();
	private final Action previousFrameAction = new PreviousFrameAction();
	private final Action nextFrameAction = new NextFrameAction();

	/**
	 * Create the panel.
	 */
	public PlaybackControlPanel() {

		GridBagLayout gbl_pnlOption = new GridBagLayout();
		gbl_pnlOption.columnWeights = new double[]{0, 0, 0, 1, 0};
		gbl_pnlOption.rowWeights = new double[]{0};
		setLayout(gbl_pnlOption);
		
		btnStartStop = new JButton();
		btnStartStop.setFont(VsketchFrame.defaultFont);
		btnStartStop.setAction(startOrStopAction);
		if (VsketchUtils.isMac()) {
			btnStartStop.putClientProperty("JButton.buttonType", "square");
		}
		GridBagConstraints gbc_btnStartStop = new GridBagConstraints();
		gbc_btnStartStop.insets = new Insets(5, 0, 0, 5);
		gbc_btnStartStop.fill = GridBagConstraints.BOTH;
		gbc_btnStartStop.anchor = GridBagConstraints.WEST;
		gbc_btnStartStop.gridx = 0;
		gbc_btnStartStop.gridy = 0;
		add(btnStartStop, gbc_btnStartStop);
		
		btnSuperStartStop = new JButton();
		btnSuperStartStop.setFont(VsketchFrame.defaultFont);
		btnSuperStartStop.setAction(superStartOrStopAction);
		if (VsketchUtils.isMac()) {
			btnSuperStartStop.putClientProperty("JButton.buttonType", "square");
		}
		GridBagConstraints gbc_btnSuperStartStop = new GridBagConstraints();
		gbc_btnSuperStartStop.insets = new Insets(5, 0, 0, 5);
		gbc_btnSuperStartStop.fill = GridBagConstraints.BOTH;
		gbc_btnSuperStartStop.anchor = GridBagConstraints.WEST;
		gbc_btnSuperStartStop.gridx = 1;
		gbc_btnSuperStartStop.gridy = 0;
		add(btnSuperStartStop, gbc_btnSuperStartStop);
		
		pnlSeek = new PlaybackSeekPanel();

		btnPreviousFrame = new JButton();
		btnPreviousFrame.setFont(VsketchFrame.defaultFont);
		btnPreviousFrame.setAction(previousFrameAction);
		btnPreviousFrame.setHorizontalTextPosition(SwingConstants.RIGHT);
		if (VsketchUtils.isMac()) {
			btnPreviousFrame.putClientProperty("JButton.buttonType", "square");
		}
		GridBagConstraints gbc_btnPreviousFrame = new GridBagConstraints();
		gbc_btnPreviousFrame.fill = GridBagConstraints.BOTH;
		gbc_btnPreviousFrame.insets = new Insets(5, 0, 0, 5);
		gbc_btnPreviousFrame.gridx = 2;
		gbc_btnPreviousFrame.gridy = 0;
		add(btnPreviousFrame, gbc_btnPreviousFrame);

		GridBagConstraints gbc_pnlSeek = new GridBagConstraints();
		gbc_pnlSeek.insets = new Insets(5, 0, 0, 5);
		gbc_pnlSeek.anchor = GridBagConstraints.EAST;
		gbc_pnlSeek.fill = GridBagConstraints.BOTH;
		gbc_pnlSeek.gridx = 3;
		gbc_pnlSeek.gridy = 0;
		add(pnlSeek, gbc_pnlSeek);
		
		btnNextFrame = new JButton();
		btnNextFrame.setFont(VsketchFrame.defaultFont);
		btnNextFrame.setAction(nextFrameAction);
		btnNextFrame.setHorizontalTextPosition(SwingConstants.LEFT);
		if (VsketchUtils.isMac()) {
			btnNextFrame.putClientProperty("JButton.buttonType", "square");
		}
		GridBagConstraints gbc_btnNextFrame = new GridBagConstraints();
		gbc_btnNextFrame.fill = GridBagConstraints.BOTH;
		gbc_btnNextFrame.insets = new Insets(5, 0, 0, 0);
		gbc_btnNextFrame.gridx = 4;
		gbc_btnNextFrame.gridy = 0;
		add(btnNextFrame, gbc_btnNextFrame);

		if (VsketchUtils.isMac()) {
			setBorder(new EmptyBorder(0, 0, 3, 0));
		}
	}

	public void repaintView() {
		pnlSeek.repaintView();
	}

	private class StartOrStopAction extends AbstractAction {
		private static final long serialVersionUID = -6384450994263922507L;
		public StartOrStopAction() {
			putValue(NAME, LABEL_START);
			putValue(SHORT_DESCRIPTION, "Start or stop playing the program.");
		}
		public void actionPerformed(ActionEvent e) {
			Interpreter interp = VsketchMain.getInstance().getInterpreter();
			if (interp != null) {
				if (!interp.isPlaying()) {
					interp.play();
				} else {
					interp.stop();
				}
			}
		}
	}

	private class SuperStartOrStopAction extends AbstractAction {
		private static final long serialVersionUID = 6741815513700372721L;
		public SuperStartOrStopAction() {
			putValue(NAME, LABEL_SUPERSTART);
			putValue(SHORT_DESCRIPTION, "Super start or stop playing the program.");
		}
		public void actionPerformed(ActionEvent e) {
			Interpreter interp = VsketchMain.getInstance().getInterpreter();
			if (interp != null) {
				if (!interp.isPlaying()) {
					interp.play(1);
				} else {
					interp.stop();
				}
			}
		}
	}

	private class PreviousFrameAction extends AbstractAction {
		private static final long serialVersionUID = -6340165964871674658L;
		public PreviousFrameAction() {
			putValue(SHORT_DESCRIPTION, "Go back to the previous frame.");
			putValue(SMALL_ICON, VsketchFrame.getImageIcon("glyphicons_172_rewind.png"));
		}
		public void actionPerformed(ActionEvent e) {
			Interpreter interp = VsketchMain.getInstance().getInterpreter();
			if (interp != null && interp.getCurrentPosition() > 0) {
				interp.seek(interp.getCurrentPosition() - 1);
			}
		}
	}
	private class NextFrameAction extends AbstractAction {
		private static final long serialVersionUID = 2945880410707733909L;
		public NextFrameAction() {
			putValue(SHORT_DESCRIPTION, "Go forward to the next frame.");
			putValue(SMALL_ICON, VsketchFrame.getImageIcon("glyphicons_176_forward.png"));
		}
		public void actionPerformed(ActionEvent e) {
			Interpreter interp = VsketchMain.getInstance().getInterpreter();
			if (interp != null) {
				interp.next();
			}
		}
	}

	@Override
	public void onStarted() {
		btnStartStop.setText(LABEL_STOP);
		btnSuperStartStop.setText(LABEL_STOP);
		btnSuperStartStop.setVisible(false);
	}

	@Override
	public void onStopped() {
		btnStartStop.setText(LABEL_START);
		btnSuperStartStop.setText(LABEL_SUPERSTART);
		btnSuperStartStop.setVisible(true);
	}

}
