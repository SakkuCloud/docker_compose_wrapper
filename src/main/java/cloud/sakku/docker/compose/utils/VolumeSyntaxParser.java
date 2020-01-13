package cloud.sakku.docker.compose.utils;


import cloud.sakku.docker.compose.model.Volume;
import cloud.sakku.docker.compose.exception.UnsupportedSyntaxException;


public final class VolumeSyntaxParser {

    private VolumeSyntaxParser() {

    }

    public static Volume parse(final String source) {

        String[] parts = source.split(":");

        Volume.VolumeBuilder builder = Volume.builder();

        if (parts.length == 1)
            builder.container(parts[0]);

        else if (parts.length >= 2) {
            if (parts[0].startsWith(".")) {
                throw UnsupportedSyntaxException.getInstance("relative path to host is not allowed", "volume", source);
            }

            builder.host(parts[0]);
            builder.container(parts[1]);

            if (parts.length == 3)
                builder.mode(parts[2]);
        }

        return builder.build();

    }
}
