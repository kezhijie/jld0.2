package com.sinpm.app.protocol;

import android.os.Handler;

public interface IService {

    void startService(Handler handler);

    void sendData(byte[] bytes);

    void stopService();

}
