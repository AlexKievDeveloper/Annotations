package com.glushkov.query_generator;

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

        String values = QueryGenerator.getColumnsValues(value);
        String[] valuesArray = values.split(", ");
        StringJoiner stringJoiner = new StringJoiner(", ");

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
        stringBuilder.append(";");

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
        String primaryKeyName = "";

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            PrimaryKey primaryKeyAnnotation = declaredField.getAnnotation(PrimaryKey.class);

            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ? declaredField.getName() : columnAnnotation.name();
                stringJoiner.add(columnName);
            }
            if (primaryKeyAnnotation != null) {
                primaryKeyName =  primaryKeyAnnotation.name().isEmpty() ? declaredField.getName() : primaryKeyAnnotation.name();
            }
        }

        stringBuilder.append(stringJoiner);
        stringBuilder.append(" FROM ");
        stringBuilder.append(tableName);
        stringBuilder.append(" WHERE ");
        stringBuilder.append(primaryKeyName);
        stringBuilder.append(" = ");
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

        String primaryKeyName = "";

        for (Field declaredField : clazz.getDeclaredFields()) {
            PrimaryKey primaryKeyAnnotation = declaredField.getAnnotation(PrimaryKey.class);

            if (primaryKeyAnnotation != null) {
                primaryKeyName =  primaryKeyAnnotation.name().isEmpty() ? declaredField.getName() : primaryKeyAnnotation.name();
            }
        }

        stringBuilder.append(tableName);
        stringBuilder.append(" WHERE ");
        stringBuilder.append(primaryKeyName);
        stringBuilder.append(" = ");
        stringBuilder.append(id);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    static String getColumnsValues(Object value) {
        try {
            StringJoiner valuesJoiner = new StringJoiner(", ");

            for (Field field : value.getClass().getDeclaredFields()) {
                Column annotation = field.getAnnotation(Column.class);
                if (annotation != null) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(value);

                    if (fieldValue == null) {
                        throw new RuntimeException("Field value is null:" + fieldValue + ". Field:" + field);
                    }
                    if (field.getType() == String.class) {
                        valuesJoiner.add("'" + fieldValue.toString() + "'");
                    } else {
                        valuesJoiner.add(fieldValue.toString());
                    }
                }
            }
            return valuesJoiner.toString();
        } catch (ReflectiveOperationException reflectiveOperationException) {
            throw new RuntimeException("Error while getting values from object fields.", reflectiveOperationException);
        }
    }
}


