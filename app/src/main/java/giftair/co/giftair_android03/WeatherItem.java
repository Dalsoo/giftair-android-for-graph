package giftair.co.giftair_android03;

/**
 * Created by parkdgun on 2015-07-27.
 */
public class WeatherItem {

    private String Title;
    private String Data;

    public WeatherItem(String Title, String Data) {
        this.Title = Title;
        this.Data = Data;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
}
