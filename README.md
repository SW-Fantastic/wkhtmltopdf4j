# WhtmlToPDF java binding

## 摘要

这个是WkHtmltoPDF的wrapper，本wrapper通过java的jni技术编写，
不需要使用命令行，不需要额外的安装。

使用之前，请首先clone [我的StandAlone工程组](https://github.com/SW-Fantastic/standalone)，
并且在本地的maven中安装libloader工程。

下面是加载本地类库以及Wrapper的使用的例子。


## English

this is a wrapper of WhtmlToPDF native library with java jni.

we can use this library to convert any html file to a pdf.

by the time you need it , clone my [standlongs project](https://github.com/SW-Fantastic/standalone) first,
install the libloader to your native maven repository

this is a test code to show how can you use it: 
```java
public class Test {

	public static void main(String[] args) {

	    // Load Narive libs
		PlatformLoader loader = new PlatformLoader();
		loader.load(new File("dist/WKHtmlPDF.xml"));

		// converter is available now
		WKHtmlPDFConverter conv = new WKHtmlPDFConverter();
		// you can put logger here
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
```