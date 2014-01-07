package jp.junkato.vsketch.utils;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;

import jp.junkato.vsketch.ui.VsketchFrame;

public class ProgressLabel extends JLabel {
	private static final long serialVersionUID = -8514615475022942547L;
	public static Color lightGreen = new Color(120, 211, 134);
	private int progress = 0;

	public ProgressLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		initialize();
	}

	public ProgressLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		initialize();
	}

	public ProgressLabel(String text) {
		super(text);
		initialize();
	}

	public ProgressLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		initialize();
	}

	public ProgressLabel(Icon image) {
		super(image);
		initialize();
	}

	public ProgressLabel() {
		super();
		initialize();
	}

	private void initialize() {
		setOpaque(false);
		setFont(VsketchFrame.defaultFont);
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(lightGreen);
		g.fillRect(0, 0, getWidth() * progress / 100, getHeight());
		super.paintComponent(g);
	}

}
