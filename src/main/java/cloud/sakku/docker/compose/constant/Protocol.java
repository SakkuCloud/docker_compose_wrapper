package cloud.sakku.docker.compose.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Protocol {

    @JsonProperty("http")
    HTTP("http"),

    @JsonProperty("tcp")
    TCP("tcp"),

    @JsonProperty("udp")
    UDP("udp");

    String value;

    Protocol(String value) {
        this.value = value;
    }
}
