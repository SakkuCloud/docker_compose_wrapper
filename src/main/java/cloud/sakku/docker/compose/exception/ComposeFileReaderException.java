package cloud.sakku.docker.compose.exception;

import java.util.List;
import java.util.Objects;

public class ComposeFileReaderException extends RuntimeException {

    private List<String> path;

    private int column;
    private int line;


    public ComposeFileReaderException(String message) {
        super(message);
    }

    public ComposeFileReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComposeFileReaderException(String message, List<String> path, int line, int column) {

        super(message);

        this.path = path;
        this.line = line;
        this.column = column;
    }

    public ComposeFileReaderException(String message, Throwable cause, List<String> path, int line, int column) {

        super(message, cause);

        this.path = path;
        this.line = line;
        this.column = column;
    }

    public static ComposeFileReaderException getInstance(String message) {
        return new ComposeFileReaderException(message);
    }

    public static ComposeFileReaderException getInstance(String message, Throwable cause) {
        return new ComposeFileReaderException(message, cause);
    }

    public static ComposeFileReaderException getInstance(String message, Throwable cause, List<String> path, int line, int column) {
        return new ComposeFileReaderException(message, cause, path, line, column);
    }

    public static ComposeFileReaderException getInstance(String message, List<String> path, int line, int column) {
        return new ComposeFileReaderException(message, path, line, column);
    }

    @Override
    public String getMessage() {
        StringBuilder error = new StringBuilder();

        if (Objects.nonNull(super.getMessage()) && !super.getMessage().isEmpty()) {
            error.append(super.getMessage());
        }

        if (Objects.nonNull(path)) {
            error.append(" [path: ");

            for (int i = 0; i < path.size(); i++) {
                error.append(path.get(i));

                if (i < path.size() - 1)
                    error.append(".");
                else
                    error.append("]");
            }
        }

        if (column > 0 && line > 0) {
            error.append(" [location: ").append("line=").append(line).append(", column=").append(column).append("]");
        }

        return error.toString();
    }
}
