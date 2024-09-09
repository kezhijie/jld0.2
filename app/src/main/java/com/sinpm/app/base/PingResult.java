package com.sinpm.app.base;

import android.util.Log;

public class PingResult {
    public final String result;
    public final String ip;
    public final String address;
    public final int interval;
    private static final String LAST_LINE_PREFIX = "rtt min/avg/max/mdev = ";
    private static final String PACKET_WORDS = " packets transmitted";
    private static final String RECEIVED_WORDS = " received";
    private static final String LOSS_WORDS = "% packet loss";
    public int sent;
    public int packetLoss = -1;
    public int dropped;
    public float max;
    public float min;
    public float avg;
    public float stddev;
    public int count;
    public int avgNumber;
    public int lostNumber;

    public PingResult(String result, String address, String ip, int interval) {
        this.result = result;
        this.ip = ip;
        this.interval = interval;
        this.address = address;
        parseResult();
    }

    static String trimNoneDigital(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char[] v = s.toCharArray();
        char[] v2 = new char[v.length];
        int j = 0;
        for (char aV : v) {
            if ((aV >= '0' && aV <= '9') || aV == '.') {
                v2[j++] = aV;
            }
        }
        return new String(v2, 0, j);
    }

    private void parseRttLine(String s) {
        String s2 = s.substring(LAST_LINE_PREFIX.length(), s.length() - 3);
        String[] l = s2.split("/");
        if (l.length != 4) {
            return;
        }
        min = Float.parseFloat(trimNoneDigital(l[0]));
        avg = Float.parseFloat(trimNoneDigital(l[1]));
        max = Float.parseFloat(trimNoneDigital(l[2]));
        stddev = Float.parseFloat(trimNoneDigital(l[3]));
    }

    private void parsePacketLine(String s) {
        String[] l = s.split(",");
        if (l.length != 4) {
            return;
        }
        if (l[0].length() > PACKET_WORDS.length()) {
            String s2 = l[0].substring(0, l[0].length() - PACKET_WORDS.length());
            count = Integer.parseInt(s2);
        }
        if (l[1].length() > RECEIVED_WORDS.length()) {
            String s3 = l[1].substring(0, l[1].length() - RECEIVED_WORDS.length());
            sent = Integer.parseInt(s3.trim());
        }
        if (l[2].length() > LOSS_WORDS.length()) {
            String s4 = l[2].substring(0, l[2].length() - LOSS_WORDS.length());
            packetLoss = Integer.parseInt(s4.trim());
        }
        dropped = count - sent;
    }

    private void parseResult() {
        String[] rs = result.split("\n");
        try {
            for (String s : rs) {
                if (s.contains(PACKET_WORDS)) {
                    parsePacketLine(s);
                } else if (s.contains(LAST_LINE_PREFIX)) {
                    parseRttLine(s);
                }
            }
        } catch (Exception e) {
            Log.e("PING_RESULT", "parseResult", e);
        }

    }

    public String getAvg() {

        if (avg == 1) {
            avgNumber = 0;
        } else if (avg > 1 && avg <= 15) {
            avgNumber = 1;
        } else if (avg > 15 && avg <= 55) {
            avgNumber = 2;
        } else if (avg > 55 && avg <= 140) {
            avgNumber = 3;
        } else if (avg > 140 && avg <= 420) {
            avgNumber = 4;
        } else if (avg > 420) {
            avgNumber = 5;
        } else {
            avgNumber = 6;
        }
        if (packetLoss == 0) {
            lostNumber = 0;
        } else if (packetLoss > 0 && packetLoss <= 2) {
            lostNumber = 1;
        } else if (packetLoss > 2 && packetLoss <= 5) {
            lostNumber = 2;
        } else if (packetLoss > 5 && packetLoss <= 8) {
            lostNumber = 3;
        } else if (packetLoss > 8 && packetLoss <= 13) {
            lostNumber = 4;
        } else if (packetLoss > 13) {
            lostNumber = 5;
        } else {
            lostNumber = 6;
        }

        Log.e("ping", "avg: " + avg + "  packetLoss: " + packetLoss);

        /*if (lostNumber > avgNumber) {
            return PingQuality.getDescription(lostNumber);
        } else {
            return PingQuality.getDescription(avgNumber);
        }*/
        return String.valueOf(avg);
    }
}
