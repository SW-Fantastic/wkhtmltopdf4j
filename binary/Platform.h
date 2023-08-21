#ifndef C_WKHTML
#define C_WKHTML

#ifdef _WIN32

    #include <Windows.h>
    #include <tchar.h>
    #include "external/windows/jni.h"
    #define API_EXPORT extern "C" __declspec(dllexport)

#elif __APPLE__

    #include <stdio.h>
    #include <dlfcn.h>
    #include "external/osx/jni.h"
    #define API_EXPORT extern "C"

#endif

#endif