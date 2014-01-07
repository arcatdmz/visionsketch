package jp.junkato.vsketch.interpreter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.lang.reflect.Constructor;

import org.simpleframework.xml.Element;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.function.Function;
import jp.junkato.vsketch.interpreter.input.Camera;
import jp.junkato.vsketch.interpreter.input.HTTPServer;
import jp.junkato.vsketch.interpreter.input.InputSource;
import jp.junkato.vsketch.interpreter.input.VideoDir;
import jp.junkato.vsketch.interpreter.input.VideoFile;
import jp.junkato.vsketch.ui.VsketchFrame;
import jp.junkato.vsketch.utils.VsketchUtils;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Input extends Stmt {
	private static Stroke s = new BasicStroke(2);
	@Element
	private InputSource source;
	private IplImage thumbnail;
	private IplImage image;
	private boolean thumbnailUpToDate;

	Input() {
	}

	private Input(Code code) {
		super(code, null);
	}

	public void setIdentifier(String identifier) {
		VsketchMain.getInstance().getInterpreter().stop();
		if (source != null) {
			source.dispose();
			image = null;
			thumbnail = null;
		}
		try {
			int optionIndex = identifier.indexOf(':');
			String simpleClassName = optionIndex < 0 ? identifier : identifier.substring(0, optionIndex);
			String option = optionIndex < 0 ? null : identifier.substring(optionIndex + 1);
			@SuppressWarnings("unchecked")
			Class<InputSource> cls = (Class<InputSource>)
					Class.forName(String.format("%s.%s",
							InputSource.class.getPackage().getName(), simpleClassName));
			Constructor<InputSource> constructor;
			if (option == null) {
				constructor = cls.getConstructor();
				source = constructor.newInstance();
			} else try {
				int i = Integer.parseInt(option);
				constructor = cls.getConstructor(int.class);
				source = constructor.newInstance(i);
			} catch (NumberFormatException nfe) {
				constructor = cls.getConstructor(String.class);
				source = constructor.newInstance(option);
			}
		} catch (Exception e) {
			e.printStackTrace();
			source = new Camera();
		}
		nextFrame();
		setFrameIndex(0);
	}

	public String getIdentifier() {
		return source == null ? null : source.getIdentifier();
	}

	public void reload() {
		source.reload();
	}

	public synchronized boolean nextFrame() {
		IplImage nextFrame = source.nextFrame();
		if (nextFrame == null) {
			return false;
		}
		image = nextFrame;
		thumbnailUpToDate = false;
		return super.nextFrame();
	}

	public synchronized void setFrameIndex(long frameIndex) {
		source.setFrameIndex(frameIndex);
	}

	public long getFrameIndex() {
		return source.getFrameIndex();
	}

	public long getFrameCount() {
		return source.getFrameCount();
	}

	@Override
	public void edit(int offsetX, int offsetY) {
		if (isVideoFileActive(offsetX, offsetY)) {
			openVideoFile();
		} else if (isVideoDirActive(offsetX, offsetY)) {
			openVideoDir();
		} else if (isCameraActive(offsetX, offsetY)) {
			openCamera();
		} else if (isHTTPServerActive(offsetX, offsetY)) {
			openHTTPServer();
		}
	}

	private void openVideoFile() {
		String filePath = VsketchUtils.openFileDialog();
		if (filePath != null) {
			setIdentifier(VideoFile.createIdentifier(filePath));
		}
	}

	private void openVideoDir() {
		String dirPath = VsketchUtils.openDirectoryDialog();
		if (dirPath != null) {
			setIdentifier(VideoDir.createIdentifier(dirPath));
		}
	}

	private void openCamera() {
		setIdentifier(Camera.createIdentifier());
	}

	private void openHTTPServer() {
		setIdentifier(HTTPServer.createIdentifier());
	}

	@Override
	public void paintInCodeView(Graphics g, boolean isActive, int mx, int my) {
		Graphics2D g2 = (Graphics2D) g;
		Stroke s_ = g2.getStroke();
		g2.setStroke(s);
		super.paintInCodeView(g, isActive, mx, my);
		g2.setStroke(s_);
		if (isActive) {
			int offsetX = mx - getX();
			int offsetY = my - getY();
			g.setFont(VsketchFrame.smallFont);
			g.setColor(isVideoFileActive(offsetX, offsetY) ?
				Color.yellow : Color.white);
			g.fillRect(
					getX() + 1,
					getY() + 1,
					Stmt.THUMBNAIL_WIDTH / 2 - 2,
					Stmt.THUMBNAIL_HEIGHT / 2 - 2);
			g.setColor(isVideoDirActive(offsetX, offsetY) ?
				Color.yellow : Color.white);
			g.fillRect(
					getX() + Stmt.THUMBNAIL_WIDTH / 2,
					getY() + 1,
					Stmt.THUMBNAIL_WIDTH / 2 - 1,
					Stmt.THUMBNAIL_HEIGHT / 2 - 2);
			g.setColor(isCameraActive(offsetX, offsetY) ?
				Color.yellow : Color.white);
			g.fillRect(
					getX() + 1,
					getY() + Stmt.THUMBNAIL_HEIGHT / 2,
					Stmt.THUMBNAIL_WIDTH / 2 - 2,
					Stmt.THUMBNAIL_HEIGHT / 4 - 1);
			g.setColor(isHTTPServerActive(offsetX, offsetY) ?
					Color.yellow : Color.white);
			g.fillRect(
					getX() + 1,
					getY() + Stmt.THUMBNAIL_HEIGHT * 3 / 4,
					Stmt.THUMBNAIL_WIDTH / 2 - 2,
					Stmt.THUMBNAIL_HEIGHT / 4 - 1);
			g.setColor(Color.black);
			g.drawString("File",
					getX() + 5,
					getY() + Stmt.THUMBNAIL_HEIGHT / 4 + 5);
			g.drawString("Directory",
					getX() + Stmt.THUMBNAIL_WIDTH / 2 + 4,
					getY() + Stmt.THUMBNAIL_HEIGHT / 4 + 5);
			g.drawString("Camera",
					getX() + 5,
					getY() + Stmt.THUMBNAIL_HEIGHT * 5 / 8 + 5);
			g.drawString("HTTP Server",
					getX() + 5,
					getY() + Stmt.THUMBNAIL_HEIGHT * 7 / 8 + 5);
		}
	}

	private boolean isVideoFileActive(int offsetX, int offsetY) {
		return offsetX < Stmt.THUMBNAIL_WIDTH / 2 - 1 &&
				offsetY < Stmt.THUMBNAIL_HEIGHT / 2 - 1;
	}

	private boolean isVideoDirActive(int offsetX, int offsetY) {
		return offsetX >= Stmt.THUMBNAIL_WIDTH / 2 - 1 &&
				offsetY < Stmt.THUMBNAIL_HEIGHT / 2 - 1;
	}

	private boolean isCameraActive(int offsetX, int offsetY) {
		return offsetX < Stmt.THUMBNAIL_WIDTH / 2 - 1 &&
				offsetY >= Stmt.THUMBNAIL_HEIGHT / 2 - 1 &&
				offsetY < Stmt.THUMBNAIL_HEIGHT * 3 / 4 - 1;
	}

	private boolean isHTTPServerActive(int offsetX, int offsetY) {
		return offsetX < Stmt.THUMBNAIL_WIDTH / 2 - 1 &&
				offsetY >= Stmt.THUMBNAIL_HEIGHT * 3 / 4 - 1;
	}

	@Override
	public int getWidth() {
		return image == null ? 0 : image.width();
	}

	@Override
	public int getHeight() {
		return image == null ? 0 : image.height();
	}

	@Override
	public IplImage getRawThumbnail() {
		if (thumbnailUpToDate) {
			return thumbnail;
		}
		thumbnailUpToDate = true;
		if (image == null) {
			return null;
		}
		if (thumbnail == null ||
				thumbnail.depth() != image.depth() ||
				thumbnail.nChannels() != image.nChannels()) {
			thumbnail = IplImage.create(
					Stmt.THUMBNAIL_WIDTH, Stmt.THUMBNAIL_HEIGHT,
					image.depth(), image.nChannels());
		}
		Function.calculateDefaultThumbnail(getRawOutput(), thumbnail, null);
		return thumbnail;
	}

	@Override
	public IplImage getRawOutput() {
		return image;
	}

	@Override
	public synchronized void dispose() {
		if (image != null) {
			// image.release(); // Released by the source.
			image = null;
		}
		if (source != null) {
			source.dispose();
			source = null;
		}
	}

	public static Input newInstance(Code code, String identifier) {
		Input input = new Input(code);
		input.setIdentifier(identifier);
		return input;
	}

}
