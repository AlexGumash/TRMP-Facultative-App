package alexey.com.facultativeapp.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;


public class Instruments {

    /*
    Данный класс содержит методы,
    которые не могут быть отнесены к другим классам.
    Методы вспомогательные, поэтому находятся
    в отдельном классе
     */

    /*
    Пара методов для взятия скриншота.
    В качестве аргумента выступает View,
    который в даннный момент находится на экране
     */
    private static Bitmap takeScreenshot(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return b;
    }

    public static Bitmap takeScreenshotOfRootView(View v) {
        return takeScreenshot(v.getRootView());
    }

    /*
    Методы для получения IP-адресса по мобильной сети
    и при подключении к Wi-Fi
     */
    public static String getMobileIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        return  addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            Log.d("Log", "Ошибка при получении адреса мобильной сети");
        }
        return "";
    }

    public static String getWifiIPAddress(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return  Formatter.formatIpAddress(ip);
    }
}
