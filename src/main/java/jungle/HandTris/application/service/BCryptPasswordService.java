package jungle.HandTris.application.service;

public interface BCryptPasswordService {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
