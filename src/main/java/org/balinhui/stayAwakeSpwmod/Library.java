package org.balinhui.stayAwakeSpwmod;

import java.lang.foreign.Arena;
import java.lang.foreign.SymbolLookup;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Library {
    private final Arena arena;
    private final Map<String, SymbolLookup> libraries;

    private static final Set<String> nameOfLibraries = Set.of("kernel32");


    public Library() {
        arena = Arena.ofConfined();
        libraries = nameOfLibraries.stream()
                .collect(Collectors.toMap(
                        s -> s,
                        s -> SymbolLookup.libraryLookup(s, arena)
                ));
    }

    public SymbolLookup getLibrary(String name) {
        if (!nameOfLibraries.contains(name))
            return null;
        return libraries.get(name);
    }

    public void dispose() {
        libraries.clear();
        arena.close();
    }
}
