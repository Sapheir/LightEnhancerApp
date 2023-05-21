#pragma once
#include <opencv2/opencv.hpp>
#include <tensorflow/lite/c/common.h>
#include <tensorflow/lite/c/c_api.h>
#include <tensorflow/lite/delegates/nnapi/nnapi_delegate_c_api.h>
#include <tensorflow/lite/delegates/gpu/delegate.h>
#include "light_enhance_strategies/LightEnhanceStrategy.h"
#include "device_adapters/DeviceMatAdapter.h"

struct TfModelConfig {
    char* modelBytes{};
    TfLiteModel* model{};
    TfLiteInterpreterOptions* interpreterOptions{};
    TfLiteInterpreter* interpreter{};
    TfLiteDelegate* gpuDelegate{};
    TfLiteTensor* inputTensor{};
};

class LightEnhancerModel {
private:
    TfModelConfig config{};

    std::unique_ptr<LightEnhanceStrategy> enhanceStrategy;

    void initDetectionModel(const char* enhancerModel, long modelSize);

public:
    LightEnhancerModel(const char* enhancerModel, long modelSize);
    LightEnhancerModel() = default;
    ~LightEnhancerModel();

    void setStrategy(std::unique_ptr<LightEnhanceStrategy> &strategy);
    TfLiteInterpreter* getInterpreter() const;
    TfLiteTensor* getInputTensor() const;
    void enhanceLight(cv::Mat &target, const int &modelImageResolution);
};
