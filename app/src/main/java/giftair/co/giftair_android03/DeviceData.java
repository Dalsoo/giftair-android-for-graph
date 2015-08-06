package giftair.co.giftair_android03;

/**
 * Created by parkdgun on 2015-07-14.
 */
public class DeviceData {
    private String ImageLocal;
    private String DeviceName;
    private String MacAddress;
    private boolean AutoCheck;

    public DeviceData(String ImageLocal, String DeviceName, String MacAddress, Boolean AutoCheck) {
        this.ImageLocal = ImageLocal;
        this.DeviceName = DeviceName;
        this.MacAddress = MacAddress;
        this.AutoCheck = AutoCheck;
    }


    public String getDeviceName() {
        return DeviceName;
    }
    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }
    public String getMacAddress() {
        return MacAddress;
    }
    public void setMacAddress(String macAddress) {
        MacAddress = macAddress;
    }
    public String getImageLocal() {
        return ImageLocal;
    }
    public void setImageLocal(String imageLocal) {
        ImageLocal = imageLocal;
    }
    public boolean isAutoCheck() {
        return AutoCheck;
    }
    public void setAutoCheck(boolean autoCheck) {
        AutoCheck = autoCheck;
    }
}
