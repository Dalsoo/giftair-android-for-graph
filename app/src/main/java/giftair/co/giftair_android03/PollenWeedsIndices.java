package giftair.co.giftair_android03;

import android.os.Handler;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by parkdgun on 2015-07-22.
 */
public class PollenWeedsIndices extends Thread {

    static public boolean active = false;
    int data = 0;
    public boolean isreceiver;
    String sCode, sAreaNo, sDate, sToday, sTomorrow, stheDayAfterTomorrow;
    boolean bCode, bAreaNo, bDate, bToday, bTomorrow, btheDayAfterTomorrow;
    boolean tResponse;
    String AreaNo = "AreaNo=1100000000";
    Handler handler;
    String ServiceKey = "ServiceKey=moZbMJ%2BdqsCWbUfUXrVBYJCbox4xa%2B0mERLFpSexsmcnDeUQ5bDUujQBWRPlu%2FSuMu6lb8d6oS2MM%2FUKgFsEVg%3D%3D";
    String getInfo = "http://203.247.66.146/iros/RetrieveWhoIndexService/getFlowerWeedsWhoList?";
    int Frag = 0;

    public PollenWeedsIndices(boolean receiver, int Frag) {
        this.handler = new Handler();
        this.isreceiver = receiver;
        this.Frag = Frag;

        bCode = bAreaNo = bDate = bToday = bTomorrow = btheDayAfterTomorrow = false;
    }

    public void run() {
        if (active) {
            try {
                data = 0;

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                String ColdUrl = getInfo + AreaNo + "&" + ServiceKey;
                URL url = new URL(ColdUrl);
                InputStream is = url.openStream();
                xpp.setInput(is, "UTF-8");

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (xpp.getName().equals("code")) {
                                bCode = true;
                            }
                            if (xpp.getName().equals("areaNo")) {
                                bAreaNo = true;
                            }
                            if (xpp.getName().equals("date")) {
                                bDate = true;
                            }
                            if (xpp.getName().equals("today")) {
                                bToday = true;
                            }
                            if (xpp.getName().equals("tomorrow")) {
                                bTomorrow = true;
                            }
                            if (xpp.getName().equals("theDayAfterTomorrow")) {
                                btheDayAfterTomorrow = true;
                            }
                            break;
                        case XmlPullParser.TEXT:
                            if (bCode) {
                                sCode = xpp.getText();
                                bCode = false;
                            }
                            if (bAreaNo) {
                                sAreaNo = xpp.getText();
                                bAreaNo = false;
                            }
                            if (bDate) {
                                sDate = xpp.getText();
                                bDate = false;
                            }
                            if (bToday) {
                                sToday = xpp.getText();
                                bToday = false;
                            }
                            if (bTomorrow) {
                                sTomorrow = xpp.getText();
                                bTomorrow = false;
                            }
                            if (btheDayAfterTomorrow) {
                                stheDayAfterTomorrow = xpp.getText();
                                btheDayAfterTomorrow = false;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (xpp.getName().equals("Response")) {
                                tResponse = true;
                                view_text();
                            }
                            if (xpp.getName().equals("IndexModel")) {
                                data++;
                            }
                            break;
                    }
                    eventType = xpp.next();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void view_text() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                active = false;
                if(tResponse) {
                    tResponse = false;
                    data = 0;
                    if(Frag == 1) {
                        HealthFragment.PollenWeedsThreadResponse(sCode, sAreaNo, sDate, sToday, sTomorrow, stheDayAfterTomorrow);
                    }
                }
            }
        });
    }
}
