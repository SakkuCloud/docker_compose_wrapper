package cloud.sakku.docker.compose.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Resources {
    @SerializedName("cpus")
    @JsonProperty("cpus")
    private Double cpus;

    @SerializedName("memory")
    @JsonProperty("memory")
    private String memory;
}
