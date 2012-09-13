LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_PACKAGE_NAME := com.unidevel.sleep
LOCAL_CERTIFICATE := platform
include $(BUILD_JAVA_LIBRARY)
