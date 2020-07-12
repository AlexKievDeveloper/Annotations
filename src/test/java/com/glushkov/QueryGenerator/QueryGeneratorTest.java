package com.glushkov.QueryGenerator;

import org.junit.Test;

import static org.junit.Assert.*;

public class QueryGeneratorTest {

    @Test
    public void getAllTest() {
        QueryGenerator queryGenerator = new QueryGenerator();

        String expectedQuery = "SELECT id, person_name, salary FROM persons;";
        String actualQuery = queryGenerator.getAll(Person.class);

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void insertTest() {
        QueryGenerator queryGenerator = new QueryGenerator();
        Person person = new Person();
        person.setId(10);
        person.setName("Alex");
        person.setSalary(3000.1);

        String expectedQuery = "INSERT INTO persons (id, person_name, salary) VALUES (10, Alex, 3000.1);";
        String actualQuery = queryGenerator.insert(person);

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void updateTest() {
        QueryGenerator queryGenerator = new QueryGenerator();
        Person person = new Person();
        person.setId(10);
        person.setName("Alex");
        person.setSalary(3000.1);

        String expectedQuery = "UPDATE persons SET id = 10, person_name = Alex, salary = 3000.1 WHERE condition;";
        String actualQuery = queryGenerator.update(person);

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void getByIDTest() {
        QueryGenerator queryGenerator = new QueryGenerator();

        String expectedQuery = "SELECT id, person_name, salary FROM persons WHERE id = 1;";
        String actualQuery = queryGenerator.getByID(Person.class, 1);

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void deleteTest() {
        QueryGenerator queryGenerator = new QueryGenerator();

        String expectedQuery = "DELETE FROM persons WHERE id = 1;";
        String actualQuery = queryGenerator.delete(Person.class, 1);

        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void getColumnsValuesTest(){
        QueryGenerator queryGenerator = new QueryGenerator();
        Person person = new Person();
        person.setId(10);
        person.setName("Alex");
        person.setSalary(3000.1);

        String expectedQuery = "10, Alex, 3000.1";
        String actualQuery = QueryGenerator.getColumnsValues(person);

        assertEquals(expectedQuery, actualQuery);
    }
}