package cloud.sakku.docker.compose.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import cloud.sakku.docker.compose.utils.VolumeSyntaxParser;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties({"type", "source", "target", "read_only", "bind", "volume", "tmpfs", "consistency"})
public class Volume {

    @SerializedName("host")
    @JsonProperty("host")
    private String host;

    @SerializedName("container")
    @JsonProperty("container")
    private String container;

    @SerializedName("mode")
    @JsonProperty("mode")
    private String mode;


    @JsonCreator
    static Volume create(String shortHandPortSyntax) {
        return VolumeSyntaxParser.parse(shortHandPortSyntax);
    }

}
