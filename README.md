**Popl Assignment 4**

Quick Java CLI tool for sorting, searching and simple aggregations over line-oriented input.

Prerequisites
- Java 21 (or newer) installed and available on `PATH`.
- Maven (for building) if you want to rebuild from source. A pre-built shaded JAR is produced by the `mvn package` step.



Run (jar)

Run the included shaded JAR directly (requires Java on the target machine):
```
java -jar dir\popl-assignment4-0.1.0-SNAPSHOT-shaded.jar --use-sample strings --op count
```



Notes and capabilities
- Use `--source file --path <path>` to read a file.
- Use `--op` to choose operation: `sort|freq|avg|inv|count|search`.
 - Use `--use-sample strings|numbers` to run built-in sample inputs.
 - Use `--run-all-samples` to execute every operation against both sample datasets and print results.



Numeric parsing and `avg`
- The `avg` operation will attempt to parse each input line as a number (using `Double.parseDouble`).
- Non-numeric lines are silently skipped when computing the average. If no numeric values are found, the program prints `No numbers`.



Running on network streams
- Local stream mode (the app starts a server that streams a local file):
	- Use `--source stream --server-file <file> [--port <port>]` to have the app start a small stream server that serves `<file>` and then reads it back over a socket. This is convenient for testing streaming behavior locally.
	- Example (run jar):
		```powershell
		# streams src/main/resources/samples/strings.txt and performs count
		java -jar target\popl-assignment4-0.1.0-SNAPSHOT.jar --source stream --server-file src\main\resources\samples\strings.txt --op count
		```

- Remote stream (streaming across machines):
	- Start the stream server on the producer machine (runs the `StreamServer` to stream a file on a chosen port):
		```powershell
		# On producer host (runs the StreamServer standalone)
		# This project exposes StreamServer.startServer(file, port) as part of the app; you can also run the jar with --source stream and --server-file on the producer to start the server locally.
		java -cp target\popl-assignment4-0.1.0-SNAPSHOT.jar app.Main --source stream --server-file C:\path\to\file.txt --port 5555
		```
	- On the consumer (client) machine, use the socket source to connect to the producer's host and port:
		```powershell
		java -jar target\popl-assignment4-0.1.0-SNAPSHOT.jar --source socket --host <producer-host> --port 5555 --op count
		```



Examples
```
# Count lines in sample strings
java -jar dir\popl-assignment4-0.1.0-SNAPSHOT-shaded.jar --use-sample strings --op count

# Sort a file
java -jar dir\popl-assignment4-0.1.0-SNAPSHOT-shaded.jar --source file --path somefile.txt --op sort
```
