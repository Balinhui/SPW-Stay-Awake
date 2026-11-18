package org.balinhui.stayAwakeSpwmod.core;

import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.ValueLayout;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    @Test
    void call() {
        Library l = new Library(null);
        int result = (int) l.call((linker, lib) -> {
            var function = linker.downcallHandle(
                    lib.findOrThrow("strlen"),
                    FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
            );
            try (Arena arena = Arena.ofConfined()) {
                return (int) function.invokeExact(arena.allocateFrom("Hello"));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals(5, result);
        l.close();
    }
}