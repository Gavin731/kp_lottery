package com.kp.lottery.kplib.utils;

import com.kp.lottery.kplib.app.KPApplication;

public enum NetworkQuality {

    NET_LOSS(100),
    NET_SLOWER(50),
    NET_SMOOTH(0);

    public int percentage;

    NetworkQuality(int percentage) {
        this.percentage = percentage;
    }

    private static NetworkQuality current;

    public synchronized static NetworkQuality current() {
        if(current != null) return current;

        int perc = PreferenceUtils.getPrefInt(KPApplication.getInstance(), "percentage", 0);
        if(perc >= 100) {
            current = NET_LOSS;
        } else if(perc >= 50) {
            current = NET_SLOWER;
        } else {
            current = NET_SMOOTH;
        }
        return current;
    }

    public synchronized static void setNetworkQuality(NetworkQuality quality) {
        current = quality;
        PreferenceUtils.setPrefInt(KPApplication.getInstance(), "percentage", quality.percentage);
    }
}
