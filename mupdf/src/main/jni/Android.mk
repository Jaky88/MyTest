LOCAL_PATH := $(call my-dir)
TOP_LOCAL_PATH := $(LOCAL_PATH)

MUPDF_ROOT := mupdf


#包含mk文件
ifdef NDK_PROFILER
include android-ndk-profiler.mk
endif

include $(TOP_LOCAL_PATH)/Core.mk
include $(TOP_LOCAL_PATH)/ThirdParty.mk

include $(CLEAR_VARS)

#包含文件
LOCAL_C_INCLUDES := \
	jni/andprof \
	$(MUPDF_ROOT)/include \
	$(MUPDF_ROOT)/source/fitz \
	$(MUPDF_ROOT)/source/pdf

#$(MUPDF_ROOT)/platform/java

LOCAL_CFLAGS := -DHAVE_ANDROID

#编译生成的库名
LOCAL_MODULE    := mupdf_java
#库源文件
LOCAL_SRC_FILES := mupdf.c
#依赖静态库----核心库: mupdfcore和三方库: mupdfthirdparty
LOCAL_STATIC_LIBRARIES := mupdfcore mupdfthirdparty

#其他
ifdef NDK_PROFILER
LOCAL_CFLAGS += -pg -DNDK_PROFILER
LOCAL_STATIC_LIBRARIES += andprof
endif

ifdef SUPPORT_GPROOF
LOCAL_CFLAGS += -DSUPPORT_GPROOF
endif

LOCAL_LDLIBS    := -lm -llog -ljnigraphics

ifdef SSL_BUILD
LOCAL_LDLIBS	+= -L$(MUPDF_ROOT)/thirdparty/openssl/android -lcrypto -lssl
endif

include $(BUILD_SHARED_LIBRARY)
