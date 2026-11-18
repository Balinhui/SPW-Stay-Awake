package org.balinhui.stayAwakeSpwmod.services;

import org.balinhui.stayAwakeSpwmod.core.Library;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.ValueLayout;

public class StayAwakeService extends Service {
    private final Library kernel32;
    private boolean closed = false;

    public StayAwakeService() {
        kernel32 = new Library("kernel32");
    }

    @Override
    public void init() {
        //Nothing to do
    }

    public void stayAwake(boolean flag) {
        if (flag) {
            kernel32.callVoid(((linker, lib) -> {
                var function = linker.downcallHandle(
                        lib.findOrThrow("SetThreadExecutionState"),
                        FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT)
                );
                function.invokeExact(0x00000001 | 0x00000002 | 0x80000000);
            }));
        } else {
            kernel32.callVoid(((linker, lib) -> {
                var function = linker.downcallHandle(
                        lib.findOrThrow("SetThreadExecutionState"),
                        FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT)
                );
                function.invokeExact(0x80000000);
            }));
        }
    }

    @Override
    public void close() {
        if (closed) return;//防止二次关闭
        closed = true;

        try {
            //使电脑息屏回归到正常状态
            stayAwake(false);
        } finally {
            kernel32.close();
        }
    }
}
