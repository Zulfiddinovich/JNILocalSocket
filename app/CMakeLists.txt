cmake_minimum_required(VERSION 3.4.1)

add_library( jni_socket

             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/javas_cppc.c
             src/main/cpp/cs_javac.c
)

target_link_libraries(jni_socket
        log )