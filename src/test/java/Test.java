import org.swdc.libloader.PlatformLoader;
import org.swdc.whtmltopdf.WKHtmlPDF;
import org.swdc.whtmltopdf.WKHtmlPDFConverter;

import java.io.File;

public class Test {

	public static void main(String[] args) {

		WKHtmlPDF.load(new File("platforms"));

		WKHtmlPDFConverter conv = new WKHtmlPDFConverter();
		WKHtmlPDFConverter conv2 = new WKHtmlPDFConverter();
		conv.setOnProgress(i -> {
			System.err.println("Progress is " + i);
		});
		conv.setOnMesssage(e -> {
			System.err.println("Message : " + e);;
		});

		conv.convert(new File("test.html"), new File("test.pdf"));
		conv2.convert("<h1>Hello</h1>", new File("Demo.pdf"));
		conv2.convertFromUrl("https://wkhtmltopdf.org/",new File("URL.pdf"));
	}
	
}
