package org.swdc.whtmltopdf;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;

public class WKHtmlPDFConverter {

	private static ExecutorService executor = new ThreadPoolExecutor(1,1,0, TimeUnit.DAYS,new LinkedBlockingQueue<>(),t-> {
		Thread thread = new Thread(t);
		thread.setName("WKHtmlPDF-Worker");
		thread.setDaemon(true);
		return thread;
	});

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

	public File convertFromUrl(String urlString, File target) {
		try {
			return executor.submit(() -> {
				synchronized (WKHtmlPDFConverter.class) {
					try {
						String dist = target.getAbsolutePath();
						if(target.exists()) {
							target.delete();
						}
						generatePDF(urlString, dist);
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
	
	private native void generatePDF(String absolutePath,String outputPath);


	/**
	 * 结束线程池，转换将会不可用.
	 * 本项目使用单例线程池，并且为守护线程，因此手动调用本方法是没有必要的，
	 * 也容易造成未知的问题。
	 */
	public static void close() {
		executor.shutdown();
	}

}
