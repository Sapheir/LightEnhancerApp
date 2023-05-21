#pragma once
#include "LightEnhanceStrategy.h"

class ResizeStrategy: public LightEnhanceStrategy {
public:
    void enhanceLight(cv::Mat &target, const int &modelImageResolution) override;

    ResizeStrategy(TfLiteInterpreter* interpreter, TfLiteTensor* inputTensor):
        LightEnhanceStrategy{interpreter, inputTensor} {};
};

