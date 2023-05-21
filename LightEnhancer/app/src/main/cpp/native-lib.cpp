#include <jni.h>
#include <android/asset_manager.h>
#include "LightEnhancerModel.h"
#include "device_adapters/AndroidMatAdapter.h"
#include "light_enhance_strategies/ResizeStrategy.h"
#include "light_enhance_strategies/SlidingWindowStrategy.h"

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_lightenhancer_LightEnhancer_initEnhancer(JNIEnv* env, jobject p_this, jobject assetManager, jstring modelPath) {
    char *buffer = nullptr;
    long size = 0;

    if (!(env->IsSameObject(assetManager, nullptr))) {
        AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
        const char *nativeModelPath = env->GetStringUTFChars(modelPath, nullptr);

        env->ReleaseStringUTFChars(modelPath, nativeModelPath);
        AAsset *asset = AAssetManager_open(mgr, nativeModelPath, AASSET_MODE_UNKNOWN);
        assert(asset != nullptr);

        size = AAsset_getLength(asset);
        buffer = (char *) malloc(sizeof(char) * size);
        AAsset_read(asset, buffer, size);
        AAsset_close(asset);
    }

    auto res = (jlong) new LightEnhancerModel(buffer, size);
    free(buffer);
    return res;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_lightenhancer_LightEnhancer_destroyEnhancer(JNIEnv* env, jobject p_this, jlong enhancerAddress) {
    if (enhancerAddress)
        delete (LightEnhancerModel*) enhancerAddress;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_lightenhancer_LightEnhancer_enhance(JNIEnv* env, jobject p_this, jobject bitmapTarget, jint modelResolution, jlong enhancerAddress) {
    try {
        std::unique_ptr<DeviceMatAdapter> androidMatAdapter = std::make_unique<AndroidMatAdapter>(env, bitmapTarget);
        cv::Mat target;
        androidMatAdapter->deviceTypeToMat(target);

        auto* enhancer = (LightEnhancerModel*) enhancerAddress;
        enhancer->enhanceLight(target, modelResolution);

        androidMatAdapter->matToDeviceType(target);
    } catch(const cv::Exception& e) {
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_lightenhancer_LightEnhancer_enhanceFullResolution(JNIEnv* env, jobject p_this, jobject bitmapTarget, jint modelResolution, jlong enhancerAddress) {
    try {
        std::unique_ptr<DeviceMatAdapter> androidMatAdapter = std::make_unique<AndroidMatAdapter>(env, bitmapTarget);
        cv::Mat target;
        androidMatAdapter->deviceTypeToMat(target);

        auto* enhancer = (LightEnhancerModel*) enhancerAddress;
        std::unique_ptr<LightEnhanceStrategy> slidingWindowStrategy = std::make_unique<SlidingWindowStrategy>(
                enhancer->getInterpreter(), enhancer->getInputTensor());
        enhancer->setStrategy(slidingWindowStrategy);
        enhancer->enhanceLight(target, modelResolution);

        androidMatAdapter->matToDeviceType(target);
    } catch(const cv::Exception& e) {
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    }
}
