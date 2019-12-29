package cloud.sakku.docker.compose.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorModel {

    private String message;

    private List<String> path;

    private int column = 0;

    private int line = 0;

    @Override
    public String toString(){
        StringBuilder error = new StringBuilder();

        if(Objects.nonNull(message) && !message.isEmpty()){
            error.append("message: ").append(message).append("\n");
        }

        if(Objects.nonNull(path)){
            error.append("path: ");

            for (int i = 0; i < path.size() ; i++){
                error.append(path.get(i));

                if(i< path.size() - 1)
                    error.append(".");
                else
                    error.append("\n");
            }
        }

        if(column > 0 && line > 0){
            error.append("location: [").append("line=").append(line).append(", column=").append(column).append("]");
        }

        return error.toString();
    }
}
