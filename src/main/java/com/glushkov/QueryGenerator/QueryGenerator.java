package com.glushkov.QueryGenerator;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class QueryGenerator {

    public String getAll(Class<?> clazz) {
        StringBuilder stringBuilder = new StringBuilder("SELECT ");

        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        String tableName = annotation.name().isEmpty() ? clazz.getName() : annotation.name();
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

        Table annotation = value.getClass().getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        String tableName = annotation.name().isEmpty() ? value.getClass().getName() : annotation.name();
        StringJoiner stringJoiner = new StringJoiner(", ");

        for (Field declaredField : value.getClass().getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);

            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ? declaredField.getName() : columnAnnotation.name();
                stringJoiner.add(columnName);
            }
        }

        stringBuilder.append(tableName);
        stringBuilder.append(" (");
        stringBuilder.append(stringJoiner);
        stringBuilder.append(") ");
        stringBuilder.append("VALUES (");
        stringBuilder.append(QueryGenerator.getColumnsValues(value));
        stringBuilder.append(");");

        return stringBuilder.toString();
    }

    public String update(Object value) {
        StringBuilder stringBuilder = new StringBuilder("UPDATE ");

        Table annotation = value.getClass().getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        String tableName = annotation.name().isEmpty() ? value.getClass().getName() : annotation.name();
        StringJoiner stringJoiner = new StringJoiner(", ");

        String values = QueryGenerator.getColumnsValues(value);
        String[] valuesArray = values.split(", ");

        int index = 0;
        for (Field declaredField : value.getClass().getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);

            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ? declaredField.getName() : columnAnnotation.name();
                StringBuilder colValueBuilder = new StringBuilder();
                colValueBuilder.append(columnName);
                colValueBuilder.append(" = ");
                colValueBuilder.append(valuesArray[index]);
                stringJoiner.add(colValueBuilder);
                index++;
            }
        }

        stringBuilder.append(tableName);
        stringBuilder.append(" SET ");
        stringBuilder.append(stringJoiner);
        stringBuilder.append(" WHERE condition;");

        return stringBuilder.toString();
    }

    public String getByID(Class<?> clazz, Object id) {
        StringBuilder stringBuilder = new StringBuilder("SELECT ");

        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        String tableName = annotation.name().isEmpty() ? clazz.getName() : annotation.name();
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
        stringBuilder.append(" WHERE id = ");
        stringBuilder.append(id);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String delete(Class<?> clazz, Object id) {
        StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");

        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        String tableName = annotation.name().isEmpty() ? clazz.getName() : annotation.name();

        stringBuilder.append(tableName);
        stringBuilder.append(" WHERE id = ");
        stringBuilder.append(id);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    static String getColumnsValues(Object value) {
        try {
            Class clazz = value.getClass();
            Field[] fields = clazz.getDeclaredFields();
            StringJoiner valuesJoiner = new StringJoiner(", ");

            for (Field field : fields) {
                Column annotation = field.getAnnotation(Column.class);
                if (annotation != null) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(value);
                    valuesJoiner.add(fieldValue.toString());
                    field.setAccessible(false);
                }
            }
            return valuesJoiner.toString();
        } catch (ReflectiveOperationException reflectiveOperationException) {
            throw new RuntimeException("Exception while getting values from object fields.", reflectiveOperationException);
        }
    }
}


