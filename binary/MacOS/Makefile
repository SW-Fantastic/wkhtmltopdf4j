main.o: main.cpp
		g++ -c main.cpp -o main.o

library.o: library.cpp 
		g++ -c library.cpp -o library.o

library: library.o
		g++ library.o -lwkhtmltox -L. -fPIC -shared -o libWkHtmlPDF.dylib

test: main.o
		g++ main.o -lwkhtmltox -L. -o main

