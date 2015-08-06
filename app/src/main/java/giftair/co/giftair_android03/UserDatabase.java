package giftair.co.giftair_android03;

import io.realm.RealmObject;

/**
 * Created by parkdgun on 2015-07-15.
 */
public class UserDatabase extends RealmObject {

    private String UserFirstName;
    private String UserLastName;
    private String Email;

    public String getUserFirstName() {
        return UserFirstName;
    }
    public void setUserFirstName(String userFirstName) {
        this.UserFirstName = userFirstName;
    }
    public String getUserLastName() {
        return UserLastName;
    }
    public void setUserLastName(String userLastName) {
        this.UserLastName = userLastName;
    }
    public String getEmail() {
        return Email;
    }
    public void setEmail(String email) {
        this.Email = email;
    }
}
