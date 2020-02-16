package cloud.sakku.docker.compose.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties({"driver", "external", "driver_opts", "attachable", "enable_ipv6", "config", "ipam", "internal", "labels", "external"})
public class NetworkSpec {
    @SerializedName("name")
    @JsonProperty("name")
    private String name;
}
