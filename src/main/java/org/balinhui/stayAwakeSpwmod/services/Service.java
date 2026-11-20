package org.balinhui.stayAwakeSpwmod.services;

public abstract class Service {
    private volatile boolean closed = false;

    public boolean isClosed() {
        return closed;
    }

    public abstract void init();

    public final void close() {
        if (closed) return; // 防止二次关闭
        try {
            onClose();
        } finally {
            closed = true;
        }
    }

    protected abstract void onClose();
}
