package giftair.co.giftair_android03;

/**
 * Created by parkdgun on 2015-07-10.
 */
public class DataEvent {

    private String FineDust;
    private String Totalvoc;
    private String Co2;
    private String Tem;
    private String Hum;
    private String Atm;
    private String Battery;

    public DataEvent(String FineDust, String Totalvoc, String Co2, String Tem, String Hum, String Atm, String Battery) {
        this.FineDust = FineDust;
        this.Totalvoc = Totalvoc;
        this.Co2 = Co2;
        this.Tem = Tem;
        this.Hum = Hum;
        this.Atm = Atm;
        this.Battery = Battery;
    }

    public String getFineDust() {
        return FineDust;
    }

    public void setFineDust(String fineDust) {
        FineDust = fineDust;
    }

    public String getTotalvoc() {
        return Totalvoc;
    }

    public void setTotalvoc(String totalvoc) {
        Totalvoc = totalvoc;
    }

    public String getCo2() {
        return Co2;
    }

    public void setCo2(String co2) {
        Co2 = co2;
    }

    public String getTem() {
        return Tem;
    }

    public void setTem(String tem) {
        Tem = tem;
    }

    public String getHum() {
        return Hum;
    }

    public void setHum(String hum) {
        Hum = hum;
    }

    public String getAtm() {
        return Atm;
    }

    public void setAtm(String atm) {
        Atm = atm;
    }

    public String getBattery() {
        return Battery;
    }

    public void setBattery(String battery) {
        Battery = battery;
    }
}
