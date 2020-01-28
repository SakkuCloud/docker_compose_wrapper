package cloud.sakku.docker.compose.utils;

import cloud.sakku.docker.compose.model.Image;

public final class ImageSyntaxParser {

    public static Image parse(String image) {

        String[] parts = image.split("/");
        Image.ImageBuilder builder = Image.builder();

        if (parts.length == 1) {
            parts = image.split(":");
            builder.name(parts[0]);
            if (parts.length >= 2)
                builder.tag(parts[1]);

            return builder.build();
        }

        // if parts length greater than 1
        builder.repository(parts[0]);
        builder.tag(parts[1]);

        return builder.build();

    }

}
