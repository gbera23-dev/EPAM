package app.integration.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.DefaultDataTableEntryTransformer;

import java.lang.reflect.Type;
import java.util.Map;

public class DataTableConfigurer {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @DefaultDataTableEntryTransformer
    public Object transformEntry(Map<String, String> row, Type type) {
        return objectMapper.convertValue(row, objectMapper.constructType(type));
    }
}
