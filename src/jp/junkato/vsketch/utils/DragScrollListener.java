package jp.junkato.vsketch.utils;

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 * <a href="http://terai.xrea.jp/Swing/HandScroll.html">JScrollPaneのViewPortをマウスで掴んでスクロール </a>
 */
public class DragScrollListener extends MouseAdapter {
	private final Point cursorPoint = new Point();

	@Override
	public void mouseDragged(MouseEvent e) {
		final JComponent view = (JComponent) e.getSource();
		Container container = view.getParent();
		if (!(container instanceof JViewport)) {
			return;
		}
		JViewport viewport = (JViewport) container;
		Point cp = SwingUtilities.convertPoint(view, e.getPoint(), viewport);
		Point vp = viewport.getViewPosition();
		vp.translate(cursorPoint.x - cp.x, cursorPoint.y - cp.y);
		view.scrollRectToVisible(new Rectangle(vp, viewport.getSize()));
		cursorPoint.setLocation(cp);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		JComponent view = (JComponent) e.getSource();
		Container container = view.getParent();
		if (!(container instanceof JViewport)) {
			return;
		}
		JViewport viewport = (JViewport) container;
		Point cp = SwingUtilities.convertPoint(view, e.getPoint(), viewport);
		cursorPoint.setLocation(cp);
	}
}