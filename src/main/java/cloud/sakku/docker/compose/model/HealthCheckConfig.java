package cloud.sakku.docker.compose.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"test"})
public class HealthCheckConfig {

    @SerializedName("disable")
    @JsonProperty(value = "disable", defaultValue = "true")
    @Builder.Default
    private boolean disable = true;

    @SerializedName("interval")
    @JsonProperty("interval")
    private String interval;

    @SerializedName("timeout")
    @JsonProperty("timeout")
    private String timeout;

    @SerializedName("retries")
    @JsonProperty("retries")
    private int retries;

    @SerializedName("start_period")
    @JsonProperty("start_period")
    private String startPeriod;

    public HealthCheckConfig(@JsonProperty("interval") String interval,
                             @JsonProperty("timeout") String timeout,
                             @JsonProperty("retries") int retries,
                             @JsonProperty("start_period") String startPeriod)
    {

        this.interval = interval;
        this.timeout = timeout;
        this.retries = retries;
        this.startPeriod = startPeriod;
        this.disable = false;

    }
}
