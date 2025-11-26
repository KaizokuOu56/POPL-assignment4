package io;

import model.ImmutableList;
import java.io.*;
import java.net.*;
import java.util.*;

public final class SocketReaderAdapter {

    public static ImmutableList<String> loadLinesFromSocket(String host, int port) throws IOException {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()))) {

            List<String> lines = new ArrayList<>();
            String line;

            while ((line = in.readLine()) != null) {
                lines.add(line);
            }

            return new ImmutableList<>(lines);
        }
    }
}

