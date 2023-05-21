#pragma once
#include <opencv2/opencv.hpp>

class DeviceMatAdapter {
public:
    virtual void matToDeviceType(const cv::Mat &src) = 0;
    virtual void deviceTypeToMat(cv::Mat &dst) = 0;

    virtual ~DeviceMatAdapter() = default;
};
