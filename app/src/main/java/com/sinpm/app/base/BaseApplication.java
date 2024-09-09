package com.sinpm.app.base;

import static com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.hjq.toast.ToastUtils;
import com.sinpm.app.Utils.OKHttpUpdateHttpService;
//import com.lawman.mpush.EventMsg;
//import com.lawman.mpush.MPush;
//import com.mpush.client.ClientConfig;
//import com.tencent.bugly.crashreport.CrashReport;
import com.sinpm.app.Utils.SharedPreferencesUtil;
import com.sinpm.app.handle.CrashHandler;
import com.tencent.bugly.crashreport.CrashReport;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;

//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.AutoSizeConfig;

public class BaseApplication extends Application {
    static BaseApplication _instance;
    public static Context applicationContext;
    private static BaseApplication baseApplication = null;

    public static BaseApplication getInstance() {
        return baseApplication;
    }
//    public Boolean getConnect() {
//        if (MPush.I.hasInit()&&MPush.I.hasStarted()&&MPush.I.hasRunning()){
//            return isConnect;
//        }else {
//            return false;
//        }
//    }
//    public void setConnect(Boolean connect) {
//        isConnect = connect;
//        ConnectFlag.postValue(connect);
//    Toasty.Config.getInstance()
//            .tintIcon(boolean tintIcon) // optional (apply textColor also to the icon)
//    .setToastTypeface(@NonNull Typeface typeface) // optional
//    .setTextSize(int sizeInSp) // optional
//    .allowQueue(boolean allowQueue) // optional (prevents several Toastys from queuing)
//    .setGravity(int gravity, int xOffset, int yOffset) // optional (set toast gravity, offsets are optional)
//    .supportDarkTheme(boolean supportDarkTheme) // optional (whether to support dark theme or not)
//    .setRTL(boolean isRTL) // optional (icon is on the right)
//    .apply(); // required
//    }

    public  Object getSerialId(){


       return SharedPreferencesUtil.getData(BaseApplication.getInstance(), "SerialId", "");

    }
    public  void setSerialId(String SerialId){

         SharedPreferencesUtil.saveData(BaseApplication.getInstance(), "SerialId", SerialId);

    }

    public Long getLastHeartTime() {
        return LastHeartTime;
    }

    public void setLastHeartTime(Long lastHeartTime) {
        LastHeartTime = lastHeartTime;
    }

    private Long LastHeartTime;//上次心跳上报时间
    //mpush初始化
    private final Boolean isInitMpush = false;
    //心跳连接
    private final Boolean isConnect = false;

    public MutableLiveData<Boolean> getConnectFlag() {
        return ConnectFlag;
    }

    private final MutableLiveData<Boolean> ConnectFlag = new MutableLiveData<>();

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
        _instance = this;
        applicationContext = this;
//        EventBus.getDefault().register(this);
        ConnectFlag.setValue(false);
//autosize 初始化
        AutoSize.initCompatMultiProcess(this);
        AutoSizeConfig.getInstance()
                .setCustomFragment(true)
                .setBaseOnWidth(true)
                .setUseDeviceSize(true)
                .setLog(true);

        CrashReport.initCrashReport(getApplicationContext(), "275284cf08", false);
        ToastUtils.init(this);
        handleSSLHandshake();
        initXUpdate();

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        SharedPreferencesUtil.remove(BaseApplication.getInstance(), "SerialId");
    }




    private void initXUpdate() {
        XUpdate.get()
                .debug(true)
                .isWifiOnly(true)                                               //默认设置只在wifi下检查版本更新
                .isGet(true)                                                    //默认设置使用get请求检查版本
                .isAutoMode(false)                                              //默认设置非自动模式，可根据具体使用配置
                /*.param("versionCode", UpdateUtils.getVersionCode(this))         //设置默认公共请求参数
                .param("appKey", getPackageName())*/
                .setOnUpdateFailureListener(new OnUpdateFailureListener() {     //设置版本更新出错的监听
                    @Override
                    public void onFailure(UpdateError error) {
                        if (error.getCode() != CHECK_NO_NEW_VERSION) {          //对不同错误进行处理
                            ToastUtils.show(error.toString());
                        }
                    }
                })
                .supportSilentInstall(false)                                     //设置是否支持静默安装，默认是true
                .setIUpdateHttpService(new OKHttpUpdateHttpService())           //这个必须设置！实现网络请求功能。
                .init(this);
    }

    //    @Subscribe(threadMode = ThreadMode.BACKGROUND)
//    public void onEventMsg(EventMsg eventMsg){
//        if (eventMsg.getType()==EventMsg.TYPE_CONNECTED){
//            getInstance().setConnect(true);
//            setLastHeartTime(System.currentTimeMillis());
//        }else if (eventMsg.getType()==EventMsg.TYPE_INITED){
//            getInstance().setInitMpush(true);
//            setLastHeartTime(System.currentTimeMillis());
//        }else if (eventMsg.getType()==EventMsg.TYPE_UNCONNECT){
//            getInstance().setConnect(false);
//        }else if (eventMsg.getType()==EventMsg.TYPE_HEART){
//            //写入心跳记录
//            getInstance().setConnect(true);
//            setLastHeartTime(System.currentTimeMillis());
////            Log.e("TYPE_HEART","写入心跳记录"+System.currentTimeMillis());
//            checkHeart();
//            startCheck = true;
//        }
//    }
//    Boolean startCheck = false;
//    private void checkHeart(){
//        if (startCheck){
//            return;
//        }
//        handler.sendEmptyMessage(HEART_BEAT);
//    }
//    final static int HEART_BEAT = 6;
//    private Handler handler = new Handler(Looper.getMainLooper()){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case HEART_BEAT:
//                    long delayTime = System.currentTimeMillis() - getLastHeartTime();
////                    Log.e("HEART_BEAT",System.currentTimeMillis()+"/"+getLastHeartTime()+"/"+String.valueOf(delayTime));
//                    if (delayTime>(30*1000)){//1分钟没心跳了，重连
//                        Log.e("HEART_BEAT","30s没心跳了，正在重连");
//                        getInstance().setConnect(false);
//                        resumeMpush();
//                    }
//                    handler.sendEmptyMessageDelayed(HEART_BEAT,30*1000);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//    public Boolean getInitMpush() {
//        if (MPush.I.hasInit()&&MPush.I.hasStarted()&&MPush.I.hasRunning()){
//            return isInitMpush;
//        }else {
//            return false;
//        }
//    }
//    public Boolean getFirstInit(){
//        return isInitMpush;
//    }
//    public void setInitMpush(Boolean initMpush) {
//        isInitMpush = initMpush;
//    }
//
//    public synchronized void initMpush(String deviceCode){
//        if (getInstance().getFirstInit()){
//            resumeMpush();
//            return;
//        }
//
//        initPush(deviceCode);
//        bindUser(deviceCode);
//        getInstance().setInitMpush(true);
//    }
//    public void resumeMpush(){
//        ///网络可用的情况下的方法
//        if (MPush.I.hasStarted()) {
//            MPush.I.onNetStateChange(true);
//            MPush.I.resumePush();
//        } else {
//
//            MPush.I.checkInit(getInstance()).startPush();
//        }
//    }
//    public void bindUser(String uerid) {
//        if (getInstance().getInitMpush()){
//            return;
//        }
//        MPush.I.bindAccount(uerid, "mpush:" + (int) (Math.random() * 10));
//    }
//    public void initPush( String userId) {
//        if (getInstance().getInitMpush()){
//            return;
//        }
//        //公钥有服务端提供和私钥对应
//        ClientConfig cc = ClientConfig.build()
//                .setPublicKey(API.PUSH_KEY)
//                .setAllotServer(API.PUSH_URL)
//                .setDeviceId(userId)
//                .setClientVersion("1.0")
//                .setEnableHttpProxy(true)
//                .setUserId(userId);
//        MPush.I.checkInit(this).setClientConfig(cc);
//        MPush.I.checkInit(this).startPush();
//
////        mpush推送 根据网络情况 重连
//        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        cm.requestNetwork(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {
//            @Override
//            public void onLost(Network network) {
//                super.onLost(network);
//
//                ///网络不可用的情况下的方法
//                MPush.I.onNetStateChange(false);
//                MPush.I.pausePush();
//            }
//
//            @Override
//            public void onAvailable(Network network) {
//                super.onAvailable(network);
//                ///网络可用的情况下的方法
//                if (MPush.I.hasStarted()) {
//                    MPush.I.onNetStateChange(true);
//                    MPush.I.resumePush();
//                } else {
//                    MPush.I.checkInit(getInstance()).startPush();
//                }
//            }
//        });
//    }
    public static void handleSSLHandshake() {
        try {

            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {

                    return new X509Certificate[0];

                }

                @Override

                public void checkClientTrusted(X509Certificate[] certs, String authType) {

                }

                @Override

                public void checkServerTrusted(X509Certificate[] certs, String authType) {

                }

            }};

            SSLContext sc = SSLContext.getInstance("TLS");

            // trustAllCerts信任所有的证书

            sc.init(null, trustAllCerts, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override

                public boolean verify(String hostname, SSLSession session) {

                    return true;

                }

            });

        } catch (Exception ignored) {

        }
    }
}
