package cloud.sakku.docker.compose;


import cloud.sakku.docker.compose.exception.ComposeFileReaderException;
import cloud.sakku.docker.compose.model.ComposeFile;
import cloud.sakku.docker.compose.model.Sakku.SakkuApp;
import cloud.sakku.docker.compose.model.ServiceSpec;
import cloud.sakku.docker.compose.utils.ComposeToSakkuPipelineConverter;
import cloud.sakku.docker.compose.utils.EnvironmentFileReader;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComposeFileReader {

    private final String YAML_DOCKER_COMPOSE_FILE_NAME = "docker-compose.yaml";
    private final String YML_DOCKER_COMPOSE_FILE_NAME = "docker-compose.yml";

    private final Pattern YAML_DOCKER_COMPOSE_SUFFIX = Pattern.compile("^.*/(?<name>[0-9a-zA-Z_\\-]+).yaml[/]?$");
    private final Pattern YML_DOCKER_COMPOSE_SUFFIX = Pattern.compile("^.*/(?<name>[0-9a-zA-Z_\\-]+).yml[/]?$");

    private final String NOT_EXIST_ENVIRONMENT_ERROR = "'{ENV}' environment is not exist ! [service: {SERVICE}]";

    private final Pattern ENV_PATTERN = Pattern.compile("\\$\\{(?<env>[0-9a-zA-Z_]+)}");

    private ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
    private ObjectMapper jsonMapper = new ObjectMapper(new JsonFactory());

    private ComposeFile composeFile;
    private String path;

    public ComposeFile getComposeFile() {
        return composeFile;
    }

    // environments from '.env' file
    private Map<String, Object> defaultEnvironment = new HashMap<>();

    public static void main(String[] args) {
        /**
         * args[0] : docker compose directory path
         * args[1] : export directory path
         * args[2] : name of export file
         */
        if (args.length < 2) {
            System.err.println("No docker compose directory path or export directory path given");
            return;
        }

        String dockerComposePath = args[0];
        String exportPath = args[1];
        String exportFileName = "sakku_pipeline_config.json";

        if (args.length > 2)
            exportFileName = args[2];

        try {
            ComposeFileReader composeFileReader = new ComposeFileReader(dockerComposePath);

            if (Objects.nonNull(composeFileReader.getComposeFile())) {

                File exportDirectory = new File(exportPath);
                exportDirectory.mkdirs();

                if (exportPath.lastIndexOf("/") != exportPath.length() - 1) {
                    exportPath += "/";
                }

                File exportPipelineConfigFile = new File(exportPath + exportFileName);

                exportPipelineConfigFile.createNewFile();

                FileWriter file = new FileWriter(exportPipelineConfigFile);
                file.write(composeFileReader.toSakkuPipelineJson());
                file.close();


                System.out.println("\033[0;32m" + "Sakku Pipeline JSON config Successfully Create :)");
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * read compose file from File
     *
     * @param file yaml docker compose file
     * @throws ComposeFileReaderException
     * @throws FileNotFoundException
     */
    public ComposeFileReader(File file) throws ComposeFileReaderException, FileNotFoundException {

        String filePath = file.getAbsolutePath();

        if ((YAML_DOCKER_COMPOSE_SUFFIX.matcher(filePath).find() || YML_DOCKER_COMPOSE_SUFFIX.matcher(filePath).find())
                && file.isFile()) {

            String composeFileInString = readFile(file);

            composeFile = readComposeFile(composeFileInString);

        } else {

            System.err.println("docker-compose file not found");

        }

    }

    /**
     * read compose file from path of directory or path of docker compose yaml file
     *
     * @param path docker compose files directory path or path of docker-compose yaml file
     * @throws ComposeFileReaderException
     * @throws IOException
     */
    public ComposeFileReader(String path) throws ComposeFileReaderException, IOException {

        // add '/' to end of path
        if (path.lastIndexOf('/') != path.length() - 1) {
            path += '/';
        }

        this.path = path;

        String dockerComposeFilePath;

        if ((YAML_DOCKER_COMPOSE_SUFFIX.matcher(path).find() || YML_DOCKER_COMPOSE_SUFFIX.matcher(path).find())
                && new File(path).isFile()) {

            dockerComposeFilePath = path;

        } else if (new File(path + YAML_DOCKER_COMPOSE_FILE_NAME).isFile()) {

            dockerComposeFilePath = path + YAML_DOCKER_COMPOSE_FILE_NAME;

        } else if (new File(path + YML_DOCKER_COMPOSE_FILE_NAME).isFile()) {

            dockerComposeFilePath = path + YML_DOCKER_COMPOSE_FILE_NAME;

        } else {

            System.err.println("docker-compose file not found");

            return;
        }

        String composeFileInString = readFile(dockerComposeFilePath);


        if (new File(path + ".env").isFile()) {
            EnvironmentFileReader.put(path + ".env", defaultEnvironment);
        }

        composeFile = readComposeFile(composeFileInString);

    }

    /**
     * @param composeFileInString string of docker compose file
     * @return ComposeFile
     * @throws ComposeFileReaderException
     */
    private ComposeFile readComposeFile(String composeFileInString) throws ComposeFileReaderException {

        try {

            ComposeFile composeFile = convertToComposeFile(composeFileInString);

            setEnvironmentsValue(composeFile);

            return composeFile;

        } catch (JsonMappingException e) {

            System.err.println(e.getCause().getMessage());

            return null;

        } catch (Exception e) {

            System.err.println(e.getMessage());

            return null;
        }
    }

    private void setEnvironmentsValue(ComposeFile composeFile) {

        composeFile.getServices().forEach((serviceName, serviceSpec) -> {

            try {

                List<String> envFiles = serviceSpec.getEnvFile();


                if (Objects.nonNull(envFiles) && Objects.nonNull(this.path)) {

                    for (String envFile : envFiles) {

                        try {

                            if (envFile.indexOf("./") == 0) {

                                envFile = envFile.replace("./", this.path);

                            } else if (envFile.indexOf("/") != 0) {

                                envFile = this.path + envFile;

                            }

                            EnvironmentFileReader.put(envFile, serviceSpec.getEnvironment());

                        } catch (FileNotFoundException e) {

                            throw ComposeFileReaderException.getInstance("'" + this.path + envFile + "' environment file not found :|");

                        }
                    }

                }

                String serviceSpecJsonString = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(serviceSpec);

                Matcher matcher;

                while ((matcher = ENV_PATTERN.matcher(serviceSpecJsonString)).find()) {

                    String envKey = matcher.group("env");

                    String notExistEnvironmentError = NOT_EXIST_ENVIRONMENT_ERROR.replace("{ENV}", envKey).replace("{SERVICE}", serviceName);


                    Object defaultEnvValue = defaultEnvironment.getOrDefault(envKey, null);
                    Object envValue = serviceSpec.getEnvironment().getOrDefault(envKey, null);

                    if (Objects.isNull(envValue) || ENV_PATTERN.matcher(envValue.toString()).find()) {

                        if(Objects.isNull(defaultEnvValue))
                            throw ComposeFileReaderException.getInstance(notExistEnvironmentError);

                        envValue = defaultEnvValue;
                    }

                    String envRegex = "\\$\\{" + envKey + "}";

                    if ((Pattern.compile(envRegex).matcher(serviceSpecJsonString)).find()) {
                        serviceSpecJsonString = serviceSpecJsonString.replaceAll(envRegex, envValue.toString());
                    }

                }


                serviceSpec = new ObjectMapper().readValue(serviceSpecJsonString, ServiceSpec.class);

                composeFile.getServices().put(serviceName, serviceSpec);

            } catch (JsonProcessingException e) {

                throw ComposeFileReaderException.getInstance("set environment processing error :|");

            }
        });

    }

    private ComposeFile convertToComposeFile(String inputStream) throws IOException {
        return yamlReader.readValue(inputStream, ComposeFile.class);
    }

    private String readFile(final String composeFilePath) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(composeFilePath));

        StringBuilder composeFileInString = new StringBuilder();

        while (scanner.hasNextLine()) {
            composeFileInString.append(scanner.nextLine()).append("\n");
        }

        return composeFileInString.toString();
    }

    private String readFile(final File composeFile) throws FileNotFoundException {

        Scanner scanner = new Scanner(composeFile);

        StringBuilder composeFileInString = new StringBuilder();

        while (scanner.hasNextLine()) {
            composeFileInString.append(scanner.nextLine()).append("\n");
        }

        return composeFileInString.toString();
    }

    private static List<String> getErrorPath(List<JsonMappingException.Reference> path) {

        List<String> errorPath = new ArrayList<>();

        for (JsonMappingException.Reference reference : path) {

            if (Objects.isNull(reference.getFieldName())) {
                errorPath.add(reference.toString());
                continue;
            }

            errorPath.add(reference.getFieldName());
        }
        return errorPath;

    }

    /**
     * convert ComposeFile to Sakku pipeline config json
     *
     * @return Json String of Sakku pipeline config
     * @throws JsonProcessingException
     */
    public String toSakkuPipelineJson() throws JsonProcessingException {

        List<SakkuApp> pipeline = ComposeToSakkuPipelineConverter.convert(composeFile);

        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pipeline);

    }
}

