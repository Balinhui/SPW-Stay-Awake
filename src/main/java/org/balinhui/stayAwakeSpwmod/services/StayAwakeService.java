package org.balinhui.stayAwakeSpwmod.services;

import org.balinhui.stayAwakeSpwmod.core.Library;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

public class StayAwakeService extends Service {
    private Library kernel32;
    private MethodHandle methodCache;

    public StayAwakeService() {
        //kernel32 = new Library("kernel32");
    }

    @Override
    public void init() {
        kernel32 = new Library("kernel32");
        try {
            methodCache = kernel32.findMethod(
                    "SetThreadExecutionState",
                    FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT)
            );
        } catch (RuntimeException e) {
            kernel32.close();
            kernel32 = null;
            throw e;
        }
    }

    private void applyExecutionState(int flags) {
        kernel32.callVoid(((_, _) -> {
            if (methodCache != null)
                methodCache.invokeExact(flags);
        }));
    }

    public void stayAwake(boolean flag) {
        if (isClosed()) return;
        applyExecutionState(flag ? (0x00000001 | 0x00000002 | 0x80000000) : 0x80000000);
    }

    @Override
    public void onClose() {
        if (kernel32 == null) return;
        try {
            // 使电脑息屏回归到正常状态
            applyExecutionState(0x80000000);
        } finally {
            kernel32.close();
        }
    }
}
