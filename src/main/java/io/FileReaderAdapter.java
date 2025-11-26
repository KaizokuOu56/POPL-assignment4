package io;

import model.ImmutableList;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public final class FileReaderAdapter {

    public static ImmutableList<String> loadLines(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        return new ImmutableList<>(lines);
    }
}

