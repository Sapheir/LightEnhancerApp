#pragma once
#include "DeviceMatAdapter.h"
#include <android/asset_manager_jni.h>
#include <android/native_window_jni.h>
#include <android/asset_manager.h>
#include <android/bitmap.h>

class AndroidMatAdapter: public DeviceMatAdapter {
private:
    JNIEnv *env;
    jobject &bitmap;

public:
    AndroidMatAdapter(JNIEnv *env, jobject &bitmap): env{env}, bitmap{bitmap} {}

    void matToDeviceType(const cv::Mat &src) override;
    void deviceTypeToMat(cv::Mat &dst) override;
};
