package giftair.co.giftair_android03;

/**
 * Created by parkdgun on 2015-07-22.
 */
public class HealthItem {

    private String Title;
    private String Date;
    private String Data;

    public HealthItem(String Title, String Date, String Data) {
        this.Title = Title;
        this.Date = Date;
        this.Data = Data;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
}
