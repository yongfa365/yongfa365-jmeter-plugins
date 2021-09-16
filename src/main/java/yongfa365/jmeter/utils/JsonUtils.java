package yongfa365.jmeter.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {

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


}
