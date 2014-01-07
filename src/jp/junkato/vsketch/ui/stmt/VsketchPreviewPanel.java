package jp.junkato.vsketch.ui.stmt;

import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.tool.Painter;

public class VsketchPreviewPanel extends JPanel {
	private static final long serialVersionUID = 5995825543442891395L;
	private Stmt stmt;
	private Set<Painter> painters;
	private FitMode fitMode;

	public VsketchPreviewPanel() {
		painters = new HashSet<Painter>();
		fitMode = FitMode.ORIGINAL;
	}

	public void setStmt(Stmt stmt) {
		this.stmt = stmt;
	}

	public void addPainter(Painter painter) {
		painters.add(painter);
	}

	public FitMode getFitMode() {
		return fitMode;
	}

	void setFitMode(FitMode fitMode) {
		this.fitMode = fitMode;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		stmt.paintInStmtView(g);
		for (Painter painter : painters) {
			painter.paint(g);
		}
	}

	public enum FitMode {
		ORIGINAL,
		FIT_HORIZONTAL,
		FIT_VERTICAL,
		FIT_BOTH
	}

}
