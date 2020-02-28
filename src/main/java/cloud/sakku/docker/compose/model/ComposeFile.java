package cloud.sakku.docker.compose.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"volumes", "configs", "secrets"})
@Getter
@Setter
public class ComposeFile {
    @SerializedName("version")
    @JsonProperty("version")
    private String version;

    @SerializedName("services")
    @JsonProperty("services")
    @Builder.Default
    private Map<String, ServiceSpec> services = new HashMap<>();

    @SerializedName("networks")
    @JsonProperty("networks")
    @Builder.Default
    private Map<String, NetworkSpec> networks = new HashMap<>();
}
