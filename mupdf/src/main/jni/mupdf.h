
#include <jni.h>
#include <time.h>
#include <pthread.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#ifdef NDK_PROFILER
#include "prof.h"
#endif

#include "mupdf/include/mupdf/fitz.h"
#include "mupdf/include/mupdf/pdf.h"

#define JNI_MuPDFCore(A) Java_com_jaky_mupdf_core_MuPDFCore_ ## A
#define PACKAGENAME "com/jaky/mupdf/core"

//Log输出
#define LOG_TAG "JNICALL_libmupdf"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGT(...) __android_log_print(ANDROID_LOG_INFO,"alert",__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

/* Enable to log rendering times (render each frame 100 times and time) */
#undef TIME_DISPLAY_LIST

#define MAX_SEARCH_HITS (500)
#define NUM_CACHE (3)
#define STRIKE_HEIGHT (0.375f)
#define UNDERLINE_HEIGHT (0.075f)
#define LINE_THICKNESS (0.07f)
#define INK_THICKNESS (4.0f)
#define SMALL_FLOAT (0.00001)
#define PROOF_RESOLUTION (300)

//======================以下是自定义的=======================

//#define SUPPORT_GPROOF 1

#include "com_jaky_mupdf_core_MuPDFCore.h"
#include "com_jaky_mupdf_core_MuPDFCore_Cookie.h"