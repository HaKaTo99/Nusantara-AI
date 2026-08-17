cmake_minimum_required(VERSION 3.22.1)
project("nusantara_llama_jni")

add_library(llama_jni SHARED
    llama_jni.cpp
)

find_library(log-lib log)
find_library(android-lib android)

target_link_libraries(llama_jni
    ${log-lib}
    ${android-lib}
)
