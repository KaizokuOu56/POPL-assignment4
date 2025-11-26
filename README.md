**Popl Assignment 4**

Quick Java CLI tool for sorting, searching and simple aggregations over line-oriented input.

Prerequisites
- Java 21 (or newer) installed and available on `PATH`.
- Maven (for building) if you want to rebuild from source. A pre-built shaded JAR is produced by the `mvn package` step.

Build

Windows PowerShell:
```
mvn -DskipTests package
```

This produces a shaded JAR at `target\popl-assignment4-0.1.0-SNAPSHOT-shaded.jar`.

Run (jar)

Run the included shaded JAR directly (requires Java on the target machine):
```
java -jar target\popl-assignment4-0.1.0-SNAPSHOT-shaded.jar --use-sample strings --op count
```

Run (fallback batch launcher)

If you prefer a simple clickable launcher on Windows, use the bundled batch script:
```
scripts\run.bat --use-sample strings --op count
```

Create a Windows executable (optional)

If your JDK distribution includes `jpackage`, you can produce a Windows `.exe` that bundles a runtime (no extra downloads). Example command (run from project root):
```
jpackage --name PoplAssignment4 --input target --main-jar popl-assignment4-0.1.0-SNAPSHOT-shaded.jar --main-class app.Main --type exe --win-console --dest dist
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

Finalizing the executable (Windows)
- You can produce a distributable Windows app-image/executable that bundles a runtime using `jpackage` (JDK 14+; use JDK 21 to match the build target). There are two practical options:
	- `--type app-image`: produces a folder that contains `PoplAssignment4.exe` and a bundled runtime. This does not require extra tools and is cross-machine runnable.
	- `--type exe` / installer: produces a Windows installer, but building an installer type may require the WiX toolset (`light.exe`, `candle.exe`) on PATH.

Example (create an app-image using the JDK that contains `jpackage`):
```powershell
& "$env:JAVA_HOME\bin\jpackage.exe" --name PoplAssignment4 \
	--input target \
	--main-jar popl-assignment4-0.1.0-SNAPSHOT.jar \
	--main-class app.Main \
	--type app-image \
	--win-console \
	--dest dist21
```

This produces `dist21\PoplAssignment4\PoplAssignment4.exe` (plus a `runtime` folder). Run it like:
```powershell
dist21\PoplAssignment4\PoplAssignment4.exe --use-sample strings --op count
```

If you prefer a single installer (`.exe` or `.msi`) you can use `--type exe` or `--type msi` but you will need to install WiX on the build machine (or CI runner) to build the installer. I can set this up in CI if you want.

Packaging in CI
- For reproducible cross-platform artifacts, run `jpackage` on the matching OS or use GitHub Actions with `ubuntu-latest`, `macos-latest`, and `windows-latest` jobs to produce Linux/macOS/Windows artifacts respectively.

Examples
```
# Count lines in sample strings
java -jar target\popl-assignment4-0.1.0-SNAPSHOT-shaded.jar --use-sample strings --op count

# Sort a file
java -jar target\popl-assignment4-0.1.0-SNAPSHOT-shaded.jar --source file --path somefile.txt --op sort
```

If you want me to produce a native installer (`.exe`) now, tell me to proceed and confirm you're OK with a platform-specific artifact for Windows only.
