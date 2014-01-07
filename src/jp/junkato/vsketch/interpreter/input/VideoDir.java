package jp.junkato.vsketch.interpreter.input;

import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.simpleframework.xml.Element;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class VideoDir implements InputSource {
	@Element
	private String dirPath;
	private File[] files;
	private Queue<IplImage> cache;
	private IplImage image;
	private long frameCount;
	private long frameIndex = 0;
	private int cacheSize = 10;
	private CacheFiller cacheFiller;
	private ScheduledExecutorService ses;
	private Future<?> future;

	VideoDir() {}

	public VideoDir(String dirPath) {
		this.dirPath = dirPath;
		reload();
	}

	public static String createIdentifier(String dirPath) {
		return String.format("%s:%s", VideoDir.class.getSimpleName(), dirPath);
	}

	@Override
	public String getIdentifier() {
		return createIdentifier(dirPath);
	}

	@Override
	public void reload() {
		dispose();

		// List up files.
		files = new File(dirPath).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg");
			}
		});
		Arrays.sort(files);

		// Initialize state information.
		frameCount = files.length;
		frameIndex = -1;

		// Setup cache.
		cache = new LinkedList<IplImage>();
		cacheFiller = new CacheFiller();
		cacheFiller.fillCache();

		ses = Executors.newSingleThreadScheduledExecutor();
		future = ses.scheduleAtFixedRate(
				cacheFiller, 15, 15, TimeUnit.MILLISECONDS);
	}

	@Override
	public IplImage nextFrame() {
		try {
			synchronized (cacheFiller) {
				if (cache.size() <= 0) {
					cacheFiller.fillCache();
				}
			}
			IplImage nextImage = cache.poll();
			if (nextImage == null) {
				return null;
			}
			if (image == null ||
					image.width() != nextImage.width() ||
					image.height() != nextImage.height() ||
					image.nChannels() != nextImage.nChannels()) {
				image = nextImage.clone();
			} else {
				cvCopy(nextImage, image);
			}
			cvReleaseImage(nextImage);
			frameIndex ++;
			return image;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean setFrameIndex(long frameIndex) {
		if (frameIndex >= frameCount) {
			frameIndex = frameCount - 1;
		}
		synchronized (cacheFiller) {
			this.frameIndex = frameIndex - 1;
			cacheFiller.fillCache();
		}
		return true;
	}

	@Override
	public long getFrameIndex() {
		return frameIndex;
	}

	@Override
	public long getFrameCount() {
		return frameCount;
	}

	@Override
	public void dispose() {
		if (future != null) {
			future.cancel(true);
			future = null;
		}
		if (ses != null) {
			ses.shutdown();
			ses = null;
		}
		if (files != null) {	
			files = null;
		}
		if (cache != null) {
			for (IplImage image : cache) {
				cvReleaseImage(image);
			}
			cache.clear();
			cache = null;
		}
		if (image != null) {
			image.release();
			image = null;
		}
	}

	@Override
	public String toString() {
		return dirPath;
	}

	private class CacheFiller implements Runnable {
		private long lastLoadedIndex;

		@Override
		public synchronized void run() {
			while (lastLoadedIndex < frameIndex + cacheSize
					&& lastLoadedIndex + 1 < frameCount) {
				loadImage();
			}
		}

		public synchronized void fillCache() {
			for (IplImage image : cache) {
				cvReleaseImage(image);
			}
			cache.clear();
			lastLoadedIndex = frameIndex;
			while (lastLoadedIndex < frameIndex + cacheSize
					&& lastLoadedIndex + 1 < frameCount) {
				loadImage();
			}
		}

		private void loadImage() {
			int index = (int) (lastLoadedIndex + 1);
			IplImage image = null;
			while (index < files.length) {
				File file = files[index];
				image = cvLoadImage(file.getAbsolutePath());
				if (image == null) {
					System.err.print("Loading image failed: ");
					System.err.println(file.getAbsolutePath());
					if (index + 1 >= files.length) {
						// Give up loading the image.
						return;
					}
					System.arraycopy(
							files, index + 1,
							files, index,
							files.length - (index + 1));
					frameCount --;
				} else {
					cache.add(image);
					lastLoadedIndex = index;
					return;
				}
			}
		}
	}

}
