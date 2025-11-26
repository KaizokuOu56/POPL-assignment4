package app;

import io.FileReaderAdapter;
import io.SocketReaderAdapter;
import io.StreamServer;
import algo.Sorter;
import algo.Aggregations;
import algo.Searcher;
import model.ImmutableList;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;

public class Main {
    @Command(name = "popl-assignment4", mixinStandardHelpOptions = true, version = "0.1")
    static class Cli implements Runnable {
        @Option(names = "--source", description = "file|socket|stream (default: file)")
        String source = "file";

        @Option(names = "--path", description = "path to input file (for source=file)")
        String path;

        @Option(names = "--host", description = "host for socket source (default: 127.0.0.1)")
        String host = "127.0.0.1";

        @Option(names = "--port", description = "port for socket/stream source (default: 5555)")
        int port = 5555;

        @Option(names = "--server-file", description = "file to stream (for source=stream)")
        String serverFile;

        @Option(names = "--op", description = "operation: sort|freq|avg|inv|count|search (default: sort)")
        String op = "sort";

        @Option(names = "--key", description = "key for search")
        String key;

        @Option(names = "--use-sample", description = "Use a built-in sample: strings|numbers")
        String useSample;

        @Option(names = "--run-all-samples", description = "Run every operation against all built-in samples and print results")
        boolean runAllSamples = false;

        @Option(names = "--parse-numeric", description = "Parse input lines as numbers where possible and run numeric operations")
        boolean parseNumeric = false;
        public void run() {
            try {
                try {
                    if (runAllSamples) {
                        runAllSamples();
                        return;
                    }

                    if (useSample != null && !useSample.isBlank()) {
                        ImmutableList<String> data = loadSample(useSample);
                        runOperation(op, data);
                        return;
                    }

                    ImmutableList<String> data = null;
                    if ("file".equalsIgnoreCase(source)) {
                        if (path == null) { System.err.println("--path is required for source=file"); return; }
                        data = FileReaderAdapter.loadLines(Path.of(path));
                    } else if ("socket".equalsIgnoreCase(source)) {
                        data = SocketReaderAdapter.loadLinesFromSocket(host, port);
                    } else if ("stream".equalsIgnoreCase(source)) {
                        if (serverFile == null) { System.err.println("--server-file is required for source=stream"); return; }
                        Thread serverThread = new Thread(() -> {
                            try { StreamServer.startServer(serverFile, port); }
                            catch (Exception e) { System.err.println("Stream server error: " + e.getMessage()); }
                        });
                        serverThread.setDaemon(true);
                        serverThread.start();
                        waitForPortOpen(host, port, 5000);
                        data = SocketReaderAdapter.loadLinesFromSocket(host, port);
                    } else {
                        System.err.println("Unknown source: " + source);
                        return;
                    }

                    runOperation(op, data);
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        private ImmutableList<String> loadSample(String name) throws Exception {
            String resource = switch (name.toLowerCase(Locale.ROOT)) {
                case "strings" -> "/samples/strings.txt";
                case "numbers" -> "/samples/numbers.txt";
                default -> throw new IllegalArgumentException("Unknown sample: " + name);
            };
            InputStream in = Main.class.getResourceAsStream(resource);
            if (in == null) throw new IllegalStateException("Sample resource not found: " + resource);
            List<String> lines = new ArrayList<>();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
                String l;
                while ((l = r.readLine()) != null) lines.add(l);
            }
            return new ImmutableList<>(lines);
        }

        private void runAllSamples() throws Exception {
            String[] samples = new String[]{"strings", "numbers"};
            String[] ops = new String[]{"sort", "freq", "count", "avg", "inv", "search"};
            for (String sname : samples) {
                System.out.println("=== Sample: " + sname + " ===");
                ImmutableList<String> data = loadSample(sname);
                for (String opn : ops) {
                    System.out.println("--- Operation: " + opn + " ---");
                    try {
                        if ("search".equals(opn)) {
                            String key = sname.equals("strings") ? "apple" : "3";
                            System.out.println("search key: " + key);
                            int idx = Searcher.linearSearch(data, key);
                            System.out.println(idx);
                        } else {
                            runOperation(opn, data);
                        }
                    } catch (Exception e) {
                        System.err.println("Operation failed: " + e.getMessage());
                    }
                }
            }
        }

        private void runOperation(String op, ImmutableList<String> data) throws Exception {
            if (data == null) { System.err.println("No data"); return; }

            if (parseNumeric) {
                // parse numeric values, skipping non-numeric lines
                List<Double> nums = new ArrayList<>();
                for (String s : data.asList()) {
                    if (s == null || s.isBlank()) continue;
                    try { nums.add(Double.parseDouble(s.trim())); }
                    catch (NumberFormatException ignore) { /* skip */ }
                }

                switch (op.toLowerCase(Locale.ROOT)) {
                    case "sort": {
                        ImmutableList<Double> numbers = new ImmutableList<>(new ArrayList<>(nums));
                        ImmutableList<Double> sorted = Sorter.mergeSort(numbers, Comparator.naturalOrder());
                        for (Double d : sorted.asList()) System.out.println(d);
                        break;
                    }
                    case "freq": {
                        ImmutableList<Double> numbers = new ImmutableList<>(new ArrayList<>(nums));
                        Map<Double, Long> freq = Aggregations.frequencyCount(numbers);
                        freq.forEach((k,v) -> System.out.println(k + " -> " + v));
                        break;
                    }
                    case "count":
                        System.out.println(nums.size());
                        break;
                    case "avg": {
                        if (nums.isEmpty()) { System.out.println("No numbers"); break; }
                        ImmutableList<Number> numbers = new ImmutableList<>(new ArrayList<>(nums));
                        OptionalDouble avg = Aggregations.average(numbers);
                        if (avg.isPresent()) System.out.println(avg.getAsDouble()); else System.out.println("No numbers");
                        break;
                    }
                    case "inv": {
                        ImmutableList<Double> numbers = new ImmutableList<>(new ArrayList<>(nums));
                        long c = Aggregations.countInversions(numbers);
                        System.out.println(c);
                        break;
                    }
                    case "search": {
                        if (key == null) { System.err.println("--key is required for search"); return; }
                        try {
                            double k = Double.parseDouble(key);
                            ImmutableList<Double> numbers = new ImmutableList<>(new ArrayList<>(nums));
                            int idx = Searcher.linearSearch(numbers, k);
                            System.out.println(idx);
                        } catch (NumberFormatException nfe) {
                            System.err.println("--key is not a valid number for numeric search");
                        }
                        break;
                    }
                    default:
                        System.err.println("Unknown operation: " + op);
                }
                return;
            }

            // default: treat data as strings
            switch (op.toLowerCase(Locale.ROOT)) {
                case "sort":
                    ImmutableList<String> sorted = Sorter.mergeSort(data, Comparator.naturalOrder());
                    for (String s : sorted.asList()) System.out.println(s);
                    break;
                case "freq":
                    Map<String, Long> freq = Aggregations.frequencyCount(data);
                    freq.forEach((k,v) -> System.out.println(k + " -> " + v));
                    break;
                case "count":
                    System.out.println(data.size());
                    break;
                case "avg":
                    List<Double> nums = new ArrayList<>();
                    for (String s : data.asList()) {
                        if (s == null || s.isBlank()) continue;
                        try {
                            nums.add(Double.parseDouble(s.trim()));
                        } catch (NumberFormatException nfe) {
                            // skip non-numeric values
                        }
                    }
                    if (nums.isEmpty()) {
                        System.out.println("No numbers");
                    } else {
                        ImmutableList<Number> numbers = new ImmutableList<>(new ArrayList<>(nums));
                        OptionalDouble avg = Aggregations.average(numbers);
                        if (avg.isPresent()) System.out.println(avg.getAsDouble()); else System.out.println("No numbers");
                    }
                    break;
                case "inv":
                    long c = Aggregations.countInversions(new ImmutableList<>(data.asList()));
                    System.out.println(c);
                    break;
                case "search":
                    if (key == null) { System.err.println("--key is required for search"); return; }
                    int idx = Searcher.linearSearch(data, key);
                    System.out.println(idx);
                    break;
                default:
                    System.err.println("Unknown operation: " + op);
            }
        }
    }

    private static void waitForPortOpen(String host, int port, long timeoutMs) throws Exception {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMs) {
            try (java.net.Socket sock = new java.net.Socket()) {
                sock.connect(new java.net.InetSocketAddress(host, port), 200);
                return; // success
            } catch (java.io.IOException ignored) {
                Thread.sleep(100);
            }
        }
        throw new IllegalStateException("Timed out waiting for port " + port);
    }

    public static void main(String[] args) {
        int exit = new CommandLine(new Cli()).execute(args);
        System.exit(exit);
    }
}

