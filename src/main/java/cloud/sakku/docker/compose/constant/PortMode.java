package cloud.sakku.docker.compose.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PortMode {

    // host for publishing a host port on each node
    @JsonProperty("host")
    HOST("host"),

    // ingress for a swarm mode port to be load balanced.
    @JsonProperty("ingress")
    INGRESS("ingress");

    String value;

    PortMode(String value) {
        this.value = value;
    }
}
