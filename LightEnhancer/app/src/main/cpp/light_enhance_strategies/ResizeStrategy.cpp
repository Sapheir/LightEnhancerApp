#include "ResizeStrategy.h"

void ResizeStrategy::enhanceLight(cv::Mat &target, const int &modelImageResolution) {
    cvtColor(target, target, cv::COLOR_RGBA2RGB);
    cv::Size original_size = target.size();
    int channels = 3;
    resize(target, target, cv::Size2f(modelImageResolution, modelImageResolution),
           0, 0, cv::INTER_CUBIC);
    target.convertTo(target, CV_32FC1, 1.0/255.0);
    float* dst = inputTensor->data.f;
    memcpy(dst, (float*)target.data,
           sizeof(float) * modelImageResolution * modelImageResolution * channels);

    if (TfLiteInterpreterInvoke(interpreter) != kTfLiteOk) {
        printf("Error invoking detection model");
    }

    const TfLiteTensor* m_output_tensor = TfLiteInterpreterGetOutputTensor(interpreter, 0);
    cv::Mat output_target(modelImageResolution, modelImageResolution, CV_32FC3, m_output_tensor->data.f);
    output_target *= 255.0;
    output_target.convertTo(output_target, CV_8UC3);
    resize(output_target, target, original_size, 0, 0, cv::INTER_CUBIC);
}
