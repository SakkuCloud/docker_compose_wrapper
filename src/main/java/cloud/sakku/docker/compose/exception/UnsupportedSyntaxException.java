package cloud.sakku.docker.compose.exception;

public class UnsupportedSyntaxException extends RuntimeException {


    private String field;
    private String value;

    public UnsupportedSyntaxException(String message, String field, String value) {

        super(message + " [" + field + ": " + value + "]");

        this.field = field;
        this.value = value;
    }

    public static UnsupportedSyntaxException getInstance(String message, String field, String value) {
        return new UnsupportedSyntaxException(message, field, value);
    }


    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

}
