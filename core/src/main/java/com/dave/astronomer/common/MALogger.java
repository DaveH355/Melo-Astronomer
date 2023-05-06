package com.dave.astronomer.common;

import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.minlog.Log;

import java.io.*;

import static com.esotericsoftware.minlog.Log.*;


public class MALogger extends Logger implements Disposable {
    private final long firstLogTime = System.currentTimeMillis();
    private PrintStream printStream;

    public MALogger(File gameDirectory) throws FileNotFoundException {
        File logDirectory = new File(gameDirectory,"log");
        logDirectory.mkdir();

        printStream = new PrintStream(new File(logDirectory, "log.txt"));

    }


    @Override
    public void log(int level, String category, String message, Throwable ex) {
        StringBuilder builder = new StringBuilder(256);

        long time = System.currentTimeMillis() - firstLogTime;
        long minutes = time / (1000 * 60);
        long seconds = time / (1000) % 60;
        if (minutes <= 9) builder.append('0');
        builder.append(minutes);
        builder.append(':');
        if (seconds <= 9) builder.append('0');
        builder.append(seconds);

        switch (level) {
            case LEVEL_ERROR -> builder.append("  ERROR: ");
            case LEVEL_WARN -> builder.append("  WARN: ");
            case LEVEL_INFO -> builder.append("  INFO: ");
            case LEVEL_DEBUG -> builder.append("  DEBUG: ");
            case LEVEL_TRACE -> builder.append("  TRACE: ");
        }

        builder.append("[");
        builder.append(Thread.currentThread().getName());

        
        if (category != null) {
            builder.append("/");
            builder.append(category);

            //divert kryo and kryonet debug messages they are repetitive
            if (level == LEVEL_DEBUG && (category.contains("kryonet") || category.contains("kryo"))) {
                return;
            }
        }


        builder.append("] ");

        builder.append(message);

        if (ex != null) {
            StringWriter writer = new StringWriter(256);
            ex.printStackTrace(new PrintWriter(writer));
            builder.append('\n');
            builder.append(writer.toString().trim());
        }

        String string = builder.toString();


        writeToStream(string, printStream);

        if (level == LEVEL_ERROR) error(string);
        else print(string);

    }
    public void error(String err) {
        System.err.println(err);
    }
    public void writeToStream(String string, PrintStream stream) {
        try {
            stream.write((string + "\n").getBytes());
        } catch (IOException e) {
            Log.warn("", e);
        }
    }

    @Override
    public void dispose() {
        printStream.close();

    }
}
