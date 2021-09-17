package yongfa365.jmeter.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MyUtils {

    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        ObjectMapper json = new ObjectMapper() {{
            setSerializationInclusion(JsonInclude.Include.NON_NULL);
            configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);
        }};

        try {
            return json.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeToFile(Path path, String content) {
        try {
            Files.write(path, content.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
