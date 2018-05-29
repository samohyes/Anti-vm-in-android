# Anti-vm-in-android

This is an apk for detecting virtual machine. Most of these techniques can be bypassed by changing the android framework and kernel.

Key points for testing this apk: use an arm based image emulator otherwise you can't compile those jni code with arm assembly int it.

*1.qemu properties
   Simply checking those properties in qemu.

*2.device id
   Some emulators have default device id.
 
*3.qemu pipes
   Check whether there exists qemu pipes. 
   /dev/socket/qemud /dev/qemu_pipe
   
*4.default number 
   Some emulators have default numbers.

*5.IMSI number
   Some emulators have default IMSI numbers.
 
*6.Build properties
   There is a Build.java file and it has some public variables. We can check those strings.

*7.Operator
   Some emulators have "android" as operators.
   
*8.qemu drivers
   There is goldfish under "/proc/tty/drivers" and "/proc/cpuinfo".

*9.qemu files
   There is "/system/lib/libc_malloc_debug_qemu.so", "/sys/qemu_trace", "/system/bin/qemu-props" in an emulator.
 
*10.genymotion files
   There is "/dev/socket/genyd", "/dev/socket/baseband_genyd" in genymotion emulators. For those who is not using genymotion, you can probably ignore this.
 
*11.Monkey
   Funny one. Some people may use Monkey for testing. It's not a real monkey just some kind of testing skills.
   
*12.debugger

*13.ptrace
   Bypass this one by changing the goldfish kernel source code.
   
*14.eth0
   Some emulators has eth0 network interface.

*15.Taintdroid
   Simply detect taintdroid.

*16.sensor
   Emulators may can't register sensors or its sensors have constant values.
   
*17.qemu tasks
   In qemu, the processor has to finish the current task before start the other one. So here the thread two can't get the globale variable when it querys. It has to wait for the thread one to finish that arm assembly block code.

*18.SMC detection
   Arm is actually based on harvard architecture. Whihc means, if we get a function address and put function 1 and function 2 on that address rotationally we may execute function 1 and function 2 randomly. While in emulators, it just execute what function we put there right before we call it.

*19.break point detection
   For qemu, it actually locked or quit abnormally when we call "bkpt".   