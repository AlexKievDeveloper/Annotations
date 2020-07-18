package com.glushkov.query_generator;

import com.glushkov.dao.DefaultDataSource;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.StringJoiner;

public class QueryGenerator {

    public String getAll(Class<?> clazz) {
        StringBuilder stringBuilder = new StringBuilder("SELECT ");

        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        String schemaName = annotation.schema();
        String tableName = annotation.name().isEmpty() ? clazz.getSimpleName().toLowerCase() : annotation.name();

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
        if (!schemaName.equals("")) {
            stringBuilder.append(schemaName + ".");
        }
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

        String schemaName = annotation.schema();
        String tableName = annotation.name().isEmpty() ? value.getClass().getSimpleName().toLowerCase() : annotation.name();

        StringJoiner stringJoiner = new StringJoiner(", ");

        for (Field declaredField : value.getClass().getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);

            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ? declaredField.getName() : columnAnnotation.name();
                stringJoiner.add(columnName);
            }
        }
        if (!schemaName.equals("")) {
            stringBuilder.append(schemaName + ".");
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

        String schemaName = annotation.schema();
        String tableName = annotation.name().isEmpty() ? value.getClass().getSimpleName().toLowerCase() : annotation.name();

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
        if (!schemaName.equals("")) {
            stringBuilder.append(schemaName + ".");
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

        String schemaName = annotation.schema();
        String tableName = annotation.name().isEmpty() ? clazz.getSimpleName().toLowerCase() : annotation.name();

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
        if (!schemaName.equals("")) {
            stringBuilder.append(schemaName + ".");
        }
        stringBuilder.append(tableName);
        stringBuilder.append(" WHERE ");
        stringBuilder.append(getPrimaryKeyName(schemaName, tableName));
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

        String schemaName = annotation.schema();
        String tableName = annotation.name().isEmpty() ? clazz.getSimpleName().toLowerCase() : annotation.name();

        if (!schemaName.equals("")) {
            stringBuilder.append(schemaName + ".");
        }
        stringBuilder.append(tableName);
        stringBuilder.append(" WHERE ");
        stringBuilder.append(getPrimaryKeyName(schemaName, tableName));
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

    static String getPrimaryKeyName(String schemaName, String tableName) {

        final String GET_PRIMARY_KEY = "SELECT C.COLUMN_NAME FROM information_schema.table_constraints AS pk INNER JOIN\n" +
                "information_schema.KEY_COLUMN_USAGE AS C ON C.TABLE_NAME = pk.TABLE_NAME AND C.CONSTRAINT_NAME = pk.CONSTRAINT_NAME\n" +
                "AND C.TABLE_SCHEMA = pk.TABLE_SCHEMA WHERE  pk.TABLE_NAME  = '" + tableName + "' " +
                "AND pk.TABLE_SCHEMA = '" + schemaName + "' AND pk.CONSTRAINT_TYPE = 'PRIMARY KEY';";

        DefaultDataSource dataSource = new DefaultDataSource();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_PRIMARY_KEY)) {
            resultSet.next();
            return resultSet.getString(1);
        } catch (SQLException sqlException) {
            throw new RuntimeException("Can`t get name of primary key from DB", sqlException);
        }
    }
}


