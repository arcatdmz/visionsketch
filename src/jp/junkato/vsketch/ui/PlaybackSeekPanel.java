package jp.junkato.vsketch.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.interpreter.Interpreter;

public class PlaybackSeekPanel extends JPanel {
	private static final long serialVersionUID = 4927797584787689226L;
	private Repainter repainter;
	private int x = -1;

	public PlaybackSeekPanel() {
		MyMouseAdapter adapter = new MyMouseAdapter();
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		final int maxWidth = getWidth();

		Interpreter interp = VsketchMain.getInstance().getInterpreter();
		long position = -1, duration = -1;
		int width = maxWidth;
		if (interp != null && interp.getDuration() > 0) {
			position = interp.getCurrentPosition();
			duration = interp.getDuration();
			width *= position;
			width /= duration;
		}

		// Show seek bar
		g.setColor(Color.blue);
		g.fillRect(0, 0, width, getHeight());
		if (x >= 0) {
			position = x * duration / maxWidth;
			g.setColor(Color.gray);
			g.fillRect(0, 0, x, getHeight());
		}

		// Show current time
		if (position >= 0) {
			final String time = String.format("%d/%d", position, duration);
			final int y = (getHeight() + VsketchFrame.defaultFont.getSize())/2 - 1;
			((Graphics2D)g).setRenderingHint(
					RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);;
			g.setFont(VsketchFrame.defaultFont);
			g.setColor(Color.black);
			g.drawString(time, 6, y);
			g.setColor(Color.white);
			g.drawString(time, 5, y-1);
		}
	}

	public synchronized void repaintView() {
		if (repainter == null) {
			repainter = new Repainter();
			SwingUtilities.invokeLater(repainter);
		}
	}

	public class Repainter implements Runnable {
		public void run() {
			repaint();
			repainter = null;
		}
	}

	private class MyMouseAdapter extends MouseAdapter {
		@Override
		public void mouseExited(MouseEvent e) {
			x = -1;
			repaintView();
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			mouseMoved(e);
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			x = e.getX();
			repaintView();
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			Interpreter interp = VsketchMain.getInstance().getInterpreter();
			if (interp != null && interp.getDuration() > 0) {
				long frameCount = interp.getDuration();
				frameCount *= e.getX();
				frameCount /= PlaybackSeekPanel.this.getWidth();
				interp.seek(frameCount);
			}
		}
	}

}
