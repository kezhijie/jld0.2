package com.sinpm.app.Utils;

//import com.mpush.util.crypto.MD5Utils;

//import com.mpush.util.crypto.MD5Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * @ClassName ASignUtil
 * @Description TODO
 * @Author feng
 * @DATE 2022/8/12 14:40
 * @Version 1.0
 */
public class ASignUtil {

    private static final String DEFAULT_SECRET = "1qaz@WSX#$%&";

    public static String sign(Map<String,Object> params, String timeStamp) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> list = new ArrayList<>(params.keySet());
        Collections.sort(list);
        for (String key : list) {
            sb.append(key).append("=").append(params.get(key)).append('#');
        }
        sb.append("timeStamp").append("=").append(timeStamp).append('#');
        /*if (params!=null) {
            params.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(key -> {
                        sb.append(key.getKey()).append("=").append(key.getValue()).append('#');
                    });
        }*/
        String join = String.join("#", DEFAULT_SECRET, sb.toString());


        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] inputBytes = join.getBytes();
        byte[] hashBytes = md.digest(inputBytes);

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }



//    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void main(String[] args){
        /*Map<String,Object> params = new HashMap<>();
        params.put("client","gs");
        params.put("id",1);
        params.put("name","feng");
        Integer[] i = {1,2,3};
        params.put("test", Arrays.toString(i));
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("name","feng1");
        params.put("test2",jsonObject.toString());
        String sign = sign(params);
        System.out.println(sign);*/

    }
}
