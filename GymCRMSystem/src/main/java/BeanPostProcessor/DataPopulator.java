package BeanPostProcessor;

import entities.Trainee;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class DataPopulator implements BeanPostProcessor {

    @Value("${data.filePath}")
    private Resource dataResource;

    @Override
    public  Object postProcessBeforeInitialization(Object bean, String beanName) {
        if(bean instanceof Map) {
            try(BufferedReader br =  new BufferedReader(new InputStreamReader(dataResource.getInputStream()))) {;
                List<String> data=  br.lines().toList();
                for (String str : data) {
                    System.out.println(str);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return bean;
    }

}
