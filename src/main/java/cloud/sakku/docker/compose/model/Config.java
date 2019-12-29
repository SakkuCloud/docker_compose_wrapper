package cloud.sakku.docker.compose.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Config {

    @SerializedName("source")
    @JsonProperty("source")
    private String source;

    @SerializedName("target")
    @JsonProperty("target")
    private String target;

    @SerializedName("uid")
    @JsonProperty(value = "uid", defaultValue = "0")
    @Builder.Default
    private int uid = 0;

    @SerializedName("gid")
    @JsonProperty(value = "gid", defaultValue = "0")
    @Builder.Default
    private int gid = 0;

    @SerializedName("mode")
    @JsonProperty(value = "mode", defaultValue = "0444")
    private String mode;

    @JsonCreator
    static Config create(String configSource) {
        return Config.builder()
                .source(configSource)
                .build();
    }

}
