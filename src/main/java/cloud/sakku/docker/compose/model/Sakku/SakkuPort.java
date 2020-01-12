package cloud.sakku.docker.compose.model.Sakku;

import cloud.sakku.docker.compose.constant.Protocol;
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
    @JsonProperty(value = "protocol", defaultValue = "http")
    @Builder.Default
    private Protocol protocol = Protocol.HTTP;

    @SerializedName("ssl")
    @JsonProperty(value = "ssl", defaultValue = "false")
    @Builder.Default
    private boolean ssl = false;

    @SerializedName("onlyInternal")
    @JsonProperty(value = "onlyInternal", defaultValue = "false")
    @Builder.Default
    private boolean onlyInternal = false;

}
