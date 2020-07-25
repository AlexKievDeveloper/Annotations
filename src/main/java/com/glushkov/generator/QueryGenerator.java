package com.glushkov.generator;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class QueryGenerator {

    public String getAll(Class<?> clazz) {
        StringBuilder stringBuilder = new StringBuilder("SELECT ");

        String tableName = getTableName(clazz);

        StringJoiner stringJoiner = new StringJoiner(", ");

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);

            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ? declaredField.getName() : columnAnnotation.name();
                stringJoiner.add(columnName);
            }
        }

        stringBuilder.append(stringJoiner);
        stringBuilder.append(" FROM ");
        stringBuilder.append(tableName);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String insert(Object value) {
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ");

        String tableName = getTableName(value.getClass());

        Map<String, Object> columnNameToValueMap = getFieldsValues(value);

        StringBuilder columnsBuilder = new StringBuilder();
        columnsBuilder.append(" (");
        StringBuilder valuesBuilder = new StringBuilder();
        valuesBuilder.append("VALUES (");

        for (Map.Entry<String, Object> columnNameWithValue : columnNameToValueMap.entrySet()) {

            String columnName = columnNameWithValue.getKey();
            columnsBuilder.append(columnName).append(", ");

            Object fieldValue = columnNameWithValue.getValue();
            valuesBuilder.append(fieldValue).append(", ");
        }
        String columns = columnsBuilder.toString().substring(0, columnsBuilder.length() - ", ".length());
        String values = valuesBuilder.substring(0, valuesBuilder.length() - ", ".length());

        stringBuilder.append(tableName);
        stringBuilder.append(columns).append(") ");
        stringBuilder.append(values).append(")");
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String update(Object value) {
        StringBuilder stringBuilder = new StringBuilder("UPDATE ");

        String tableName = getTableName(value.getClass());

        StringJoiner stringJoiner = new StringJoiner(", ");

        Map<String, Object> columnNameToValueMap = getFieldsValues(value);

        for (Map.Entry<String, Object> columnNameWithValue : columnNameToValueMap.entrySet()) {
            String columnName = columnNameWithValue.getKey();
            Object fieldValue = columnNameWithValue.getValue();
            stringJoiner.add(columnName + " = " + fieldValue);
        }

        stringBuilder.append(tableName);
        stringBuilder.append(" SET ");
        stringBuilder.append(stringJoiner);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String getByID(Class<?> clazz, Object id) {
        StringBuilder stringBuilder = new StringBuilder("SELECT ");

        String tableName = getTableName(clazz);

        StringJoiner stringJoiner = new StringJoiner(", ");
        String primaryKeyName = "";

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            Id idAnnotation = declaredField.getAnnotation(Id.class);

            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ? declaredField.getName() : columnAnnotation.name();
                stringJoiner.add(columnName);
            }
            if (idAnnotation != null) {
                primaryKeyName = columnAnnotation.name().isEmpty() ? declaredField.getName() : columnAnnotation.name();
            }
        }

        stringBuilder.append(stringJoiner);
        stringBuilder.append(" FROM ");
        stringBuilder.append(tableName);
        stringBuilder.append(" WHERE ");
        stringBuilder.append(primaryKeyName);
        stringBuilder.append(" = ");
        stringBuilder.append("'").append(id).append("'");
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String delete(Class<?> clazz, Object id) {

        StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");

        String tableName = getTableName(clazz);

        String primaryKeyName = "";

        for (Field declaredField : clazz.getDeclaredFields()) {

            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            Id idAnnotation = declaredField.getAnnotation(Id.class);

            if (idAnnotation != null) {
                primaryKeyName = columnAnnotation.name().isEmpty() ? declaredField.getName() : columnAnnotation.name();
            }
        }

        stringBuilder.append(tableName);
        stringBuilder.append(" WHERE ");
        stringBuilder.append(primaryKeyName);
        stringBuilder.append(" = ");
        stringBuilder.append("'").append(id).append("'");
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    static String getTableName(Class<?> clazz) {
        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        return annotation.name().isEmpty() ? clazz.getName() : annotation.name();
    }

    static Map<String, Object> getFieldsValues(Object value) {
        try {
            Map<String, Object> columnNameToValueMap = new HashMap<>();

            for (Field field : value.getClass().getDeclaredFields()) {

                StringBuilder valueBuilder = new StringBuilder();

                Column columnAnnotation = field.getAnnotation(Column.class);

                if (columnAnnotation != null) {
                    field.setAccessible(true);

                    String columnName = columnAnnotation.name().isEmpty() ? field.getName() : columnAnnotation.name();

                    Object fieldValue = field.get(value);

                    if (fieldValue == null) {
                        throw new RuntimeException("Field value is:" + fieldValue + ". Field:" + field);
                    }

                    if (fieldValue instanceof CharSequence) {
                        valueBuilder.append("'").append(fieldValue).append("'");
                    } else {
                        valueBuilder.append(fieldValue);
                    }
                    columnNameToValueMap.put(columnName, valueBuilder.toString());
                }
            }
            return columnNameToValueMap;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error while getting values from object fields.", e);
        }
    }
}


