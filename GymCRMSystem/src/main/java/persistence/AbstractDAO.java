package persistence;

import java.util.List;
import java.util.Map;

public abstract class AbstractDAO<T> {

    private Map<Long, T> storage;

    public void setStorage(Map<Long, T> storage) {
        this.storage = storage;
    }

    public T getEntity(long entityPK) {
        return storage.get(entityPK);
    }

    public void save(long PK, T entity) {
        storage.put(PK, entity);
    }

    public void delete(long PK) {storage.remove(PK); }

    public List<T> getAll() {
        return storage.values().stream().toList();
    }

}
