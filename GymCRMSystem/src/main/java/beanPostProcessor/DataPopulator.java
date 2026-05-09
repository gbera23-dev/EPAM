package beanPostProcessor;

import builders.Builder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.GymEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


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

    @Override
    public  Object postProcessBeforeInitialization(@NonNull Object bean, String beanName) {
        if(beanName.endsWith("Storage") && bean instanceof Map) {
            List<GymEntity> entities = null;
            try {
                entities = getAllEntities(beanName, fetchAppropriateResource(beanName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Map<Long, GymEntity> beanMap = (Map<Long, GymEntity>)bean;

            for(GymEntity entity : entities) {
                beanMap.put(entity.getEntityId(), entity);
            }
        }

        return bean;
    }


    private String readData(Resource resource) {
        try(BufferedReader br =  new BufferedReader(new InputStreamReader(resource.getInputStream()))) {;
            return br.lines().collect(Collectors.joining());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Resource fetchAppropriateResource(String beanName) {
        String name = beanName.substring(0, beanName.indexOf("Storage"));
        String resourceName = name + "Resource";
        return dataMap.get(resourceName);
    }

    private List<GymEntity> getAllEntities(String beanName, Resource resource) throws IOException {
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
            throw new IllegalArgumentException("appropriate builder could not be found");
        }

        List<Map<String, Object>> entryList = objectMapper.readValue(resource.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {});

        return entryList.
                stream()
                .map(builder::build)
                .toList();
    }

}
