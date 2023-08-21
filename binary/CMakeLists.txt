cmake_minimum_required(VERSION 3.12)
project(WkHtmlPDF)

set (CMAKE_CONFIGURATION_TYPES "Release")

link_directories("dependencies")
add_library(WkHtmlPDF SHARED library.cpp)
target_link_libraries(WkHtmlPDF -lwkhtmltox)
