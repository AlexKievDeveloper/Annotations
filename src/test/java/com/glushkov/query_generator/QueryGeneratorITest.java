package com.glushkov.query_generator;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryGeneratorITest {

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
        String expectedQuery = "INSERT INTO person (id, person_name, salary) VALUES (10, 'Alex', 3000.1);";
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
        String expectedQuery = "UPDATE person SET id = 10, person_name = 'Alex', salary = 3000.1;";
        //when
        String actualQuery = queryGenerator.update(testEntity);
        //then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void getByIDTest() {
        //prepare
        QueryGenerator queryGenerator = new QueryGenerator();
        String expectedQuery = "SELECT id, person_name, salary FROM person WHERE id = 1;";
        //when
        String actualQuery = queryGenerator.getByID(TestEntity.class, 1);
        //then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void deleteTest() {
        //prepare
        QueryGenerator queryGenerator = new QueryGenerator();
        String expectedQuery = "DELETE FROM person WHERE id = 1;";
        //when
        String actualQuery = queryGenerator.delete(TestEntity.class, 1);
        //then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void getColumnsValuesTest() {
        //prepare
        TestEntity testEntity = new TestEntity(10, "Alex", 3000.1);
        String expectedQuery = "10, 'Alex', 3000.1";
        //when
        String actualQuery = QueryGenerator.getColumnsValues(testEntity);
        //then
        assertEquals(expectedQuery, actualQuery);
    }
}