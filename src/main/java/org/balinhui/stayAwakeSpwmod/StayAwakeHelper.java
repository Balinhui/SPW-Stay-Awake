package org.balinhui.stayAwakeSpwmod;

import java.lang.foreign.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StayAwakeHelper {
    private static ExecutorService run;
    private static Library library;
    private static final Linker linker = Linker.nativeLinker();

    private static boolean enable = false;

    private StayAwakeHelper() {}


    public static void init() {
        if (enable) return;
        run = Executors.newSingleThreadExecutor();
        run.submit(() -> {
            library = new Library();
        });
        enable = true;
    }


    public static void stayAwake(boolean flag) {
        if (!enable) return;

        if (flag) {
            run.submit(() -> {
                try {
                    var function = linker.downcallHandle(
                            library.getLibrary("kernel32").findOrThrow("SetThreadExecutionState"),
                            FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT)
                    );
                    function.invokeExact(0x00000001 | 0x00000002 | 0x80000000);
                } catch (Throwable e) {
                    Toast.error("调用 SetThreadExecutionState 时发生未知错误");
                }
            });
        } else {
            run.submit(() -> {
                try {
                    var function = linker.downcallHandle(
                            library.getLibrary("kernel32").findOrThrow("SetThreadExecutionState"),
                            FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT)
                    );
                    function.invokeExact(0x80000000);
                } catch (Throwable e) {
                    Toast.error("调用 SetThreadExecutionState 时发生未知错误");
                }
            });
        }
    }


    public static void destroy() {
        if (!enable) return;

        StayAwakeStateManager.cancelScreenOn();

        run.submit(() -> {
            library.dispose();
            library = null;
        });
        run.shutdown();
        run.close();
        enable = false;
    }
}
