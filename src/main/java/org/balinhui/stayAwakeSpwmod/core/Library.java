package org.balinhui.stayAwakeSpwmod.core;

import java.lang.foreign.Arena;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
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
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException re) throw re;
            if (cause instanceof Error err) throw err;
            throw new IllegalStateException("执行失败", cause);
        }
    }

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
            // close 期间的异常一般可忽略
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
