package cloud.sakku.docker.compose.model.Sakku;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SakkuPort {

    @SerializedName("port")
    @JsonProperty("port")
    private String port;

    @SerializedName("protocol")
    @JsonProperty(value = "protocol", defaultValue = "HTTP")
    @Builder.Default
    private String protocol = "HTTP";

    @SerializedName("ssl")
    @JsonProperty(value = "ssl", defaultValue = "false")
    @Builder.Default
    private boolean ssl = false;

    @SerializedName("onlyInternal")
    @JsonProperty(value = "onlyInternal", defaultValue = "false")
    @Builder.Default
    private boolean onlyInternal = false;

}
