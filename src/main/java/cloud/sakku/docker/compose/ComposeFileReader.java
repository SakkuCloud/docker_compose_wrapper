package cloud.sakku.docker.compose;


import cloud.sakku.docker.compose.model.ComposeFile;
import cloud.sakku.docker.compose.model.ErrorModel;
import cloud.sakku.docker.compose.model.Sakku.SakkuApp;
import cloud.sakku.docker.compose.utils.ComposeToSakkuPipelineConverter;
import cloud.sakku.docker.compose.utils.EnvironmentFileReader;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class ComposeFileReader {

    private static ComposeFileReader mComposeFileReader;

    private ObjectMapper yamlReader;
    private ObjectMapper jsonMapper;

    private ComposeFileReader() {
        yamlReader = new ObjectMapper(new YAMLFactory());
        jsonMapper = new ObjectMapper(new JsonFactory());
    }

    public static ComposeFileReader getInstance() {
        if (mComposeFileReader == null)
            mComposeFileReader = new ComposeFileReader();
        return mComposeFileReader;
    }

    public static void main(String[] args) throws IOException {

        String path = "/path/to/docker-compose/directory/";

        String yamlFilePath = path + "docker-compose";

        if (new File(yamlFilePath + ".yml").isFile()) {

            yamlFilePath += ".yml";

        } else if (new File(yamlFilePath + ".yaml").isFile()) {

            yamlFilePath += ".yaml";

        } else {

            throw new FileNotFoundException("docker-compose file not found");

        }

        String composeFileInString = new ComposeFileReader().readFile(path + "docker-compose.yml");

        Map<String, Object> env = new HashMap<>();

        if (new File(path + ".env").isFile())
            env = EnvironmentFileReader.read(path + ".env");

        composeFileInString = setEnvironmentToComposeFile(env, composeFileInString);

        try {

            ComposeFile composeFile = new ComposeFileReader().read(composeFileInString);
            String sakkuPipelineJson = new ComposeFileReader().toSakkuPipelineJson(composeFile);
            System.out.println(sakkuPipelineJson);

        } catch (ValueInstantiationException e){

            String message = e.getCause().getMessage();

            ErrorModel error = ErrorModel.builder()
                    .message(message)
                    .column(e.getLocation().getColumnNr())
                    .line(e.getLocation().getLineNr())
                    .path(getErrorPath(e.getPath())).build();

            System.out.print(error.toString());

        } catch (UnrecognizedPropertyException e) {


            String message = "Unrecognized Property";

            ErrorModel error = ErrorModel.builder()
                    .message(message)
                    .column(e.getLocation().getColumnNr())
                    .line(e.getLocation().getLineNr())
                    .path(getErrorPath(e.getPath())).build();

            System.out.print(error.toString());

        } catch (JsonMappingException e) {

            List<String> errorPath = getErrorPath(e.getPath());

            String message = "Mapping Exception";

            ErrorModel error = ErrorModel.builder()
                    .message(message)
                    .column(e.getLocation().getColumnNr())
                    .line(e.getLocation().getLineNr())
                    .path(getErrorPath(e.getPath())).build();

            System.out.print(error.toString());

        } catch (Exception e) {

            e.printStackTrace();

        }


    }

    public ComposeFile read(String inputStream) throws IOException {
        return yamlReader.readValue(inputStream, ComposeFile.class);
    }

    public String toSakkuPipelineJson(ComposeFile composeFile) throws JsonProcessingException {

        List<SakkuApp> pipeline = ComposeToSakkuPipelineConverter.convert(composeFile);

        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pipeline);

    }

    public String readFile(final String composeFilePath) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(composeFilePath));

        StringBuilder composeFileInString = new StringBuilder();

        while (scanner.hasNextLine()) {
            composeFileInString.append(scanner.nextLine()).append("\n");
        }

        return composeFileInString.toString();
    }

    private static String setEnvironmentToComposeFile(Map<String, Object> environment, String composeFileInString) {

        String result = composeFileInString;

        for (String key : environment.keySet()) {

            String envRegex = "\\$\\{" + key + "}";

            if ((Pattern.compile(envRegex).matcher(result)).find()) {
                result = result.replaceAll(envRegex, environment.get(key).toString());
            }
        }

        return result;
    }

    private static List<String> getErrorPath(List<JsonMappingException.Reference> path) {

        List<String> errorPath = new ArrayList<>();

        for (JsonMappingException.Reference reference : path) {

            if(Objects.isNull(reference.getFieldName())) {
                errorPath.add(reference.toString());
                continue;
            }

            errorPath.add(reference.getFieldName());
        }
        return errorPath;

    }
}

