package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Ping {
    public static final int TIMEOUT;
    public static final int NUM_OF_CONNECTION_ATTEMPTS;

    private static final File logDir;
    private final InetAddress ipAddress;
    private final File file;
    private final BufferedWriter writer;

    static {
        TIMEOUT = 1000;
        NUM_OF_CONNECTION_ATTEMPTS = 3;

        logDir = new File("E:\\Documents\\Java\\Ping\\out\\logs");
    }

    public Ping(String ip) throws IOException {
        ipAddress = InetAddress.getByName(ip);

        if (!logDir.mkdirs() && !logDir.exists()) {
            throw new IllegalStateException("Failed to create the logs directory");
        }
        file = new File(logDir, formattedNow() + ".log");
        writer = new BufferedWriter(new FileWriter(file));
        System.out.println("Log file: " + file.getAbsolutePath());
        log("Pinging " + ip);
    }

    public void continuousConnectionCheck() throws IOException {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime;

        boolean status = true;
        int FailedAttempts = 0;
        int successfulAttempts = 0;

        //noinspection InfiniteLoopStatement
        while (true) {
            if (isConnected()) {
                FailedAttempts = 0;
                if (++successfulAttempts == NUM_OF_CONNECTION_ATTEMPTS && !status) {
                    endTime = LocalDateTime.now();
                    double timeDifference = ChronoUnit.NANOS.between(startTime, endTime) / Math.pow(10, 9);
                    log("Lost connection from " + timeFormatter.format(startTime) + " to " + timeFormatter.format(endTime) + " (" + timeDifference + "s)");
                    startTime = endTime;
                    status = true;
                }
            } else {
                successfulAttempts = 0;
                if (++FailedAttempts == NUM_OF_CONNECTION_ATTEMPTS && status) {
                    endTime = LocalDateTime.now();
                    double timeDifference = ChronoUnit.NANOS.between(startTime, endTime) / Math.pow(10, 9);
                    log("Connected from " + timeFormatter.format(startTime) + " to " + timeFormatter.format(endTime) + "(" + timeDifference + "s)");
                    startTime = endTime;
                    status = false;
                }
            }
        }

    }

    public boolean isConnected() throws IOException {
        try {
            return ipAddress.isReachable(TIMEOUT);
        } catch (SocketException e) {
            return false;
        }
    }

    public void log(String message) throws IOException {
        System.out.println(message);
        writer.append(message).append("\n");
        writer.flush();
    }

    private String formattedNow() {
        LocalDateTime now = LocalDateTime.now();
        return DateTimeFormatter.ofPattern("yyyy_MM_dd-HH_mm_ss").format(now);
    }
}
