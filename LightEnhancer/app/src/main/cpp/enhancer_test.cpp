#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include "LightEnhancerModel.h"
#include "light_enhance_strategies/ResizeStrategy.h"
#include "light_enhance_strategies/SlidingWindowStrategy.h"
#include "device_adapters/AndroidMatAdapter.h"

class MockLightEnhanceStrategy : public LightEnhanceStrategy {
public:
    MOCK_METHOD(void, enhanceLight, (cv::Mat&, const int&), (override));
};

class LightEnhanceStrategyTest : public ::testing::Test {
protected:
    TfLiteInterpreter* interpreter = nullptr;
    TfLiteTensor* inputTensor = nullptr;
    cv::Mat target;
};

TEST_F(LightEnhanceStrategyTest, testEnhanceLight) {
    MockLightEnhanceStrategy strategy;

    cv::Mat inputImage = cv::Mat::zeros(cv::Size(500, 500), CV_32F);
    int modelImageResolution = 500;
    strategy.enhanceLight(inputImage, modelImageResolution);

    ASSERT_EQ(inputImage.size(), cv::Size(modelImageResolution, modelImageResolution));
}

class MockDeviceMatAdapter : public DeviceMatAdapter {
public:
    MOCK_METHOD(void, matToDeviceType, (const cv::Mat &src), (override));
    MOCK_METHOD(void, deviceTypeToMat, (cv::Mat &dst), (override));
};

class DeviceMatAdapterTest : public ::testing::Test {
protected:
    cv::Mat src, dst;
    JNIEnv* env = nullptr;
    jobject bitmap{};

    void SetUp() override {
        src = cv::Mat::zeros(cv::Size(500, 500), CV_8UC4);
    }
};

TEST_F(DeviceMatAdapterTest, TestAndroidMatAdapter) {
    MockDeviceMatAdapter adapter;

    adapter.matToDeviceType(src);
    adapter.deviceTypeToMat(dst);

}