package cloud.sakku.docker.compose.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Network {
    @SerializedName("name")
    @JsonProperty("name")
    private String name;
    @SerializedName("aliases")
    @JsonProperty("aliases")
    private List<String> aliases;
}
