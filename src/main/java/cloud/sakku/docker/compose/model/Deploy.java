package cloud.sakku.docker.compose.model;

import cloud.sakku.docker.compose.constant.DeployMode;
import cloud.sakku.docker.compose.exception.UnsupportedSyntaxException;
import cloud.sakku.docker.compose.utils.DockerShortSyntaxParser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"endpoint_mode", "placement", "rollback_config", "update_config"})
public class Deploy {

    @JsonProperty(value = "mode", defaultValue = "replicated")
    @SerializedName("mode")
    @Builder.Default
    private DeployMode mode = DeployMode.REPLICATED;

    @SerializedName("replicas")
    @JsonProperty(value = "replicas")
    private long replicas;

    @SerializedName("resources")
    @JsonProperty("resources")
    private ResourceSpec resources;

    @SerializedName("restart_policy")
    @JsonProperty("restart_policy")
    private RestartPolicy restartPolicy;

    @SerializedName("labels")
    @Builder.Default
    private Map<String, Object> labels = new HashMap<>();

    @JsonSetter("labels")
    public void setLabels(Object labels) {
        if (labels instanceof Map) {
            this.labels.putAll((Map) labels);
        } else if (labels instanceof List) {
            this.labels.putAll(DockerShortSyntaxParser.parse((List) labels));
        } else {
            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "deploy.labels", labels.toString());
        }
    }


}
