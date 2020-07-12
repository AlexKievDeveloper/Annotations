package com.glushkov.annotationsimplementation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationsMethods {

    static void injectAnnotation(Object value) {
        try {
            Class clazz = value.getClass();
            Method[] methods = clazz.getDeclaredMethods();

            for (Method method : methods) {
                Run annotation = method.getAnnotation(Run.class);
                if (annotation != null) {
                    if (method.toString().contains("()")) {
                        method.setAccessible(true);
                        method.invoke(value);
                        method.setAccessible(false);
                    }
                }
            }
        } catch (ReflectiveOperationException reflectiveOperationException) {
            reflectiveOperationException.printStackTrace();
        }
    }

    static void fillInTheFields(Object value) {
        try {
            Class clazz = value.getClass();

            for (Field field : clazz.getDeclaredFields()) {
                Inject annotation = field.getAnnotation(Inject.class);
                if (annotation != null) {
                    if (annotation.value().equals(Void.class)) {
                        Class fieldClazz = field.getClass();
                        field.setAccessible(true);
                        field.set(value, fieldClazz.getConstructor().newInstance());
                        field.setAccessible(false);
                        break;
                    }

                    Class annotationValueClass = annotation.value();
                    if (annotationValueClass.isPrimitive()) {
                        field.setAccessible(true);
                        if (field.getType().equals(byte.class) || field.getType().equals(short.class) ||
                                field.getType().equals(int.class) || field.getType().equals(long.class) ||
                                field.getType().equals(double.class) || field.getType().equals(float.class)) {

                            field.setByte(value, (byte) 0);
                        } else if (field.getType().equals(boolean.class)) {
                            field.setBoolean(value, false);
                        } else if (field.getType().equals(char.class)) {
                            field.setChar(value, ' ');
                        }
                        field.setAccessible(false);
                    } else {
                        field.setAccessible(true);
                        field.set(value, annotationValueClass.getConstructor().newInstance());
                        field.setAccessible(false);
                    }
                }
            }
        } catch (ReflectiveOperationException reflectiveOperationException) {
            reflectiveOperationException.printStackTrace();
        }
    }
}




