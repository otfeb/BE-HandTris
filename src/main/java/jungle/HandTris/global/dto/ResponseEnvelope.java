package jungle.HandTris.global.dto;

public record ResponseEnvelope<T>(String code, T data, String message) {
    public static <T> ResponseEnvelope<T> of(T data) {
        return new ResponseEnvelope<>("200", data, null);
    }

    public static <T> ResponseEnvelope<T> of(String code, T data, String message) {
        return new ResponseEnvelope<>(code, data, message);
    }
}
