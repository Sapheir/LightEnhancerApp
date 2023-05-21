#include "LightEnhancerModel.h"
#include "light_enhance_strategies/ResizeStrategy.h"

LightEnhancerModel::LightEnhancerModel(const char *enhancerModel, long modelSize) {
    initDetectionModel(enhancerModel, modelSize);
}

void LightEnhancerModel::initDetectionModel(const char *enhancerModel, long modelSize) {

    // Copy to model bytes as the caller might release this memory while we need it (EXC_BAD_ACCESS error on ios)
    config.modelBytes = (char*)malloc(modelSize);
    memcpy(config.modelBytes, enhancerModel, modelSize);
    config.model = TfLiteModelCreate(config.modelBytes, modelSize);

    if (!config.model) {
        printf("Failed to load model");
        return;
    }

    // Build the interpreter
    config.interpreterOptions = TfLiteInterpreterOptionsCreate();
    TfLiteInterpreterOptionsSetNumThreads(config.interpreterOptions, 1);

    // Create the interpreter.
    config.interpreter = TfLiteInterpreterCreate(config.model, config.interpreterOptions);
    if (!config.interpreter) {
        printf("Failed to create interpreter");
        return;
    }

    // Use GPU
    config.gpuDelegate = TfLiteGpuDelegateV2Create(nullptr);
    TfLiteInterpreterOptionsAddDelegate(config.interpreterOptions, config.gpuDelegate);

    // Allocate tensor buffers.
    if (TfLiteInterpreterAllocateTensors(config.interpreter) != kTfLiteOk) {
        printf("Failed to allocate tensors!");
        return;
    }

    // Find input tensors.
    if (TfLiteInterpreterGetInputTensorCount(config.interpreter) != 1) {
        printf("Detection model graph needs to have 1 and only 1 input!");
        return;
    }

    config.inputTensor = TfLiteInterpreterGetInputTensor(config.interpreter, 0);
    if (config.inputTensor->type != kTfLiteFloat32) {
        printf("Detection model input should be kTfLiteFloat32!");
        return;
    }

    this->enhanceStrategy = std::make_unique<ResizeStrategy>(config.interpreter, config.inputTensor);
}

void LightEnhancerModel::setStrategy(std::unique_ptr<LightEnhanceStrategy> &strategy) {
    this->enhanceStrategy = std::move(strategy);
}

void LightEnhancerModel::enhanceLight(cv::Mat &target, const int &modelImageResolution) {
    enhanceStrategy->enhanceLight(target, modelImageResolution);
}

TfLiteInterpreter* LightEnhancerModel::getInterpreter() const {
    return config.interpreter;
}

TfLiteTensor* LightEnhancerModel::getInputTensor() const {
    return config.inputTensor;
}

LightEnhancerModel::~LightEnhancerModel() {
    if (config.interpreter)
        TfLiteInterpreterDelete(config.interpreter);
    if (config.gpuDelegate)
        TfLiteGpuDelegateV2Delete(config.gpuDelegate);
    if (config.interpreterOptions)
        TfLiteInterpreterOptionsDelete(config.interpreterOptions);
    if (config.model)
        TfLiteModelDelete(config.model);
    if (config.modelBytes)
        free(config.modelBytes);
}


