package com.sinpm.serialportlibrary;


import static com.sinpm.serialportlibrary.Constants.HANDLER;
import static com.sinpm.serialportlibrary.Constants.THREAD_FACTORY;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 多串口同时使用时的管理工具
 *
 * @author pengh
 */
public class MultipleSerialPortManager {

    private static final String TAG = MultipleSerialPortManager.class.getSimpleName();

    private static final int DEFAULT_READ_DELAY = 100;
    private static final boolean DEFAULT_NEED_AVAILABLE = false;

    private static final HashMap<String, Integer> READ_DATA_DELAY_LIST = new HashMap<>();

    private static final HashMap<String, Boolean> NEED_AVAILABLE_LIST = new HashMap<>();

    private static final HashMap<String, InputStream> INPUT_STREAMS = new HashMap<>();

    private static final HashMap<String, OutputStream> OUTPUT_STREAMS = new HashMap<>();

    private static final HashMap<String, Thread> RECEIVE_DATA_THREADS = new HashMap<>();

    private static final SerialPortFinder SERIAL_PORT_FINDER = SerialPortFinder.getInstance();

    private static final HashMap<String, SerialPort> SERIAL_PORTS = new HashMap<>();

    private static final HashMap<String, OnSerialPortDataChangedListener> ON_SERIAL_PORT_DATA_CHANGED_LISTENERS = new HashMap<>();

    private static int serialPortCacheDataSize = 1024;

    private static int debugLevel = DebugLevel.OFF;

    public static void setReadDataDelay(String serialPortPath, int readDataDelay) {
        READ_DATA_DELAY_LIST.put(serialPortPath, readDataDelay);
    }

    public static String[] getAllDevices() {
        return SERIAL_PORT_FINDER.getAllDevices();
    }

    public static String[] getAllDevicesPath() {
        return SERIAL_PORT_FINDER.getAllDevicesPath();
    }

    public static boolean openSerialPort(String serialPortPath, int baudrate, @Nullable OnSerialPortDataChangedListener onSerialPortDataChangedListener) {
        if (SERIAL_PORTS.containsKey(serialPortPath)) {
            debug(TAG, "重复打开串口 [" + serialPortPath + "] ，返回false");
            return false;
        }
        try {
            SerialPort serialPort = new SerialPort(new File(serialPortPath), baudrate, 0);
            InputStream inputStream = serialPort.getInputStream();
            INPUT_STREAMS.put(serialPortPath, inputStream);
            OutputStream outputStream = serialPort.getOutputStream();
            OUTPUT_STREAMS.put(serialPortPath, outputStream);
            startReceiveDataThread(serialPortPath);
            ON_SERIAL_PORT_DATA_CHANGED_LISTENERS.put(serialPortPath, onSerialPortDataChangedListener);
            SERIAL_PORTS.put(serialPortPath, serialPort);
            return true;
        } catch (IOException | SecurityException e) {
            closeSerialPort(serialPortPath);
            return false;
        }
    }

    public static void closeSerialPort(String serialPortPath) {
        closeSerialPort(serialPortPath, true);
    }

    public static void closeAll() {
        if (SERIAL_PORTS.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, SerialPort>> entries = SERIAL_PORTS.entrySet();
        for (Map.Entry<String, SerialPort> next : entries) {
            String key = next.getKey();
            closeSerialPort(key, false);
        }

        SERIAL_PORTS.clear();
        INPUT_STREAMS.clear();
        OUTPUT_STREAMS.clear();
        RECEIVE_DATA_THREADS.clear();
    }

    private static void closeSerialPort(String serialPortPath, boolean needRemove) {
        if (!SERIAL_PORTS.containsKey(serialPortPath)) {
            return;
        }
        SerialPort serialPort = SERIAL_PORTS.get(serialPortPath);
        if (serialPort != null) {
            serialPort.close();
            if (needRemove) {
                SERIAL_PORTS.remove(serialPortPath);
            }
        }
        Integer readDelay = READ_DATA_DELAY_LIST.get(serialPortPath);
        if (readDelay != null) {
            READ_DATA_DELAY_LIST.remove(serialPortPath);
        }
        InputStream inputStream = INPUT_STREAMS.get(serialPortPath);
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (needRemove) {
                INPUT_STREAMS.remove(serialPortPath);
            }
        }
        OutputStream outputStream = OUTPUT_STREAMS.get(serialPortPath);
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (needRemove) {
                OUTPUT_STREAMS.remove(serialPortPath);
            }
        }
        if (needRemove) {
            RECEIVE_DATA_THREADS.remove(serialPortPath);
        }

    }

    public static void setSerialPortCacheDataSize(int size) {
        MultipleSerialPortManager.serialPortCacheDataSize = size;
    }

    public static boolean isOpened(String serialPortPath) {
        return SERIAL_PORTS.containsKey(serialPortPath);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean writeData(String serialPortPath, String data) {
        return writeData(serialPortPath, data, Charset.forName("GBK"));
    }

    public static boolean writeData(String serialPortPath, String data, Charset charset) {
        debug(TAG, "data = " + data + ",charset = " + charset.name());
        return writeData(serialPortPath, data.getBytes(charset));
    }

    public static void setDebugLevel(int debugLevel) {
        MultipleSerialPortManager.debugLevel = debugLevel;
    }

    public static boolean writeData(String serialPortPath, byte[] data) {
        if (!OUTPUT_STREAMS.containsKey(serialPortPath)) {
            return false;
        }
        OutputStream outputStream = OUTPUT_STREAMS.get(serialPortPath);
        if (outputStream == null) {
            return false;
        }
        debug(TAG, "data = " + SerialPortManager.byteArrayToHexStr(data));
        try {
            outputStream.write(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void setNeedAvailable(String serialPort, boolean needAvailable) {
        NEED_AVAILABLE_LIST.put(serialPort, needAvailable);
    }

    /**
     * 打印调试信息
     *
     * @param tag TAG
     * @param msg 调试信息
     */
    private static void debug(@SuppressWarnings("SameParameterValue") String tag, String msg) {

        switch (debugLevel) {
            case DebugLevel.VERBOSE:
                Log.v(tag, msg);
                break;
            case DebugLevel.DEBUG:
                Log.d(tag, msg);
                break;
            case DebugLevel.INFO:
                Log.i(tag, msg);
                break;
            case DebugLevel.WARNING:
                Log.w(tag, msg);
                break;
            case DebugLevel.ERROR:
                Log.e(tag, msg);
                break;
            case DebugLevel.OFF:
            default:
                Log.e(tag, "debug is disable");
                break;
        }
    }

    private static void startReceiveDataThread(final String serialPortPath) {
        Thread receiveDataThread = RECEIVE_DATA_THREADS.get(serialPortPath);
        if (receiveDataThread != null && receiveDataThread.isAlive()) {
            return;
        }
        Runnable runnable = () -> {
            Looper.prepare();
            byte[] buffer = new byte[serialPortCacheDataSize];
            while (true) {
                if (buffer.length != serialPortCacheDataSize) {
                    buffer = new byte[serialPortCacheDataSize];
                }
                Arrays.fill(buffer, (byte) 0);
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                if (INPUT_STREAMS.isEmpty()) {
                    RECEIVE_DATA_THREADS.remove(serialPortPath);
                    break;
                }
                InputStream inputStream = INPUT_STREAMS.get(serialPortPath);
                if (inputStream == null) {
                    RECEIVE_DATA_THREADS.remove(serialPortPath);
                    break;
                }
                Integer readDelay = READ_DATA_DELAY_LIST.get(serialPortPath);
                if (readDelay == null) {
                    READ_DATA_DELAY_LIST.put(serialPortPath, DEFAULT_READ_DELAY);
                    readDelay = DEFAULT_READ_DELAY;
                }
                Boolean needAvailable = NEED_AVAILABLE_LIST.get(serialPortPath);
                if (needAvailable == null) {
                    NEED_AVAILABLE_LIST.put(serialPortPath, DEFAULT_NEED_AVAILABLE);
                    needAvailable = DEFAULT_NEED_AVAILABLE;
                }
                debug(TAG, serialPortPath + " needAvailable = " + needAvailable);
                debug(TAG, serialPortPath + " readDelay = " + readDelay);
                SystemClock.sleep(readDelay);
                int available;
                final int size;
                try {
                    if (needAvailable) {
                        available = inputStream.available();
                        size = inputStream.read(buffer, 0, available);
                    } else {
                        size = inputStream.read(buffer);
                    }
                    if (size == 0) {
                        continue;
                    }
                    final byte[] finalBuffer = new byte[size];
                    System.arraycopy(buffer, 0, finalBuffer, 0, size);
                    HANDLER.post(() -> {
                        OnSerialPortDataChangedListener onSerialPortDataChangedListener = ON_SERIAL_PORT_DATA_CHANGED_LISTENERS.get(serialPortPath);
                        if (onSerialPortDataChangedListener != null) {
                            onSerialPortDataChangedListener.serialPortDataReceived(finalBuffer, size);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            debug(TAG,serialPortPath + " receive data thread end");
        };
        receiveDataThread = THREAD_FACTORY.newThread(runnable);
        receiveDataThread.start();
        RECEIVE_DATA_THREADS.put(serialPortPath, receiveDataThread);
    }

    private MultipleSerialPortManager() {
    }
}
