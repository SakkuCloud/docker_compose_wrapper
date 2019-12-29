package cloud.sakku.docker.compose.exception;

public class UnsupportedSyntaxException extends RuntimeException {

    private String _message;

    private String _field;

    private String _value;

    public UnsupportedSyntaxException(String _message, String _field, String _value) {

        super("[" + _field + ": " +_value + "] " + _message );

        this._message = _message;
        this._field = _field;
        this._value = _value;
    }

    public String getMessage() {
        return _message;
    }

    public String getField(){ return _field;}

    public String getValue() {return _value;}


    public String toString(){
        return "[" + _field + ": " +_value + "] " + _message;
    }
}
