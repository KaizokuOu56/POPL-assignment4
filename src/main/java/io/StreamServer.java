package io;

import java.io.*;
import java.net.*;

public final class StreamServer {

    public static void startServer(String filePath, int port) throws Exception {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("[Server] Listening on port " + port + "...");

            try (Socket client = server.accept();
                 BufferedWriter out = new BufferedWriter(
                     new OutputStreamWriter(client.getOutputStream()));
                 BufferedReader file = new BufferedReader(new FileReader(filePath))) {

                System.out.println("[Server] Client connected.");

                String line;
                while ((line = file.readLine()) != null) {
                    out.write(line);
                    out.write("\n");
                    out.flush();

                    Thread.sleep(50); // simulate network delay
                }

                System.out.println("[Server] Completed sending data.");
            }
        }
    }
}

