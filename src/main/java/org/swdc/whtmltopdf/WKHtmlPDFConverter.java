package org.swdc.whtmltopdf;

import org.swdc.libloader.PlatformLoader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class WKHtmlPDFConverter {

	private static ExecutorService executor = new ThreadPoolExecutor(1,1,0, TimeUnit.DAYS,new LinkedBlockingQueue<>());

	private WKMessageListener msg;
	
	private WKProcessListener progress;

	public WKHtmlPDFConverter() {
	}

	public void setOnMesssage(WKMessageListener msg) {
		this.msg = msg;
	}
	
	public void setOnProgress(WKProcessListener processListener) {
		this.progress = processListener;
	}
	
	public File convert(String source, File target) {
		try {
			Path tempFile = Files.createTempFile("wkhtml", "PDf.html");
			OutputStream out = Files.newOutputStream(tempFile);
			out.write(source.getBytes(StandardCharsets.UTF_8));
			out.close();

			File result = convert(tempFile.toFile(), target);
			Files.delete(tempFile);
			return result;

		} catch (Exception e) {
			throw new RuntimeException("Faild to convert string",e);
		}
	}
	
	public File convert(File source, File target) {
			try {
				return executor.submit(() -> {
					synchronized (WKHtmlPDFConverter.class) {
						try {
							String path = "file:///" + source.getAbsolutePath();
							String dist = target.getAbsolutePath();
							if(target.exists()) {
								target.delete();
							}
							generatePDF(path, dist);
							File file = new File(dist);
							if(file.exists()) {
								return file;
							}
							return null;
						} catch (Exception e) {
							throw new RuntimeException("Can not open source file",e);
						}
					}
				}).get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
	}
	
	private void onMessage(String errMessage) {
		if (this.msg!= null) {
			this.msg.onMessage(errMessage);
		}
	}
	
	private void onProgress(int prog) {
		if(this.progress != null) {
			this.progress.onProgress(prog);
		}
	}
	
	public native void generatePDF(String absolutePath,String outputPath);


	public static void close() {
		executor.shutdown();
	}

}
