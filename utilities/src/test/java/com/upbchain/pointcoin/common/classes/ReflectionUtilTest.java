package com.upbchain.pointcoin.common.classes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author kevin.wang.cy@gmail.com
 */
public class ReflectionUtilTest {
    @Before
    public void setUp() throws Exception {
        System.out.println("setUp....");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("teardDown....");
    }

    @Test
    public void reflectStaticFieldValueTest() throws Exception {
        assertEquals("Try get static private field's value", "Static Field Value", ReflectionUtil.reflectValue(TargetClass.class, "STATIC_FIELD"));
    }

    @Test
    public void reflectInstanceFieldValueTest() throws Exception {
        assertEquals("Try to get instance private field value", "Instance Field Value", ReflectionUtil.reflectValue(new TargetClass(), "instanceField"));
    }

    @Test
    public void reflectFieldTest() throws Exception {
        Field field = ReflectionUtil.reflectField(TargetClass.class, "instanceList");

       assertNotNull("instanceList is a valid field", field);

       assertEquals("instanceList", field.getName());
    }

    @Test
    public void refectSetValueTest() throws Exception {
        List<String> mockedList = mock(List.class);
        when(mockedList.get(0)).thenReturn("First ELement");
        when(mockedList.get(1)).thenReturn("Second Element");

        TargetClass target = new TargetClass();

        ReflectionUtil.refectSetValue(target, "instanceList", mockedList);

        assertEquals("Mocked Value of First Element should returned.", "First ELement", target.getInstanceList().get(0));
        assertEquals("Mocked Value of Second Element should returned.", "Second Element", target.getInstanceList().get(1));


        assertEquals("Mocked Value of First Element should returned.", "First ELement", target.getCopyInstanceList().get(0));
        assertEquals("Mocked Value of Second Element should returned.", "Second Element", target.getCopyInstanceList().get(1));

    }



    static class TargetClass {
        private static String STATIC_FIELD = "Static Field Value";
        private String instanceField = "Instance Field Value";

        private List<String> instanceList = new ArrayList<>();

        public List<String> getInstanceList() {
            return this.instanceList;
        }

        public List<String> getCopyInstanceList() {
            return Collections.unmodifiableList(this.instanceList);
        }
    }

}