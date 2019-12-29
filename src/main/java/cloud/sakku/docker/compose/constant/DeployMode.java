package cloud.sakku.docker.compose.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DeployMode {

    @JsonProperty("replicated")
    REPLICATED("replicated"),

    @JsonProperty("global")
    GLOBAL("global");

    String value;

    DeployMode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue(){
        return value;
    }
}
