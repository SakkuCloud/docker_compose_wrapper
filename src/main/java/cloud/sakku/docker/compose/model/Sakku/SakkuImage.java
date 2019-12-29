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
public class SakkuImage {

    @SerializedName("name")
    @JsonProperty("name")
    private String name;

    @SerializedName("registry")
    @JsonProperty(value = "registry", defaultValue = "dockerhub")
    @Builder.Default
    private String registry = "dockerhub";

    @SerializedName("username")
    @JsonProperty(value = "username", defaultValue = "")
    @Builder.Default
    private String username = "";

    @SerializedName("accessToken")
    @JsonProperty(value = "accessToken", defaultValue = "")
    @Builder.Default
    private String accessToken = "";
}
