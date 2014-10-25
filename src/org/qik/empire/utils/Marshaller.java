package org.qik.empire.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.qik.empire.utils.Lambda.asLambda;
import static org.qik.empire.utils.ReflectionUtils.XField;
import static org.qik.empire.utils.ReflectionUtils.getXFields;
import static org.qik.empire.utils.Utils.toStr;

/**
 * Created by qik on 25.10.2014.
 */
public class Marshaller<T> {
    private final Class<T> sourceEntity;


    public Marshaller(Class<T> sourceEntity) {
        this.sourceEntity = sourceEntity;

        try {
            sourceEntity.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new VerificationException("The default constructor should be defined for entity %s", sourceEntity);
        }
    }

    public Marshaller<T> verify(Collection<String> dataProperties) {
        String[] entityProperties = ReflectionUtils.getFieldName(sourceEntity);

        List<String> invalidEntityProperties = new ArrayList<>();

        invalidEntityProperties.addAll(asList(entityProperties));
        invalidEntityProperties.removeAll(dataProperties);

        if (!invalidEntityProperties.isEmpty()) {
            throw new VerificationException("Can't find corespondent mapping storage for properties %s of %s, storage properties are: %s", invalidEntityProperties, sourceEntity, dataProperties);
        }

        return this;
    }

    public  <T, V> Function<T, V> f(Function<T, V> v){
        return v;
    }

    public Map<String, String> marshal(T entity) {
        return asLambda(  getXFields(entity))
                             .toMap(XField::getName,
                                     field -> toStr(field.getValue()));
    }

    public T unmarshal(Map<String, String> map) {
        T entity = ReflectionUtils.createInstance(sourceEntity, true);

        for(XField field: getXFields(entity)) {
            field.setFieldValue(map.get(field.getName()));
        }

        return entity;
    }
}
