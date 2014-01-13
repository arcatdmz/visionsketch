package jp.junkato.vsketch.interpreter.input;

import java.io.IOException;

import org.simpleframework.xml.Element;

import jp.junkato.vsketch.VsketchMain;
import jp.junkato.vsketch.server.PostedFileHandler;
import jp.junkato.vsketch.server.SimpleHttpServer;
import jp.junkato.vsketch.server.PostedFileHandler.FileData;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvDecodeImage;

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;

public class HTTPServer implements InputSource {
	/**
	 * TODO Allow users to set the server port
	 */
	@Element(required=false)
	private int port = 8080;
	private PostedFileHandler handler;
	private HttpContext context;
	private IplImage image;
	private long frameIndex;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new HTTPServer();
	}

	public HTTPServer() {
		handler = new PostedFileHandler() {
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				super.handle(exchange);
				for (FileData file : files) {
					onFilePosted(file);
				}
			}
		};
		reload();
	}

	public static String createIdentifier() {
		return HTTPServer.class.getSimpleName();
	}

	@Override
	public String getIdentifier() {
		return createIdentifier();
	}

	@Override
	public void reload() {
		dispose();
		SimpleHttpServer server = VsketchMain.getInstance().getServer();
		if (server != null && context == null) {
			context = server.createContext("/post", handler);
		}
	}

	protected void onFilePosted(FileData file) {
		image = cvDecodeImage(cvMat(
				1,
				file.data.length,
				CV_8UC1,
				new BytePointer(file.data)));
	}

	@Override
	public IplImage nextFrame() {
		IplImage nextFrame = image;
		image = null;
		return nextFrame;
	}

	@Override
	public boolean setFrameIndex(long frameIndex) {
		return false;
	}

	@Override
	public long getFrameIndex() {
		return frameIndex;
	}

	@Override
	public long getFrameCount() {
		return -1;
	}

	@Override
	public void dispose() {
		SimpleHttpServer server = VsketchMain.getInstance().getServer();
		if (server != null && context != null) {
			server.removeContext(context);
		}
		context = null;
	}

	@Override
	public String toString() {
		return "";
	}

}
