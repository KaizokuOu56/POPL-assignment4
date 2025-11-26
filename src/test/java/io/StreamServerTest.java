package io;

import model.ImmutableList;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

public class StreamServerTest {

    @Test
    public void streamsOverSocket_areDeliveredCorrectly() throws Exception {
        // copy sample resource to temp file
        Path temp = Files.createTempFile("streamserver-test-", ".txt");
        try (InputStream in = StreamServerTest.class.getResourceAsStream("/samples/strings.txt")) {
            assertNotNull(in, "sample resource not found on classpath");
            Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
        }

        // pick an ephemeral free port
        int port;
        try (ServerSocket ss = new ServerSocket(0)) {
            port = ss.getLocalPort();
        }

        final Exception[] serverEx = new Exception[1];
        Thread serverThread = new Thread(() -> {
            try {
                StreamServer.startServer(temp.toString(), port);
            } catch (Exception e) {
                serverEx[0] = e;
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // allow the server to bind and wait for a client
        Thread.sleep(100);

        ImmutableList<String> fromSocket = SocketReaderAdapter.loadLinesFromSocket("127.0.0.1", port);
        ImmutableList<String> fromFile = FileReaderAdapter.loadLines(temp);

        assertEquals(fromFile.asList(), fromSocket.asList(), "Lines read over socket should match file contents");
        if (serverEx[0] != null) fail("Server threw an exception: " + serverEx[0].getMessage());
    }
}
