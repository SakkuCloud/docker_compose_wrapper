package cloud.sakku.docker.compose.model;

import cloud.sakku.docker.compose.utils.ServiceSyntaxParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Service {

    @SerializedName("name")
    @JsonProperty("name")
    private String name;

    @SerializedName("alias")
    @JsonProperty("alias")
    private String alias;


    @JsonCreator
    static Service create(String link){
        return ServiceSyntaxParser.parse(link);
    }
}
