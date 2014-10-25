package org.qik.empire.dao;

import org.qik.empire.core.Inject;
import org.qik.empire.storage.Storage;
import org.qik.empire.utils.Marshaller;
import org.qik.empire.utils.ReflectionUtils;

import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.qik.empire.storage.Storage.Schema;

/**
 * Created by qik on 23.10.2014.
 */
public abstract class EntityDao<T> {

    private final Class<T> entityClass;
    private final String groupName;
    private Storage.Group group;
    private Marshaller<T> marshaller;

    private Storage storage;

    public EntityDao(Class<T> entityClass, String groupName) {
        this.entityClass = entityClass;
        this.groupName   = groupName;
    }

    @Inject
    public void setStorage(Storage storage) {
        this.storage = storage;

        if(storage != null) {
            Schema schema = groupSchema();

            group      = storage.defineGroup(groupName, schema);
            marshaller = newMarshaller().verify(schema.getColumnNames());
        }
    }

    protected abstract Schema groupSchema();
    protected abstract String entityID(T entity);

    protected Map<String, String> marshal(T entity) {
        requireNonNull(storage, format("Storage to %s haven't been set", this.getClass()));

        return marshaller.marshal(entity);
    }

    protected T unmarshal(Map<String, String> raw) {
        return raw != null ? marshaller.unmarshal(raw) : null;
    }



    protected Marshaller<T> newMarshaller(){
        return new Marshaller<>(entityClass);
    }

    protected final Schema schemaByClass(){
        return new Schema(ReflectionUtils.getFieldName(entityClass));
    }


    public Storage.Group group() {
        requireNonNull(storage, format("Property 'setStorage' haven't been set to %s ", this.getClass()));

        return group;
    }

    public T get(String id){
        return unmarshal(storage.get(group(), id));
    }

    public void insert(T entity){
        storage.insert(group(), entityID(entity), marshal(entity));
    }

    public void update(T entity){
        storage.update(group(), entityID(entity), marshal(entity));
    }

    public void delete(T entity){
        storage.delete(group(), entityID(entity));
    }


}
