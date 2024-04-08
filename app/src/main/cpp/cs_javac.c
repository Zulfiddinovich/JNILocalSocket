//
// Created by Zukhriddin Kamolov on 06-Apr-24.
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
#include <linux/in.h>
#include <sys/endian.h>


#define LOG    "cs_javac" // This is the log of the LOGD(...)
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG,__VA_ARGS__)  // LOGD
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG,__VA_ARGS__)   // LOGI
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG,__VA_ARGS__)   // LOGW
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG,__VA_ARGS__)  // LOGE
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG,__VA_ARGS__)  // LOGF

static int isDoing = 0;
static const char* name = "CppS_JavaC_Address";

int mClient = -1;
int mServer = -1;

/**
 * This is Thread function for long running message sending
 * */
static int send_message_to_java_client(const char *msg) {
    if (mClient > 0){
        if (send(mServer, &msg, strlen(msg), 0) != -1){
            LOGD("send_message_to_java_client \n");
        } else
            LOGE("send_message_to_java_client: error\n");
    }
    return 0;
}

/**
 * This is new Thread function for long running message triggering logic
 * */
static void *threadMessage(void *msg){
    const char *message = msg;
    int counter = 0;

    while(isDoing){
        counter++;                                          // increment
        send_message_to_java_client(message);          // send a message
        usleep(1000000);                         // delay
    }
    LOGD("threadMessageJava complete");
}

/**
 * This creates a C++ socket server and starts automatic message sending
 * */
void *create_server(void *agrs){
    LOGI("server starting");
    int server_fd, new_client;
    ssize_t valread;
    char buffer[1024] = {0};

    // used - https://www.toptip.ca/2013/01/unix-domain-socket-with-abstract-socket.html?m=1  =============================
    struct sockaddr_un server_address, client_address;
    int len;
    int bytes_received, bytes_sent, integer_buffer;
    socklen_t address_length = sizeof(struct sockaddr_un);

    // Creating socket file descriptor
    // don`t use 'SOCK_DGRAM', use 'SOCK_STREAM' - https://stackoverflow.com/a/33488309/20314223
    if ((server_fd = socket(AF_UNIX, SOCK_STREAM, 0)) < 0){
        LOGE("socket failed");
        goto finish;
    }

    // used - https://stackoverflow.com/a/65435074/20314223     =========================
    server_address.sun_path[0] = '\0'; /* abstract namespace */
    strcpy(server_address.sun_path + 1, name);
    server_address.sun_family = AF_UNIX;
    int nameLen = strlen(name);
    len = nameLen + offsetof(struct sockaddr_un, sun_path) + 1; // ======================

    if (bind(server_fd,(const struct sockaddr *) &server_address,len) < 0){
        close(server_fd);
        LOGE("bind failed");
        goto finish;
    } // ===============================================================================


    // used - https://www.geeksforgeeks.org/socket-programming-cc/ =================================
    if (listen(server_fd, 100) < 0) {
        LOGE("listen failed");
        goto finish;
    }

    if ((new_client = accept(server_fd, (struct sockaddr*)&server_address,&address_length)) < 0) {
        LOGE("accept failed");
        goto finish;
    }

    // global client
    mClient = new_client;
    mServer = server_fd;


    // creating periodical sending message thread function
    pthread_t thread_id;
    pthread_create(&thread_id, NULL, threadMessage, "hello");

//    valread = read(new_client, buffer, 1024 - 1); // subtract 1 for the null

    finish:
    LOGI("finish");

    // close this connected client socket every time,  otherwise receiver doesn`t read every time (reads only once at the end)
    close(new_client);
}


/**
 * This starts a server
 * */
JNIEXPORT void JNICALL
Java_etsoft_localsocket_cpps_1javac_CppS_1JavaC_1Helper_startServer(JNIEnv *env, jobject thiz) {
    isDoing = 1;

    // creating periodical sending message thread function
    pthread_t thread_id;
    pthread_create(&thread_id, NULL, create_server, NULL);
}

/**
 * This stops the messaging in next loop
 * */
JNIEXPORT void JNICALL
Java_etsoft_localsocket_cpps_1javac_CppS_1JavaC_1Helper_stopServer(JNIEnv *env, jobject thiz) {
    isDoing = 0;
}
