package io.abc_def.kickstart_fx.core;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import io.abc_def.kickstart_fx.issue.ErrorEventFactory;
import io.abc_def.kickstart_fx.issue.TrackEvent;
import io.abc_def.kickstart_fx.util.JacksonMapper;
import io.abc_def.kickstart_fx.util.OsType;
import io.abc_def.kickstart_fx.util.ThreadHelper;
import lombok.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AppBeacon {

    private static AppBeacon INSTANCE;

    private final Path path;

    private WinNT.HANDLE namedPipeHandle;
    private RandomAccessFile randomAccessFile;

    public AppBeacon(Path path) {
        this.path = path;
    }

    public static boolean isExistingBeaconRunning() {
        var data = new WinBase.WIN32_FIND_DATA();
        var r = Kernel32.INSTANCE.FindFirstFile(getNamedPipeLocation().toString(), data.getPointer());
        if (!WinBase.INVALID_HANDLE_VALUE.equals(r)) {
            Kernel32.INSTANCE.FindClose(r);
            return true;
        }

        return false;
    }

    public static void sendRequest(AppBeaconMessage message) throws IOException {
        var pipeName = getNamedPipeLocation().toString();

        // based on https://msdn.microsoft.com/en-us/library/windows/desktop/aa365592(v=vs.85).aspx
        Kernel32.INSTANCE.WaitNamedPipe(pipeName, (int) TimeUnit.SECONDS.toMillis(15L));

        WinNT.HANDLE hPipe = Kernel32.INSTANCE.CreateFile(pipeName,
                WinNT.GENERIC_READ | WinNT.GENERIC_WRITE,
                0,                      // no sharing
                null,                   // default security attributes
                WinNT.OPEN_EXISTING,      // opens existing pipe
                0,                      // default attributes
                null                  // no template file
        );

        try {
            IntByReference lpMode = new IntByReference(WinBase.PIPE_READMODE_MESSAGE);
            Kernel32.INSTANCE.SetNamedPipeHandleState(hPipe, lpMode, null, null);

            String expMessage = JacksonMapper.getDefault().writeValueAsString(message);
            byte[] expData = expMessage.getBytes();
            IntByReference lpNumberOfBytesWritten = new IntByReference(0);
            Kernel32.INSTANCE.WriteFile(hPipe, expData, expData.length, lpNumberOfBytesWritten, null);

            byte[] readBuffer = new byte[1024];
            IntByReference lpNumberOfBytesRead = new IntByReference(0);
            Kernel32.INSTANCE.ReadFile(hPipe, readBuffer, readBuffer.length, lpNumberOfBytesRead, null);

            int readSize = lpNumberOfBytesRead.getValue();
            String actMessage = new String(readBuffer, 0, readSize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        Path path = switch (OsType.ofLocal()) {
            case OsType.Windows windows -> getNamedPipeLocation();
            default -> null;
        };

        try {
            INSTANCE = new AppBeacon(path);
            INSTANCE.start();
            TrackEvent.withInfo("Started beacon")
                    .tag("path", path)
                    .build()
                    .handle();
        } catch (Exception ex) {
            // Not terminal!
            // We can still continue without the running server
            ErrorEventFactory.fromThrowable("Unable to start beacon", ex)
                    .handle();
        }
    }

    public static void reset() {
        if (INSTANCE != null) {
            INSTANCE.stop();
            INSTANCE = null;
        }
    }

    public static AppBeacon get() {
        return INSTANCE;
    }



    private static Path getNamedPipeLocation() {
        var pipeName = "\\\\.\\pipe\\" + AppNames.ofCurrent().getKebapName();
        return Path.of(pipeName);
    }

    private void createNamedPipe() throws IOException {
        String    pipeName= path.toString();
        WinNT.HANDLE hNamedPipe= Kernel32.INSTANCE.CreateNamedPipe(
                pipeName,
                WinBase.PIPE_ACCESS_DUPLEX,        // dwOpenMode
                WinBase.PIPE_TYPE_MESSAGE  | WinBase.PIPE_READMODE_MESSAGE | WinBase.PIPE_WAIT,    // dwPipeMode
                1,    // nMaxInstances,
                Byte.MAX_VALUE,    // nOutBufferSize,
                Byte.MAX_VALUE,    // nInBufferSize,
                1000,    // nDefaultTimeOut,
                null);    // lpSecurityAttributes
        if (WinBase.INVALID_HANDLE_VALUE.equals(hNamedPipe)) {
            throw new IOException("Unable to create named pipe at " + pipeName + ". " + getWinError());
        }

        namedPipeHandle = hNamedPipe;
    }

    private static String getWinError() {
        int hr = Kernel32.INSTANCE.GetLastError();
        if (hr == WinError.ERROR_SUCCESS) {
            return "Operation failed with unknown reason code";
        } else {
            return "Operation failed: hr=" + hr + " - 0x" + Integer.toHexString(hr);
        }
    }

    private void runThread() {
        final int MAX_BUFFER_SIZE=1024;
        ThreadHelper.createPlatformThread("beacon", false, () -> {
            while (true) {
                try {
                    Kernel32.INSTANCE.ConnectNamedPipe(namedPipeHandle, null);

                    var r = Kernel32.INSTANCE.ConnectNamedPipe(namedPipeHandle, null);

                    byte[] readBuffer = new byte[MAX_BUFFER_SIZE];
                    IntByReference lpNumberOfBytesRead = new IntByReference(0);
                    Kernel32.INSTANCE.ReadFile(namedPipeHandle, readBuffer, readBuffer.length, lpNumberOfBytesRead, null);

                    int readSize = lpNumberOfBytesRead.getValue();

                    IntByReference lpNumberOfBytesWritten = new IntByReference(0);
                    Kernel32.INSTANCE.WriteFile(namedPipeHandle, readBuffer, readSize, lpNumberOfBytesWritten, null);

                    // Flush the pipe to allow the client to read the pipe's contents before disconnecting
                    Kernel32.INSTANCE.FlushFileBuffers(namedPipeHandle);

                    var rr = randomAccessFile.read();
                    var request = JacksonMapper.getDefault().readValue(randomAccessFile, AppBeaconMessage.class);
                    if (request == null) {
                        break;
                    }

                    System.out.print(request.toString());
                } catch (IOException e) {
                    ErrorEventFactory.fromThrowable(e).handle();
                }
            }
        }).start();
    }

    private void closeNamedPipe() {
        Kernel32.INSTANCE.DisconnectNamedPipe(namedPipeHandle);
        Kernel32.INSTANCE.CloseHandle(namedPipeHandle);
        namedPipeHandle = null;
    }

    private void stop() {
        try {
            randomAccessFile.close();
        } catch (IOException e) {
            ErrorEventFactory.fromThrowable(e).handle();
        }

        if (OsType.ofLocal() == OsType.WINDOWS) {
            closeNamedPipe();
        }
    }

    private void start() throws IOException {
        if (OsType.ofLocal() == OsType.WINDOWS) {
            createNamedPipe();
        }

        randomAccessFile = new RandomAccessFile(path.toFile(), "rw");
        runThread();
    }
}
