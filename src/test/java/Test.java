import org.swdc.libloader.PlatformLoader;
import org.swdc.whtmltopdf.WKHtmlPDFConverter;

import java.io.File;

public class Test {

	public static void main(String[] args) {

		PlatformLoader loader = new PlatformLoader();
		loader.load(new File("dist/WKHtmlPDF.xml"));

		WKHtmlPDFConverter conv = new WKHtmlPDFConverter();
		conv.setOnProgress(i -> {
			System.err.println("Progress is " + i);
		});
		conv.setOnMesssage(e -> {
			System.err.println("Message : " + e);;
		});
		conv.convert(new File("test.html"), new File("test.pdf"));
		conv.convert("<h1>Hello</h1>", new File("Demo.pdf"));
	}
	
}
