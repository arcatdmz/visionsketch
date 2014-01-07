package jp.junkato.vsketch.shape;

import java.awt.Graphics;
import java.io.Serializable;

import jp.junkato.vsketch.ui.stmt.VsketchPreviewPane;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public interface Shape extends Serializable, Cloneable {
	boolean contains(int x, int y);
	void fill(IplImage image);
	void paint(Graphics g, VsketchPreviewPane pane);
	Shape clone();
}
