#include"headers/pdf.h"
#include<iostream>
#include<cstring>

#include "java/org_swdc_whtmltopdf_WKHtmlPDFConverter.h"


JavaVM* javaVm;

// 这里的本地代码，通过java来保证它会运行在同一个转换任务里面，
// 不会出现同时转换多个文件的情况，java同样保证本组件会运行在同一个线程上面，
// 在同一个线程执行转换任务，以避免“QObject::startTimer: Timers cannot be started from another thread”
// 和类似的异常。

jobject javaInstance;

jstring asJavaString(JNIEnv* env, const char* pat){
    //定义java String类 strClass
    jclass strClass = (env)->FindClass("java/lang/String");
    //获取java String类方法String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    //建立byte数组
    jbyteArray bytes = (env)->NewByteArray((jsize)strlen(pat));
    //将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, (jsize)strlen(pat), (jbyte*)pat);
    //设置String, 保存语言类型,用于byte数组转换至String时的参数
    jstring encoding = (env)->NewStringUTF("GB2312");
    //将byte数组转换为java String,并输出
    return (jstring)(env)->NewObject(strClass, ctorID, bytes, encoding);
}


jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_8) != JNI_OK) {
        return -1;
    }

    javaVm = vm;

    return JNI_VERSION_1_8;
}

jint JNI_OnUnload() {
    return 0;
}

void progress_changed(wkhtmltopdf_converter* c, int p){
    if (javaInstance == NULL){
        return;
    }
    
    JNIEnv* env = NULL;
    int status = javaVm->AttachCurrentThread((void**)&env,NULL);

    if (status < 0) {
        return;
    }
    jclass converter = env->FindClass("org/swdc/whtmltopdf/WKHtmlPDFConverter");
    jmethodID method = env->GetMethodID(converter,"onProgress","(I)V");

    env->CallVoidMethod(javaInstance,method,p);
}

void phase_changed(wkhtmltopdf_converter* c){

    if (javaInstance == NULL){
        return;
    }
    
    JNIEnv* env = NULL;
    int status = javaVm->AttachCurrentThread((void**)&env,NULL);

    if (status < 0) {
        return;
    }
    jclass converter = env->FindClass("org/swdc/whtmltopdf/WKHtmlPDFConverter");
    jmethodID method = env->GetMethodID(converter,"onMessage","(Ljava/lang/String;)V");

	int phase = wkhtmltopdf_current_phase(c);
    env->CallVoidMethod(javaInstance,method,asJavaString(env,wkhtmltopdf_phase_description(c,phase)));

}

void error(wkhtmltopdf_converter* c, const char* msg){
     if (javaInstance == NULL){
        return;
    }
    
    JNIEnv* env = NULL;
    int status = javaVm->AttachCurrentThread((void**)&env,NULL);

    if (status < 0) {
        return;
    }
    jclass converter = env->FindClass("org/swdc/whtmltopdf/WKHtmlPDFConverter");
    jmethodID method = env->GetMethodID(converter,"onMessage","(Ljava/lang/String;)V");

    env->CallVoidMethod(javaInstance,method,asJavaString(env,msg));

}


void warning(wkhtmltopdf_converter* c, const char* msg){

   if (javaInstance == NULL){
        return;
    }
    
    JNIEnv* env = NULL;
    int status = javaVm->AttachCurrentThread((void**)&env,NULL);

    if (status < 0) {
        return;
    }
    jclass converter = env->FindClass("org/swdc/whtmltopdf/WKHtmlPDFConverter");
    jmethodID method = env->GetMethodID(converter,"onMessage","(Ljava/lang/String;)V");

    env->CallVoidMethod(javaInstance,method,asJavaString(env,msg));

}

/*
 * Class:     org_swdc_whtmltopdf_WKHtmlPDFConverter
 * Method:    generatePDF
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_swdc_whtmltopdf_WKHtmlPDFConverter_generatePDF
  (JNIEnv * jenv, jobject self, jstring source, jstring dist) {

    javaInstance = self;

	wkhtmltopdf_global_settings* gs;
	wkhtmltopdf_object_settings* os;
	wkhtmltopdf_converter* c;
	
	wkhtmltopdf_init(false);
	
	gs = wkhtmltopdf_create_global_settings();
	wkhtmltopdf_set_global_setting(gs, "out", jenv->GetStringUTFChars(dist,JNI_FALSE));
	
	os = wkhtmltopdf_create_object_settings();
	wkhtmltopdf_set_object_setting(os, "page", jenv->GetStringUTFChars(source,JNI_FALSE));
	
	c = wkhtmltopdf_create_converter(gs);
	
	wkhtmltopdf_set_progress_changed_callback(c, progress_changed);
	wkhtmltopdf_set_phase_changed_callback(c, phase_changed);
	wkhtmltopdf_set_error_callback(c, error);
	wkhtmltopdf_set_warning_callback(c, warning);
	
	wkhtmltopdf_add_object(c, os, NULL);
	
	if( !wkhtmltopdf_convert(c) )
		fprintf(stderr, "Convertion failed!");
		
	printf("Error: %d\n", wkhtmltopdf_http_error_code(c));
	
	wkhtmltopdf_destroy_converter(c);
	
	wkhtmltopdf_deinit();
  }