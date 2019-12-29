package cloud.sakku.docker.compose.model.Sakku;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SakkuModule {

    @SerializedName("appId")
    @JsonProperty("appId")
    private long appId;

    @SerializedName("code")
    @JsonProperty("code")
    private long code;

    @SerializedName("metadata")
    @JsonProperty("metadata")
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();


    public static SakkuModule createReplicatedStorageModule(String appPath){

        Map<String, Object> metadata = new HashMap<>();

        metadata.put("appPath", appPath);

        return SakkuModule.builder()
                .appId(0)
                .code(50) // Storage : REPLICATED
                .metadata(metadata)
                .build();
    }

}
