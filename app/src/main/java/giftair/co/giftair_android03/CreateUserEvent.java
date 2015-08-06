package giftair.co.giftair_android03;

public final class CreateUserEvent {
    public String email;
    public String password;

    public CreateUserEvent(String Email, String Password) {
        email = Email;
        password = Password;
    }
}
