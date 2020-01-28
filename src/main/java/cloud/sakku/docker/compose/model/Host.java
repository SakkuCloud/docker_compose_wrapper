package cloud.sakku.docker.compose.model;

import cloud.sakku.docker.compose.utils.HostSyntaxParser;
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
public class Host {

    @SerializedName("name")
    @JsonProperty("name")
    private String name;

    @SerializedName("ip")
    @JsonProperty("ip")
    private String ip;

    @JsonCreator
    static Host create(String host) {
        return HostSyntaxParser.parse(host);
    }
}
