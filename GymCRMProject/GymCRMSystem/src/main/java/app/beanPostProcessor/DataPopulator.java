package app.beanPostProcessor;

import app.builders.Builder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import app.dto.internal.GymDTO;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
public class DataPopulator implements BeanPostProcessor {


    private final Map<String, Resource> dataMap;

    private final Map<String, Builder> builderMap;

    private final ObjectProvider<ObjectMapper> objectMapperProvider;

    public DataPopulator(@Lazy Map<String, Resource> dataMap, @Lazy Map<String, Builder> builderMap,
                          ObjectProvider<ObjectMapper> objectMapperProvider) {
        this.dataMap = dataMap;
        this.builderMap = builderMap;
        this.objectMapperProvider = objectMapperProvider;
    }

    /**
     * Method checks for beans that exist as Storage and injects data present in .json files in the appropriate
     * Storage
     * @param bean the new bean instance
     * @param beanName the name of the bean
     * @return Bean object
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessBeforeInitialization(@NonNull Object bean, String beanName) {
        if(beanName.endsWith("Storage") && bean instanceof Map) {
            List<GymDTO> entities = null;
            try {
                entities = getAllEntities(beanName, fetchAppropriateResource(beanName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Map<Long, GymDTO> beanMap = (Map<Long, GymDTO>)bean;

            for(GymDTO entity : entities) {
                beanMap.put(entity.getEntityId(), entity);
            }

        }

        return bean;
    }

    /**
     * Given the storage bean, gives appropriate resource bean(for instance, TraineeStorage -> TraineeResource)
     * @param beanName Name of the storage bean
     * @return Resource instance
     */
    private Resource fetchAppropriateResource(String beanName) {
        String name = beanName.substring(0, beanName.indexOf("Storage"));
        String resourceName = name + "Resource";
        return dataMap.get(resourceName);
    }

    /**
     * Fetches the appropriate builder for the particular Storage bean(Trainee, Trainer or Training) and
     * for each entry, builds the appropriate entity instance
     * @param beanName Name of the bean
     * @param resource Appropriate Resource instance
     * @return List of GymEntities
     * @throws IOException Exception is thrown if the builder for the particular entity is not implemented
     */
    private List<GymDTO> getAllEntities(String beanName, Resource resource) throws IOException {
        String name = beanName.substring(0, beanName.indexOf("Storage"));
        ObjectMapper objectMapper = objectMapperProvider.getObject();

        Builder builder = builderMap.
                entrySet().
                stream().
                filter(e ->
                e.getKey().
                        startsWith(name)).map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);


        if(builder == null) {
            throw new IllegalArgumentException("appropriate builder could not be found!");
        }

        List<Map<String, Object>> entryList = objectMapper.readValue(resource.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {});

        return entryList.
                stream()
                .map(builder::build)
                .toList();
    }

}
