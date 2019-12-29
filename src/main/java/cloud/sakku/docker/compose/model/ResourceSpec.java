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
public class ResourceSpec {

    @SerializedName("limits")
    @JsonProperty("limits")
    private Resources limits;

    @SerializedName("reservations")
    @JsonProperty("reservations")
    private Resources reservations;

}
