package cloud.sakku.docker.compose.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RestartPolicyCondition {
    @JsonProperty("none")
    NONE("none"),

    @JsonProperty("any")
    ANY("any"),

    @JsonProperty("on-failure")
    ON_FAILURE("on-failure");

    String value;

    RestartPolicyCondition(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue(){
        return value;
    }
}
