# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)

# Declares and names the project.

project("lightenhancer")

# OpenCV
set(OpenCV_STATIC ON)
set(OpenCV_DIR $ENV{OPENCV_ANDROID}/sdk/native/jni)
find_package(OpenCV REQUIRED)

# Tensorflow Lite
set(Tensorflowlite_DIR $ENV{TFLITE_DIST})
add_library(libtensorflowlite_c SHARED IMPORTED)
add_library(libtensorflowlite_gpu_delegate SHARED IMPORTED)
set_target_properties(libtensorflowlite_c PROPERTIES IMPORTED_LOCATION ${Tensorflowlite_DIR}/libs/android/${ANDROID_ABI}/libtensorflowlite_c.so)
set_target_properties(libtensorflowlite_gpu_delegate PROPERTIES IMPORTED_LOCATION ${Tensorflowlite_DIR}/libs/android/${ANDROID_ABI}/libtensorflowlite_gpu_delegate.so)

# Google Test
set(GTest_DIR $ENV{GTEST})
find_package(googletest REQUIRED CONFIG)
find_package(junit-gtest REQUIRED CONFIG)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        lightenhancer

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).

        light_enhance_strategies/LightEnhanceStrategy.h
        light_enhance_strategies/ResizeStrategy.h light_enhance_strategies/ResizeStrategy.cpp
        light_enhance_strategies/SlidingWindowStrategy.h light_enhance_strategies/SlidingWindowStrategy.cpp

        device_adapters/DeviceMatAdapter.h
        device_adapters/AndroidMatAdapter.h device_adapters/AndroidMatAdapter.cpp

        LightEnhancerModel.h LightEnhancerModel.cpp
        native-lib.cpp)

target_include_directories(
        lightenhancer PRIVATE
        ${Tensorflowlite_DIR}/include
)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

find_library(android-lib android)
find_library(jnigraphics-lib jnigraphics)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        lightenhancer

        libtensorflowlite_c
        libtensorflowlite_gpu_delegate
        ${OpenCV_LIBS}
        ${android-lib}
        ${jnigraphics-lib}
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})

# Testing library
add_library(app_tests SHARED enhancer_test.cpp)
target_include_directories(app_tests PRIVATE
        ${Tensorflowlite_DIR}/include)
target_link_libraries(app_tests
        PRIVATE
        lightenhancer
        googletest::gtest
        googletest::gmock
        junit-gtest::junit-gtest)