LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_LDLIBS    := -llog
LOCAL_MODULE    := image-processing
LOCAL_SRC_FILES := image-processing.c


include $(BUILD_SHARED_LIBRARY)