package cloud.sakku.docker.compose.model;

import cloud.sakku.docker.compose.utils.DockerShortSyntaxParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.annotations.SerializedName;
import cloud.sakku.docker.compose.exception.UnsupportedSyntaxException;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"cache_from", "shm_size", "target"})
@Getter
@Setter
public class Build {

    @SerializedName("context")
    @JsonProperty("context")
    private String context;

    @JsonSetter("context")
    public void setContext(String context) {

        if (context.startsWith(".")) {
            throw UnsupportedSyntaxException.getInstance("relative path to host is not allowed", "build.context", context);
        }

        this.context = context;
    }

    @SerializedName("dockerfile")
    @JsonProperty("dockerfile")
    private String dockerfile;

    @SerializedName("args")
    @Builder.Default
    private Map<String, Object> args = new HashMap<>();

    @JsonSetter("args")
    public void setArgs(Object args) {

        if (args instanceof Map) {

            this.args.putAll(Map.class.cast(args));

        } else if (args instanceof List) {

            this.args.putAll(DockerShortSyntaxParser.parse(List.class.cast(args)));

        } else {

            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "build.args", args.toString());

        }
    }

    @SerializedName("labels")
    @Builder.Default
    private Map<String, Object> labels = new HashMap<>();

    @JsonSetter("labels")
    public void setLabels(Object labels) {

        if (labels instanceof Map) {

            this.labels.putAll(Map.class.cast(labels));

        } else if (labels instanceof List) {

            this.labels.putAll(DockerShortSyntaxParser.parse(List.class.cast(labels)));

        } else {

            throw UnsupportedSyntaxException.getInstance("unsupported syntax", "build.labels", labels.toString());

        }
    }

    @JsonCreator
    static Build create(String context) {

        if (context.startsWith(".")) {
            throw UnsupportedSyntaxException.getInstance("relative path to host is not allowed", "build", context);
        }

        return Build.builder()
                .context(context)
                .build();
    }
}
