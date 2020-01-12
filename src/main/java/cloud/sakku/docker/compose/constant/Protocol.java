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

    public String getValue() {
        return value;
    }

    public static Protocol find(String value){
        for (Protocol protocol:values()){
            if (protocol.getValue().equals(value))
                return protocol;
        }

        return null;
    }


}
