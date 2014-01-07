package jp.junkato.vsketch.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.ui.action.ShowCodeAction;
import jp.junkato.vsketch.ui.action.ShowStmtAction;

public class AnimatedGlassPane extends JPanel {
	private static final long serialVersionUID = 8801653083414747909L;
	private static final int FRAMES = 15;
	private static final int DELAY = 15;
	private Timer timer;
	private boolean zoomIn;
	private Point center;
	private int step;
	
	public AnimatedGlassPane() {
		setOpaque(false);
	}

	public void startAnimation(Point center, boolean zoomIn) {
		this.center = center;
		this.zoomIn = zoomIn;

		// Cancel previous timer if any.
		if (timer != null) {
			timer.stop();
		}

		// Setup timer.
		step = FRAMES;
		timer = new Timer(DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				step --;
				if (step <= 0) {
					stopAnimation();
					return;
				}
				repaint();
			}
		});
		timer.start();
		setVisible(true);
		repaint();
	}

	private void stopAnimation() {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
		setVisible(false);
		if (zoomIn) {
			new ShowStmtAction().actionPerformed(null);
		} else {
			new ShowCodeAction().actionPerformed(null);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.black);

		// Zoom in or out.
		int i, j;
		if (zoomIn) {
			i = step;
			j = FRAMES - step;
		} else {
			i = FRAMES - step;
			j = step;
		}

		g.drawRect(
				(center.x - Stmt.THUMBNAIL_WIDTH / 2) * i / FRAMES,
				(center.y - Stmt.THUMBNAIL_HEIGHT / 2) * i / FRAMES,
				(Stmt.THUMBNAIL_WIDTH * i + getWidth() * j) / FRAMES,
				(Stmt.THUMBNAIL_HEIGHT * i + getHeight() * j) / FRAMES);
	}
}