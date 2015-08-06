package giftair.co.giftair_android03;

import io.realm.RealmObject;

/**
 * Created by parkdgun on 2015-07-13.
 */
public class Database extends RealmObject {

    private String DeviceName;
    private String MacAddr;
    private String Enrollment;
    private boolean AutoCheck;

    public String getDeviceName() {
        return DeviceName;
    }
    public void setDeviceName(String deviceName) {
        this.DeviceName = deviceName;
    }
    public String getMacAddr() {
        return MacAddr;
    }
    public void setMacAddr(String macAddr) {
        this.MacAddr = macAddr;
    }
    public String getEnrollment() {
        return Enrollment;
    }
    public void setEnrollment(String enrollment) {
        this.Enrollment = enrollment;
    }
    public boolean isAutoCheck() {
        return AutoCheck;
    }
    public void setAutoCheck(boolean autoCheck) {
        this.AutoCheck = autoCheck;
    }
}
