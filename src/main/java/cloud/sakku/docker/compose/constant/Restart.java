package cloud.sakku.docker.compose.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Restart {
    @JsonProperty("no")
    NO("no"),

    @JsonProperty("always")
    ALWAYS("always"),

    @JsonProperty("on-failure")
    ON_FAILURE("on-failure"),

    @JsonProperty("unless-stopped")
    UNLESS_STOPPED("unless-stopped");

    String value;

    Restart(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue(){
        return value;
    }
}
