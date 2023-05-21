#include "SlidingWindowStrategy.h"

void SlidingWindowStrategy::enhanceLight(cv::Mat &target, const int &modelImageResolution) {
    cvtColor(target, target, cv::COLOR_RGBA2RGB);
    cv::Size original_size = target.size();
    cv::Mat output_img(original_size, CV_8UC3);
    int height = modelImageResolution, width = modelImageResolution, channels = 3;

    int x1 = 0, x2 = width - 1, y1 = 0, y2 = height - 1;
    for (int i = 1; i <= original_size.height / height + (original_size.height % height > 0); i++) {
        for (int j = 1; j <= original_size.width / width + (original_size.width % width > 0); j++) {

            cv::Mat roi = target(cv::Rect(x1, y1, width, height)).clone();
            roi.convertTo(roi, CV_32F, 1 / 255.0);
            float* dst = inputTensor->data.f;
            memcpy(dst, (float*)roi.data, sizeof(float) * height * width * channels);

            if (TfLiteInterpreterInvoke(interpreter) != kTfLiteOk) {
                printf("Error invoking detection model");
            }

            const TfLiteTensor* m_output_tensor = TfLiteInterpreterGetOutputTensor(interpreter, 0);
            cv::Mat output_image(height, width, CV_32FC3, m_output_tensor->data.f);
            output_image *= 255.0;
            output_image.convertTo(output_image, CV_8UC3);
            output_image.copyTo(output_img(cv::Rect(x1, y1, width, height)));
            if (x2 + width >= original_size.width) {
                x2 = original_size.width - 1;
                x1 = x2 - width + 1;
            }
            else {
                x1 += width;
                x2 += width;
            }
        }
        if (y2 + height >= original_size.height) {
            y2 = original_size.height - 1;
            y1 = y2 - height + 1;
        }
        else {
            y1 += height;
            y2 += height;
        }
        x1 = 0;
        x2 = width - 1;
    }
    target = output_img;
}
