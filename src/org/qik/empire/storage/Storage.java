package org.qik.empire.storage;

import org.qik.empire.utils.VerificationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.qik.empire.utils.Lambda.asLambda;

/**
 * Created by qik on 19.10.2014.
 */
public class Storage {

    private Map<String, Group> groups = new HashMap<>();

    public Group defineGroup(String groupName, Schema schema) {
        if(groups.containsKey(groupName)) {
            throw new VerificationException("Group %s already exists", groupName);
        }

        Group group = new Group(groupName, schema);
        groups.put(groupName, group);

        return group;
    }

    public Group openGroup(String groupName) {
        return groups.get(groupName);
    }


    public Map<String, String> get(Group group, String id){
        Map<String, String> record = group.idIndex.get(id);

        if(record == null) return null;

        return new HashMap<>(record);
    }

    public List<Map<String, String>> list(Group group){
        return asLambda(   group.records)
                            .map(record -> new HashMap<>(record))
                          .copyTo(new ArrayList<>());
    }

    public void insert(Group group, String id, Map<String, String> record) {
        if(group.idIndex.containsKey(id)) {
            throw new VerificationException("Record %s already exists in group %s", id, group.name);
        }

        validateRecordKeys(group, record);

        record = new HashMap<>(record);

        group.records.add(record);
        group.idIndex.put(id, record);
    }

    public void update(Group group, String id, Map<String, String> record) {
        if(!group.idIndex.containsKey(id)) {
            throw new VerificationException("Can't find record %s in group %s", id, group.name);
        }

        validateRecordKeys(group, record);

        group.idIndex.get(id).putAll(record);

    }

    public void delete(Group group, String id) {
        if(!group.idIndex.containsKey(id)) {
            throw new VerificationException("Can't find record %s in group %s", id, group.name);
        }

        Map<String, String> record = group.idIndex.get(id);
        group.records.remove(record);
        group.idIndex.remove(id);
    }



    private void validateRecordKeys(Group group, Map<String, String> record) {
        List<String> invalidColumns = new ArrayList<>();

        invalidColumns.addAll(record.keySet());
        invalidColumns.removeAll(group.schema.columnNames);

        if(!invalidColumns.isEmpty()) {
            throw new VerificationException("Column sets %s isn't defined in group %s schema", invalidColumns, group.name);
        }
    }


    public static class Schema{
        private final List<String> columnNames;

        public Schema(String... columnNames) {
            this.columnNames = unmodifiableList(asList(columnNames));
        }

        public List<String> getColumnNames(){
            return columnNames;
        }
    }

    public static class Group{
        private final String name;
        private final Schema schema;

        private final List<Map<String, String>> records = new ArrayList<>();
        private final Map<String, Map<String, String>> idIndex = new HashMap<>();

        private Group(String name, Schema schema) {
            this.name   = name;
            this.schema = schema;
        }
    }
}
