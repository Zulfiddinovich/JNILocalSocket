//
// Created by Zukhriddin Kamolov on 05-Apr-24.
//
#include <jni.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <android/log.h>
#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>


#define LOG    "javas_cppc" // This is the log of the LOGD(...)
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG,__VA_ARGS__)  // LOGD
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG,__VA_ARGS__)   // LOGI
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG,__VA_ARGS__)   // LOGW
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG,__VA_ARGS__)  // LOGE
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG,__VA_ARGS__)  // LOGF

static int isDoing = 0;
static const char* name = "JavaS_CppC_Address";

/**
 * this connects a client to the server socket and starts
 * */
static int send_message_to_java_server(const char *msg) {
    LOGD("send_message_to_java_server start \n");
    int localsocket, len;
    struct sockaddr_un remote;

    if ((localsocket = socket(AF_UNIX, SOCK_STREAM, 0)) == -1) {
        LOGE("socket error \n");
        return -1;
    }

    remote.sun_path[0] = '\0'; /* abstract namespace */
    strcpy(remote.sun_path + 1, name);
    remote.sun_family = AF_UNIX;
    int nameLen = strlen(name);
    len = 1 + nameLen + offsetof(struct sockaddr_un, sun_path);

    if (connect(localsocket, (struct sockaddr *) &remote, len) == -1) {
        LOGE("connect error \n");
        return -1;
    }

    if (send(localsocket, msg, strlen(msg), 0) == -1) {
        LOGE("send error \n");
        return -1;
    }

    close(localsocket);
    LOGD("send_message_to_java_server complete\n");
    return 0;
}

/**
 * This is new Thread method for long running message triggering logic
 * */
static void *threadMessageJava(void *msg){
    const char *message = msg;
    int counter = 0;

    while(isDoing){
        counter++;                                          // increment
        send_message_to_java_server(message);          // send a message
        usleep(1000000);                         // delay
    }
    LOGD("threadMessageJava complete");
}


/**
 * This starts a messaging
 * */
JNIEXPORT void JNICALL
Java_etsoft_localsocket_javas_1cppc_JavaS_1CppC_1Helper_startMessagingFromCpp(JNIEnv *env, jobject thiz) {
    const char *message = "hello";
    isDoing = 1;

    // creating periodical sending message thread method
    pthread_t thread_id;
    pthread_create(&thread_id, NULL, threadMessageJava, message);
}

/**
 * This stops the messaging
 * */
JNIEXPORT void JNICALL
Java_etsoft_localsocket_javas_1cppc_JavaS_1CppC_1Helper_stopMessagingFromCpp(JNIEnv *env, jobject thiz) {
    isDoing = 0;
}