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

## 如何构建

native类库位于binary内部，如果需要从源码构建，请按照如下方法进行：

### Mac OS 

安装XCode，以及make，gcc，g++等编译器。

进入binary/MacOS文件夹，执行如下命令。
```bash
make -f Makefile library
```

### windows 

首先安装MinGW 64，然后将MinGW的bin目录放在系统的环境变量Path中。

进入binary/windows文件夹，执行make.bat
```bash
./make
```

## How to build

native c++ source code in the binary folder。

### Mac OS

open the native/Mac OS，and run the makefile ：
```bash 
make -f Makefile library
```

### windows
open the native/windows and run make.bat
```bash
make
```

## 局限
本组件只能单线程转换PDF，一个一个的被HTML或者String的执行转换，不能多线程同时操作，因此效率比较有限，
这是因为Qt的渲染只能在Qt的ApplicationThread运行。

由于使用了线程池，所以在关闭应用的时候需要显式的使用`System.exit`，或者
调用`WKHtmlPDFConverter.close`

## limitation
the wrapper working on a single thread，because render operation must be call on
Qt Application thread。

thread pool used in WKHtmlPDFConverter, 
please call `System.exit`, or `WKHtmlPDFConverter.close` when 
you want to exit the application