package jp.junkato.vsketch.ui.stmt;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.ui.VsketchFrame;
import jp.junkato.vsketch.ui.stmt.VsketchPreviewPanel.FitMode;

public class VsketchPreviewPane extends JScrollPane {
	private static final long serialVersionUID = 6179086466010398422L;
	private VsketchPreviewPanel panel;
	private Stmt stmt;

	public VsketchPreviewPane() {
		this.panel = new VsketchPreviewPanel();
		this.setViewportView(panel);
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
		if (stmt != null) {
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

	public class ChangeFitModeAction extends AbstractAction {
		private static final long serialVersionUID = 2356953880198901552L;
		public ChangeFitModeAction() {
			putValue(NAME, "");
			putValue(SHORT_DESCRIPTION, "Change how the image fits to the panel boundary.");
			putValue(SMALL_ICON, new ImageIcon(VsketchFrame.class.getResource("/glyphicons_215_resize_full.png")));
		}
		public void actionPerformed(ActionEvent e) {
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
	}
}
