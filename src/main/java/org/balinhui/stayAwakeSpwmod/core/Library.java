package org.balinhui.stayAwakeSpwmod.core;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Library implements AutoCloseable {
    private static final Linker LINKER = Linker.nativeLinker();

    private final ExecutorService service;
    private final Arena arena;
    private final SymbolLookup library;
    private volatile boolean closed = false;

    public Library(String libName) {
        this.service = Executors.newSingleThreadExecutor(r -> {
            var t = new Thread(r, "native-lib-" + libName);
            t.setDaemon(true);
            return t;
        });

        // 在 service 线程中初始化，并同步等待完成，保证 arena/library 的可见性
        try {
            Future<Object[]> init = service.submit(() -> {
                var a = Arena.ofConfined();
                var l = (libName == null)
                        ? LINKER.defaultLookup()
                        : SymbolLookup.libraryLookup(libName, a);
                return new Object[]{a, l};
            });
            Object[] arr = init.get();
            this.arena = (Arena) arr[0];
            this.library = (SymbolLookup) arr[1];
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            service.shutdownNow();
            throw new IllegalStateException("初始化时发现中断", e);
        } catch (ExecutionException e) {
            service.shutdownNow();
            throw new IllegalStateException("初始化本地库失败", e.getCause());
        }
    }

    /**
     * 从库中查找对应的函数
     * 注意: 此方法不准在<code>call</code>和<code>callVoid</code>的lambda中调用，因为这些方法都将在<code>service</code>中执行
     * @param methodName 函数名
     * @param descriptor 函数签名
     * @return 对应的句柄
     * @throws NoSuchElementException 如果没有查找到相应的函数
     */
    public MethodHandle findMethod(String methodName, FunctionDescriptor descriptor) throws NoSuchElementException {
        if (closed) throw new IllegalStateException("Library 已关闭");
        try {
            if (Thread.currentThread().getName().startsWith("native-lib-"))
                throw new RuntimeException("无法在库调用线程中执行");
            return service.submit(() -> LINKER.downcallHandle(
                    library.findOrThrow(methodName),
                    descriptor
            )).get();
        } catch (ExecutionException e) {
            var cause = e.getCause();
            if (cause instanceof NoSuchElementException nse) throw nse;
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("调用函数时发现中断", e);
        }
    }

    /**
     * 在<code>service</code>中执行调用函数的操作<br>
     * 在<code>Task</code>类型中只能含有函数调用相关的操作，请勿将耗时任务置于其中<br>
     * 禁止将<code>findMethod</code>和<code>callVoid</code>置于其中
     * @param task 函数调用操作
     * @return 操作的返回值
     */
    public Object call(Task task) {
        if (closed) throw new IllegalStateException("Library 已关闭");
        try {
            return service.submit(() -> {
                try {
                    return task.invoke(LINKER, library);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("调用函数时发生中断", e);
        } catch (ExecutionException e) {
            var cause = e.getCause();
            if (cause instanceof RuntimeException re) throw re;
            if (cause instanceof Error err) throw err;
            throw new IllegalStateException("执行失败", cause);
        }
    }

    /**
     * 与<code>call</code>方法类似，无返回值
     * @param task 无返回值的函数调用
     */
    public void callVoid(VoidTask task) {
        if (closed) throw new IllegalStateException("Library 已关闭");
        try {
            service.submit(() -> {
                try {
                    task.invoke(LINKER, library);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("调用函数时发生中断", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException re) throw re;
            if (cause instanceof Error err) throw err;
            throw new IllegalStateException("执行失败", cause);
        }
    }

    @Override
    public synchronized void close() {
        if (closed) return;
        closed = true;
        try {
            service.submit(arena::close).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException _) {
        } finally {
            service.shutdown();
        }
    }

    @FunctionalInterface
    public interface Task {
        Object invoke(Linker linker, SymbolLookup lib) throws Throwable;
    }

    @FunctionalInterface
    public interface VoidTask {
        void invoke(Linker linker, SymbolLookup lib) throws Throwable;
    }
}
