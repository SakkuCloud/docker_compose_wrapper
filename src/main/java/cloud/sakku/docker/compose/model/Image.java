package cloud.sakku.docker.compose.model;

import cloud.sakku.docker.compose.utils.ImageSyntaxParser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Image {

    @SerializedName("name")
    @JsonProperty("name")
    private String name;

    @SerializedName("repository")
    @JsonProperty("repository")
    private String repository;

    @SerializedName("tag")
    @JsonProperty("tag")
    private String tag;

    @JsonCreator
    static Image create(String image) {
        return ImageSyntaxParser.parse(image);
    }

    @Override
    public String toString() {

        String image = "";

        if (Objects.nonNull(name) && !name.isEmpty()) {

            image += name;

            if(Objects.nonNull(tag) && !tag.isEmpty())
                image += ":" + tag;


        } else if (Objects.nonNull(repository) && !repository.isEmpty()) {

            image += repository;

            if(Objects.nonNull(tag) && !tag.isEmpty())
                image += "/" + tag;

        }

        return image;
    }
}
