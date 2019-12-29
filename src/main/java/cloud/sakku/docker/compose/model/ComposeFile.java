package cloud.sakku.docker.compose.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"volumes", "configs", "secrets", "networks"})
@Getter
@Setter
public class ComposeFile {

    @SerializedName("version")
    @JsonProperty("version")
    private String version;

    @Builder.Default
    private Map<String, ServiceSpec> services = new HashMap<>();

}
