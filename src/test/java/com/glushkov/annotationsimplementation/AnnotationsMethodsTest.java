package com.glushkov.annotationsimplementation;

import org.junit.Test;

import static org.junit.Assert.*;

public class AnnotationsMethodsTest {

    @Test
    public void injectAnnotation() {
        TestClass testClass = new TestClass();
        assertEquals(0, testClass.getCount());

        AnnotationsMethods.injectAnnotation(testClass);

        assertEquals(100, testClass.getCount());
    }

    @Test
    public void fillInTheFields() {
        TestClass testClass = new TestClass();

        assertEquals(10, testClass.getCost());
        assertNull(testClass.getArrayList());

        AnnotationsMethods.fillInTheFields(testClass);

        assertEquals(0, testClass.getCost());
        assertNotNull(testClass.getArrayList());
    }
}