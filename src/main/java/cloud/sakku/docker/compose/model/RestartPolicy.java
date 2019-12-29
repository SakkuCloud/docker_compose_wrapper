package cloud.sakku.docker.compose.model;

import cloud.sakku.docker.compose.constant.RestartPolicyCondition;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RestartPolicy {

    @SerializedName("condition")
    @JsonProperty(value = "condition", defaultValue = "any")
    @Builder.Default
    private RestartPolicyCondition condition = RestartPolicyCondition.ANY;

    @SerializedName("delay")
    @JsonProperty(value = "delay", defaultValue = "0s")
    @Builder.Default
    private String delay = "0s";

    @SerializedName("max_attempts")
    @JsonProperty(value = "max_attempts", defaultValue = "never give up")
    @Builder.Default
    private String maxAttempts = "never give up";

    @SerializedName("window")
    @JsonProperty(value = "window", defaultValue = "decide immediately")
    @Builder.Default
    private String window = "decide immediately";

}
