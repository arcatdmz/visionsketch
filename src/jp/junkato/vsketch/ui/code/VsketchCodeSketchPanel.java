package jp.junkato.vsketch.ui.code;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import sun.swing.SwingUtilities2;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.function.Function;
import jp.junkato.vsketch.function.FunctionParameter;
import jp.junkato.vsketch.interpreter.Input;
import jp.junkato.vsketch.interpreter.Stmt;
import jp.junkato.vsketch.ui.VsketchFrame;

public class VsketchCodeSketchPanel extends JPanel {
	private static final long serialVersionUID = 3594142212901249705L;
	private MouseAdapter mouseAdapter;
	private Stmt pressedStmt;
	private Stmt draggingStmt;
	private boolean isDragging = false;
	private int originalX, originalY;
	private int x, y;
	private boolean initialized;
	private BufferedImage background;

	public VsketchCodeSketchPanel(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		updateBackground(width, height);

		this.mouseAdapter = new VsketchMouseAdapter();
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
	}

	private void updateBackground(int width, int height) {
		if (background == null ||
				background.getWidth() < width ||
				background.getHeight() < height) {
			BufferedImage oldImage = background;
			background = new BufferedImage(
					width, height, BufferedImage.TYPE_INT_BGR);
			Graphics g = background.getGraphics();
			g.setColor(getBackground());
			g.fillRect(0, 0, width, height);
			if (oldImage != null) {
				g.drawImage(oldImage, 0, 0, null);
			}
			g.dispose();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!initialized) {
			SwingUtilities2.getGraphics2D(g).setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		updateBackground(getWidth(), getHeight());
		g.drawImage(background, 0, 0, null);

		if (VsketchMain.getInstance().getCode() == null) {
			return;
		}

		if (isDragging && draggingStmt == null && pressedStmt != null) {
			g.setColor(Color.red);
			g.drawLine(originalX, originalY, x, y);
		}
		if (pressedStmt != null && pressedStmt.isInside(x, y)) {
			VsketchMain.getInstance().getCode().paint(g, pressedStmt, x, y);
		} else {
			VsketchMain.getInstance().getCode().paint(g, null, 0, 0);
		}
	}

	private class VsketchMouseAdapter extends MouseAdapter {
		Graphics g;

		@Override
		public void mousePressed(MouseEvent e) {

			if (VsketchMain.getInstance().getCode() == null) {
				return;
			}

			x = e.getX();
			y = e.getY();
			for (Stmt stmt : VsketchMain.getInstance().getCode().getStmts()) {
				if (stmt.isInBorder(x, y)) {
					originalX = stmt.getX() + Stmt.THUMBNAIL_WIDTH / 2;
					originalY = stmt.getY() + Stmt.THUMBNAIL_HEIGHT / 2;
					if (stmt.isOnBorder(x, y)) {
						draggingStmt = stmt;
					} else {
						pressedStmt = stmt;
					}
				}
			}
			isDragging = true;
			if (draggingStmt == null && pressedStmt == null) {
				g = background.getGraphics();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			updateNode(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			updateNode(e);
			if (pressedStmt != null) {
				if (draggingStmt == null) {
					if (pressedStmt.isInside(e.getX(), e.getY())) {
						if (e.getButton() == MouseEvent.BUTTON1) {
							pressedStmt.edit(
									e.getX() - pressedStmt.getX(),
									e.getY() - pressedStmt.getY());
						} else {
							showMenu(pressedStmt, e.getX(), e.getY());
						}
					} else if (pressedStmt.getRawOutput() != null) {
						pressedStmt.newChild(e.getX(), e.getY());
					}
				}
			}
			draggingStmt = null;
			pressedStmt = null;
			isDragging = false;
			if (g != null) {
				g.dispose();
			}
		}

		private FunctionParameter parameter = null;
		private void showMenu(Stmt stmt, int x, int y) {
			Function f = stmt.getFunction();
			if (f == null) {
				return;
			}
			FunctionParameter parameter = f.getParameter();
			if (this.parameter == null && parameter == null) {
				return;
			}
			JPopupMenu menu = new JPopupMenu();
			if (parameter != null) {
				menu.add(new JMenuItem(new CopyParameterAction(parameter)));
			}
			if (this.parameter != null) {
				menu.add(new JMenuItem(new PasteParameterAction(stmt)));
			}
			menu.show(VsketchCodeSketchPanel.this, x, y);
		}

		private class CopyParameterAction extends AbstractAction {
			private static final long serialVersionUID = -5863136978206658617L;
			private FunctionParameter parameter = null;

			public CopyParameterAction(FunctionParameter parameter) {
				this.parameter = parameter;
				putValue(NAME, "Copy parameter");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				VsketchMouseAdapter.this.parameter = this.parameter;
			}
		}

		private class PasteParameterAction extends AbstractAction {
			private static final long serialVersionUID = -285967925850446763L;
			Stmt stmt;
			public PasteParameterAction(Stmt stmt) {
				this.stmt = stmt;
				putValue(NAME, "Paste parameter");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				stmt.getFunction().parameterize(VsketchMouseAdapter.this.parameter.clone());
				if (!VsketchMain.getInstance().getInterpreter().isPlaying()) {
					stmt.nextFrame();
					repaint();
				}
			}
		}

		private void updateNode(MouseEvent e) {
			if (draggingStmt != null) {
				draggingStmt.setX(draggingStmt.getX() + e.getX() - x);
				draggingStmt.setY(draggingStmt.getY() + e.getY() - y);
				if (!(draggingStmt instanceof Input) && (
						draggingStmt.getX() < 0 || draggingStmt.getY() < 0 ||
						draggingStmt.getX() + Stmt.THUMBNAIL_WIDTH > getWidth() ||
						draggingStmt.getY() + Stmt.THUMBNAIL_HEIGHT > getHeight())) {
					VsketchMain.getInstance().getCode().remove(draggingStmt);
				}
			}
			if (g != null) {
				if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) > 0) {
					SwingUtilities2.getGraphics2D(g).setStroke(VsketchFrame.boldStroke);
					g.setColor(getBackground());
				} else {
					SwingUtilities2.getGraphics2D(g).setStroke(VsketchFrame.stroke);
					g.setColor(Color.blue);
				}
				g.drawLine(x, y, e.getX(), e.getY());
			}
			x = e.getX();
			y = e.getY();
			repaint();
		}
	}
}
