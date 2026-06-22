#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_jayce_vexis_client_NativeLib_stringFromJNI(JNIEnv* env, jobject) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}