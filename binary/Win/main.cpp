#include<iostream>

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

#include"headers/pdf.h"


void progress_changed(wkhtmltopdf_converter* c, int p){
	printf("%3d%%\r", p);	
	fflush(stdout);
}

void phase_changed(wkhtmltopdf_converter* c){
	int phase = wkhtmltopdf_current_phase(c);
	printf("%s\n", wkhtmltopdf_phase_description(c,phase));	
}

void error(wkhtmltopdf_converter* c, const char* msg){
	fprintf(stderr, "Error: %s\n", msg);	
}

void warning(wkhtmltopdf_converter* c, const char* msg){
	fprintf(stderr, "Warning: %s\n", msg);	
}

/**
 * 
 * argv[0] 应用本身的名称
 * argv[1] 文件的绝对路径
 * argv[2] 文件的相对路径 
 * 
 */
int main(int argc, char** argv) {
    std::cout << "Started" << std::endl;

	wkhtmltopdf_global_settings* gs;
	wkhtmltopdf_object_settings* os;
	wkhtmltopdf_converter* c;
	
	wkhtmltopdf_init(false);
	
	gs = wkhtmltopdf_create_global_settings();
	wkhtmltopdf_set_global_setting(gs, "out", argv[2]);
	
	os = wkhtmltopdf_create_object_settings();
    std::string prefix = "file://";
    std::string location = argv[1];
	wkhtmltopdf_set_object_setting(os, "page", (prefix + location).c_str());
	
	c = wkhtmltopdf_create_converter(gs);
	
	wkhtmltopdf_set_progress_changed_callback(c, progress_changed);
	wkhtmltopdf_set_phase_changed_callback(c, phase_changed);
	wkhtmltopdf_set_error_callback(c, error);
	wkhtmltopdf_set_warning_callback(c, warning);
	
	wkhtmltopdf_add_object(c, os, NULL);
	
	if( !wkhtmltopdf_convert(c) )
		fprintf(stderr, "Convertion failed!");
		
	printf("httpErrorCode: %d\n", wkhtmltopdf_http_error_code(c));
	
	wkhtmltopdf_destroy_converter(c);
	
	wkhtmltopdf_deinit();
    return 0;
}

