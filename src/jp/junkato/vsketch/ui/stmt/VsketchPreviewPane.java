package jp.junkato.vsketch.ui.stmt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.RepaintManager;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.ui.Icon;
import jp.junkato.vsketch.ui.VsketchFrame;
import jp.junkato.vsketch.ui.stmt.VsketchPreviewPanel.FitMode;

public class VsketchPreviewPane extends JScrollPane {
	private static final long serialVersionUID = -7261812132374951716L;
	private VsketchPreviewPanel panel;
	private Stmt stmt;
	private Icon fitModeIcon;
	private static final int fitModeIconPadding = 5;
	private boolean buttonVisible;
	private boolean buttonPressed;

	public VsketchPreviewPane() {
		panel = new VsketchPreviewPanel();
		setViewportView(panel);
		fitModeIcon = VsketchFrame.getIcon("glyphicons_215_resize_full.png");
		MyMouseListener listener = new MyMouseListener();
		panel.addMouseListener(listener);
		panel.addMouseMotionListener(listener);
	}

	public VsketchPreviewPanel getPanel() {
		return panel;
	}

	public void setStmt(Stmt stmt) {
		this.stmt = stmt;
		panel.setStmt(stmt);
		setFitMode(panel.getFitMode());
		repaint();
	}

	public int viewToImageX(int x) {
		switch (panel.getFitMode()) {
		case FIT_BOTH:
		case FIT_HORIZONTAL:
			return x * stmt.getWidth() / panel.getWidth();
		case FIT_VERTICAL:
			return x * stmt.getHeight() / panel.getHeight();
		default:
			return x;
		}
	}

	public int viewToImageY(int y) {
		switch (panel.getFitMode()) {
		case FIT_BOTH:
		case FIT_VERTICAL:
			return y * stmt.getHeight() / panel.getHeight();
		case FIT_HORIZONTAL:
			return y * stmt.getWidth() / panel.getWidth();
		default:
			return y;
		}
	}

	public int imageToViewX(int x) {
		switch (panel.getFitMode()) {
		case FIT_BOTH:
		case FIT_HORIZONTAL:
			return x * panel.getWidth() / stmt.getWidth();
		case FIT_VERTICAL:
			return x * panel.getHeight() / stmt.getHeight();
		default:
			return x;
		}
	}

	public int imageToViewY(int y) {
		switch (panel.getFitMode()) {
		case FIT_BOTH:
		case FIT_VERTICAL:
			return y * panel.getHeight() / stmt.getHeight();
		case FIT_HORIZONTAL:
			return y * panel.getWidth() / stmt.getWidth();
		default:
			return y;
		}
	}

	public void setFitMode(FitMode fitMode) {
		panel.setFitMode(fitMode);

		// Set scroll bar policy.
		setHorizontalScrollBarPolicy(
				fitMode == FitMode.FIT_VERTICAL || fitMode == FitMode.ORIGINAL ?
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED :
							ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(
				fitMode == FitMode.FIT_VERTICAL || fitMode == FitMode.FIT_BOTH ?
						ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER :
							ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		updatePanelSize();
		panel.repaint();
	}

	public void updatePanelSize() {

		// Set panel size.
		if (stmt != null
				&& stmt.getWidth() > 0 && stmt.getHeight() > 0) {
			int width, height;
			switch (panel.getFitMode()) {
			case FIT_VERTICAL:
				height = getViewport().getHeight();
				width = stmt.getWidth() * height / stmt.getHeight();
				break;

			case FIT_HORIZONTAL:
				width = getViewport().getWidth();
				height = stmt.getHeight() * width / stmt.getWidth();
				break;

			case FIT_BOTH:
				width = getViewport().getWidth();
				height = getViewport().getHeight();
				break;

			case ORIGINAL:
			default:
				width = stmt.getWidth();
				height = stmt.getHeight();
				break;
			}
			panel.setPreferredSize(new Dimension(width, height));
			panel.setSize(new Dimension(width, height));
		}
	}

	private int getFitModeButtonX() {
		int x = getWidth() - fitModeIcon.image.getIconWidth() - 15;
		if (getVerticalScrollBar().isShowing())
			x -= getVerticalScrollBar().getWidth();
		return x;
	}

	private int getFitModeButtonY() {
		int y = getHeight() - fitModeIcon.image.getIconHeight() - 15;
		if (getHorizontalScrollBar().isShowing())
			y -= getHorizontalScrollBar().getHeight();
		return y;
	}

	private int getFitModeButtonWidth() {
		return fitModeIcon.image.getIconWidth()
				+ fitModeIconPadding * 2;
	}

	private int getFitModeButtonHeight() {
		return fitModeIcon.image.getIconHeight()
				+ fitModeIconPadding * 2;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (buttonVisible || buttonPressed) {
			int x = getFitModeButtonX();
			int y = getFitModeButtonY();
			g.translate(x, y);
			Color c = g.getColor();
			g.setColor(Color.white);
			g.fillRect(0, 0,
					getFitModeButtonWidth(),
					getFitModeButtonHeight());
			fitModeIcon.image.paintIcon(this, g,
					fitModeIconPadding,
					fitModeIconPadding);
			g.setColor(c);
			g.translate(-x, -y);
		}
	}

	private void changeFitMode() {
		switch (panel.getFitMode()) {
		case ORIGINAL:
			setFitMode(FitMode.FIT_VERTICAL);
			break;
		case FIT_VERTICAL:
			setFitMode(FitMode.FIT_HORIZONTAL);
			break;
		case FIT_HORIZONTAL:
			setFitMode(FitMode.FIT_BOTH);
			break;
		case FIT_BOTH:
		default:
			setFitMode(FitMode.ORIGINAL);
			break;
		}
	}

	private class MyMouseListener extends MouseInputAdapter{

		@Override
		public void mouseMoved(MouseEvent e) {
			Point p = SwingUtilities.convertPoint(
					e.getComponent(),
					e.getX(), e.getY(),
					VsketchPreviewPane.this);
			boolean visible = buttonVisible;
			buttonVisible = p.getX() > getFitModeButtonX() - 40
					&& p.getX() < getFitModeButtonX() + getFitModeButtonWidth();
			buttonVisible &= p.getY() > getFitModeButtonY() - 40
					&& p.getY() < getFitModeButtonY() + getFitModeButtonHeight();
			if (visible != buttonVisible) {
				RepaintManager rm = RepaintManager.currentManager(VsketchPreviewPane.this);
				rm.addDirtyRegion(VsketchPreviewPane.this,
						getFitModeButtonX(), getFitModeButtonY(),
						getFitModeButtonWidth(), getFitModeButtonHeight());
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (buttonVisible) {
				buttonPressed = true;
				e.consume();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (buttonPressed) {
				e.consume();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (buttonPressed) {
				buttonPressed = false;
				changeFitMode();
				e.consume();
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			buttonVisible = false;
			buttonPressed = false;
		}
	}

	public class ChangeFitModeAction extends AbstractAction {
		private static final long serialVersionUID = 2356953880198901552L;
		public ChangeFitModeAction() {
			putValue(NAME, "");
			putValue(SHORT_DESCRIPTION, "Change how the image fits to the panel boundary.");
			putValue(SMALL_ICON, VsketchFrame.getIcon("glyphicons_215_resize_full.png").image);
		}
		public void actionPerformed(ActionEvent e) {
			changeFitMode();
		}
	}
}
