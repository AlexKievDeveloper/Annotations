package com.glushkov.generator;


import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class QueryGeneratorTest {

    @Test
    public void getAllTest() {
        //prepare
        QueryGenerator queryGenerator = new QueryGenerator();
        String expectedQuery = "SELECT id, person_name, salary FROM person;";
        //when
        String actualQuery = queryGenerator.getAll(TestEntity.class);
        //then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void insertTest() {
        //prepare
        QueryGenerator queryGenerator = new QueryGenerator();
        TestEntity testEntity = new TestEntity(10, "Alex", 3000.1);
        String expectedQuery = "INSERT INTO person (person_name, id, salary) VALUES ('Alex', 10, 3000.1);";
        //when
        String actualQuery = queryGenerator.insert(testEntity);
        //then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void updateTest() {
        //prepare
        QueryGenerator queryGenerator = new QueryGenerator();
        TestEntity testEntity = new TestEntity(10, "Alex", 3000.1);
        String expectedQuery = "UPDATE person SET person_name = 'Alex', id = 10, salary = 3000.1;";
        //when
        String actualQuery = queryGenerator.update(testEntity);
        //then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void getByIDTest() {
        //prepare
        QueryGenerator queryGenerator = new QueryGenerator();
        String expectedQuery = "SELECT id, person_name, salary FROM person WHERE id = '1';";
        //when
        String actualQuery = queryGenerator.getByID(TestEntity.class, 1);
        //then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void deleteTest() {
        //prepare
        QueryGenerator queryGenerator = new QueryGenerator();
        String expectedQuery = "DELETE FROM person WHERE id = '1';";
        //when
        String actualQuery = queryGenerator.delete(TestEntity.class, 1);
        //then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void getFieldsValuesTest() {
        //prepare
        TestEntity testEntity = new TestEntity(10, "Alex", 3000.1);
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("person_name", "'Alex'");
        expectedMap.put("id", 10);
        expectedMap.put("salary", 3000.1);
        //when
        Map<String, Object> actualMap = QueryGenerator.getFieldsValues(testEntity);
        //then
        assertEquals(expectedMap.toString(), actualMap.toString());
    }

    @Test
    public void getTableNameTest() {
        //prepare
        String expected = "person";
        //when
        String actual = QueryGenerator.getTableName(TestEntity.class);
        //then
        assertEquals(expected, actual);
    }
}