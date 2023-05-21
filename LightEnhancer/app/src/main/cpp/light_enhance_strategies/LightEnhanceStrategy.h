#pragma once
#include <opencv2/opencv.hpp>
#include <tensorflow/lite/c/common.h>
#include <tensorflow/lite/c/c_api.h>

class LightEnhanceStrategy {
protected:
    TfLiteInterpreter* interpreter;
    TfLiteTensor* inputTensor;
public:
    virtual void enhanceLight(cv::Mat &target, const int &modelImageResolution) = 0;

    LightEnhanceStrategy() = default;
    LightEnhanceStrategy(TfLiteInterpreter* interpreter, TfLiteTensor* inputTensor):
        interpreter{interpreter}, inputTensor{inputTensor} {};
    virtual ~LightEnhanceStrategy() = default;
};