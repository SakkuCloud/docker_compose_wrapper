package cloud.sakku.docker.compose.model;

import cloud.sakku.docker.compose.constant.PortMode;
import cloud.sakku.docker.compose.constant.Protocol;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import cloud.sakku.docker.compose.utils.PortShortSyntaxParser;
import lombok.*;

import java.io.IOException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PortConfig {

    @SerializedName("target")
    @JsonProperty("target")
    private String target; // the port inside the container

    @SerializedName("published")
    @JsonProperty("published")
    private String published; // the publicly exposed port

    @SerializedName("protocol")
    @JsonProperty("protocol")
    private Protocol protocol;

    @SerializedName("mode")
    @JsonProperty("mode")
    private PortMode mode;


    @JsonCreator
    static PortConfig create(String shortHandPortSyntax) throws IOException {
        return PortShortSyntaxParser.parse(shortHandPortSyntax);
    }

    @JsonCreator
    static PortConfig create(Integer containerPort) {
        return builder().target(containerPort.toString()).build();
    }

}
