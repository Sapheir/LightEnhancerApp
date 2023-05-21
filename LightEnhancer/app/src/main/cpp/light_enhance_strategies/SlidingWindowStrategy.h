#pragma once
#include "LightEnhanceStrategy.h"

class SlidingWindowStrategy: public LightEnhanceStrategy {
public:
    void enhanceLight(cv::Mat &target, const int &modelImageResolution) override;

    SlidingWindowStrategy(TfLiteInterpreter* interpreter, TfLiteTensor* inputTensor):
        LightEnhanceStrategy{interpreter, inputTensor} {};
};
