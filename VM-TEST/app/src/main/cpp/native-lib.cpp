#include <jni.h>
#include <string>
#include <stdlib.h>
#include <signal.h>
#include <sys/wait.h>
#include <unistd.h>
#include <pthread.h>
#include <android/log.h>
#include <sstream>
#include <stdio.h>

#define TAG "test"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)



extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_xudong_1shao_vm_1test_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


int global_value = 0;
void* thread_one(void* arg){
    LOGD("Thread 1");
    for(;;){
        global_value = 1;
        __asm__ __volatile__("mov r0, %0;"
                "mov r1, #1;"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                "add r1, r1, #1;" "str r1, [r0];"
                :
                : "r" (&global_value)
                :
                );

    }
    return ((void*)0);
}

int count[33] = {0};
std::string count_string = "";
void* thread_two(void* arg){
    LOGD("Thread 2");
    count_string = "";
    for(int i=0;i<5000;i++)
        count[global_value]++;

    sleep(2);
    LOGD("Thread two awake!");
    for(int j=0;j<33;j++){
        count_string += std::to_string(count[j]);
    }
    LOGD("Thread 2 finish!");
    return ((void*)0);
}




extern "C" JNIEXPORT jstring
JNICALL
Java_com_example_xudong_1shao_vm_1test_MainActivity_mainthread(JNIEnv *env,
                                                                    jobject ){

    pthread_t pt[2];
    pthread_create(&pt[0],NULL,thread_one,NULL);
    pthread_create(&pt[0],NULL,thread_two,NULL);

    sleep(6);
    return env->NewStringUTF(count_string.c_str());
}




std::string smcstring = "";
typedef void (*myfunc)();
void SMC_one(){
    smcstring += "#SMC1#";
    LOGD("###SMC1###");
}

void SMC_two(){
    smcstring += "#SMC2#";
    LOGD("###SMC2###");
}
extern "C" JNIEXPORT jstring
JNICALL
Java_com_example_xudong_1shao_vm_1test_MainActivity_SMCdetection(JNIEnv *env,
                                                               jobject ){
    for(int i=0;i<100;i++) {
        myfunc this_smc = SMC_one;
        this_smc();
        this_smc = SMC_two;
        this_smc();
    }
    return env->NewStringUTF(smcstring.c_str());
}







void handler_sigtrap(int signo) {
    exit(-1);
}

void handler_sigbus(int signo) {
    exit(-1);
}

int setupSigTrap() {
    // BKPT throws SIGTRAP on nexus 5 / oneplus one (and most devices)
    signal(SIGTRAP, handler_sigtrap);
    // BKPT throws SIGBUS on nexus 4
    signal(SIGBUS, handler_sigbus);
}

// This will cause a SIGSEGV on some QEMU or be properly respected
int tryBKPT() {
    __asm__ __volatile__("bkpt #255");
}


extern "C" JNIEXPORT jint
JNICALL
Java_com_example_xudong_1shao_vm_1test_MainActivity_qemuBkpt(JNIEnv* env, jobject jObject) {

    pid_t child = fork();
    int child_status, status = 0;

    if(child == 0) {
        setupSigTrap();
        tryBKPT();
    } else if(child == -1) {
        status = -1;
    } else {

        int timeout = 0;
        int i = 0;
        while ( waitpid(child, &child_status, WNOHANG) == 0 ) {
            sleep(1);
            // Time could be adjusted here, though in my experience if the child has not returned instantly
            // then something has gone wrong and it is an emulated device
            if(i++ == 1) {
                timeout = 1;
                break;
            }
        }

        if(timeout == 1) {
            // Process timed out - likely an emulated device and child is frozen
            status = 1;
        }

        if ( WIFEXITED(child_status) ) {
            // Likely a real device
            status = 0;
        } else {
            // Didn't exit properly - very likely an emulator
            status = 2;
        }

        // Ensure child is dead
        kill(child, SIGKILL);
    }

    return status;
}
