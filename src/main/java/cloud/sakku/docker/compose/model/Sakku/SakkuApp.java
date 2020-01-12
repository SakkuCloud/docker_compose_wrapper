package cloud.sakku.docker.compose.model.Sakku;

import cloud.sakku.docker.compose.model.Service;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SakkuApp {

    @SerializedName("name")
    @JsonProperty("name")
    private String name;

    @SerializedName("mem")
    @JsonProperty(value = "mem", defaultValue = "0.1")
    @Builder.Default
    private double mem = 0.1;

    @SerializedName("cpu")
    @JsonProperty(value = "cpu", defaultValue = "0.1")
    @Builder.Default
    private double cpu = 0.1;

    @SerializedName("disk")
    @JsonProperty(value = "disk", defaultValue = "1")
    @Builder.Default
    private int disk = 1;

    @SerializedName("image")
    @JsonProperty("image")
    private SakkuImage image;

    @SerializedName("ports")
    @JsonProperty("ports")
    @Builder.Default
    private List<SakkuPort> ports = new ArrayList<>();

    @SerializedName("cmd")
    @JsonProperty(value = "cmd", defaultValue = "null")
    @Builder.Default
    private String cmd = null;

    @SerializedName("scalingMode")
    @JsonProperty(value = "scalingMode", defaultValue = "OFF")
    @Builder.Default
    private String scalingMode = "OFF";

    @SerializedName("args")
    @JsonProperty("args")
    @Builder.Default
    private List<String> args = new ArrayList<>();

    @SerializedName("environments")
    @JsonProperty("environments")
    @Builder.Default
    private Map<String, Object> environments = new HashMap<>();

    @SerializedName("labels")
    @JsonProperty("labels")
    @Builder.Default
    private Map<String, Object> labels = new HashMap<>();

    @SerializedName("links")
    @JsonProperty("links")
    @Builder.Default
    private List<Service> links = new ArrayList<>();

    @SerializedName("deployType")
    @JsonProperty(value = "deployType", defaultValue = "DOCKER_IMAGE")
    @Builder.Default
    private String deployType = "DOCKER_IMAGE";

    @SerializedName("minInstance")
    @JsonProperty(value = "minInstance", defaultValue = "1")
    @Builder.Default
    private long minInstance = 1;

    @SerializedName("maxInstance")
    @JsonProperty(value = "maxInstance", defaultValue = "2")
    @Builder.Default
    private long maxInstance = 2;

    @SerializedName("dependsOn")
    @JsonProperty("dependsOn")
    @Builder.Default
    private List<String> dependsOn = new ArrayList<>();

    @SerializedName("modules")
    @JsonProperty("modules")
    @Builder.Default
    private List<SakkuModule> modules = new ArrayList<>();
}
