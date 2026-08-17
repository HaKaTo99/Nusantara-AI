#include <jni.h>
#include <string>
#include <vector>
#include <sstream>
#include <android/log.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <unistd.h>

#define TAG "NusantaraLlamaNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

struct NativeModelHandle {
    std::string model_path;
    int n_threads;
    int n_gpu_layers;
    bool use_mmap;
    int fd;
    void* mmap_addr;
    size_t file_size;
    uint32_t magic;
    uint32_t version;
    uint64_t n_tensors;
    uint64_t n_kv;
    bool is_valid;
};

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_example_domain_ai_native_NativeLlamaBridge_nativeInitModel(
    JNIEnv* env,
    jobject /* this */,
    jstring j_model_path,
    jint n_threads,
    jint n_gpu_layers,
    jboolean use_mmap
) {
    if (!j_model_path) return 0;

    const char* path_str = env->GetStringUTFChars(j_model_path, nullptr);
    if (!path_str) return 0;

    LOGI("Memuat Model GGUF Native dari path: %s (Threads: %d, GPU Layers: %d)", path_str, n_threads, n_gpu_layers);

    auto* handle = new NativeModelHandle();
    handle->model_path = path_str;
    handle->n_threads = n_threads;
    handle->n_gpu_layers = n_gpu_layers;
    handle->use_mmap = use_mmap;
    handle->mmap_addr = nullptr;
    handle->file_size = 0;
    handle->is_valid = false;

    int fd = open(path_str, O_RDONLY);
    if (fd >= 0) {
        handle->fd = fd;
        off_t size = lseek(fd, 0, SEEK_END);
        handle->file_size = (size > 0) ? static_cast<size_t>(size) : 0;
        lseek(fd, 0, SEEK_SET);

        if (use_mmap && handle->file_size > 0) {
            handle->mmap_addr = mmap(nullptr, handle->file_size, PROT_READ, MAP_SHARED, fd, 0);
            if (handle->mmap_addr != MAP_FAILED) {
                LOGI("Memory mapping (mmap) sukses: %zu bytes dialokasikan di virtual address space", handle->file_size);
                handle->is_valid = true;
            } else {
                handle->mmap_addr = nullptr;
            }
        } else {
            handle->is_valid = true;
        }
    } else {
        LOGE("Gagal membuka berkas model pada path: %s", path_str);
    }

    env->ReleaseStringUTFChars(j_model_path, path_str);
    return reinterpret_cast<jlong>(handle);
}

JNIEXPORT void JNICALL
Java_com_example_domain_ai_native_NativeLlamaBridge_nativeFreeModel(
    JNIEnv* /* env */,
    jobject /* this */,
    jlong context_ptr
) {
    if (context_ptr == 0) return;
    auto* handle = reinterpret_cast<NativeModelHandle*>(context_ptr);

    if (handle->mmap_addr && handle->file_size > 0) {
        munmap(handle->mmap_addr, handle->file_size);
        handle->mmap_addr = nullptr;
    }

    if (handle->fd >= 0) {
        close(handle->fd);
        handle->fd = -1;
    }

    delete handle;
    LOGI("Model GGUF native context berhasil dibersihkan dari RAM.");
}

JNIEXPORT jstring JNICALL
Java_com_example_domain_ai_native_NativeLlamaBridge_nativeSampleNextToken(
    JNIEnv* env,
    jobject /* this */,
    jlong context_ptr,
    jstring j_prompt,
    jfloat temperature,
    jfloat top_p
) {
    if (!j_prompt) return env->NewStringUTF("");
    const char* prompt_str = env->GetStringUTFChars(j_prompt, nullptr);

    std::string response = "";
    if (context_ptr != 0) {
        auto* handle = reinterpret_cast<NativeModelHandle*>(context_ptr);
        LOGI("Sampling token untuk prompt: %.30s... (Model: %s)", prompt_str, handle->model_path.c_str());
    }

    env->ReleaseStringUTFChars(j_prompt, prompt_str);
    return env->NewStringUTF(response.c_str());
}

JNIEXPORT jboolean JNICALL
Java_com_example_domain_ai_native_NativeLlamaBridge_nativeIsHardwareAccelerated(
    JNIEnv* /* env */,
    jobject /* this */
) {
#if defined(__aarch64__)
    return JNI_TRUE; // ARM64 NEON Vector Accelerator Available
#else
    return JNI_FALSE;
#endif
}

} // extern "C"
