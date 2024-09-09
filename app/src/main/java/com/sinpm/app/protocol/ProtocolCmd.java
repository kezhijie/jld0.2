package com.sinpm.app.protocol;

public interface ProtocolCmd {
    int OPEN_PORT_SUCCESSS = 200;
    int OPEN_PORT_FAIL = 404;
    int CHANGE_VALUE = 5;
    int QUARY_TEMP = 2;
    int OPEN = 6;

    int ERRO_CODE = 500;
    // A6 6A 07 83 03 03 01 05 05 05  A6 6A 06 83 08 08 01 00 02
    String UP_ENERGY = "AA 78 05".replace(" ", "");
    String DOWN_ENERGY = "AA 78 06".replace(" ", "");

    String GNB_ENERGY = "AA 78 07".replace(" ", "");
    String START = "AA 78 03 63".replace(" ", "");
    String PAUSE = "AA 78 03 64".replace(" ", "");
    String RESET = "AA 78 50 00".replace(" ", "");
    String NBYY = "AA 78 B0".replace(" ", "");
    String CYXD = "AA 78 B1".replace(" ", "");
    String GNB = "AA 78 B2".replace(" ", "");
    String LZD = "AA 78 B3".replace(" ", "");
    String XCQG = "AA 78 B4".replace(" ", "");
    String FYLZ = "AA 78 B5".replace(" ", "");
    String THZ = "AA 78 B6".replace(" ", "");
    String FWD = "AA 78 B7".replace(" ", "");
    String TEMP = "AA 78 C0";
    String TEMP_QUARY = "AA 78 C0 00".replace(" ", "");
    String INIT = "AA 78 01 00".replace(" ", "");


    String HEAD = "A8 58 ";
    String END = " CC 33 C3 3C";

}
