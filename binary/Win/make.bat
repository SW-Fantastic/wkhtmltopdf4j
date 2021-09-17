g++ -c main.cpp -o main.o
g++ main.o -lwkhtmltox -L. -o main.exe
g++ -c library.cpp -o library.o
g++ library.o -lwkhtmltox -L. -fPIC -shared -o libWkHtmlPDF.dll