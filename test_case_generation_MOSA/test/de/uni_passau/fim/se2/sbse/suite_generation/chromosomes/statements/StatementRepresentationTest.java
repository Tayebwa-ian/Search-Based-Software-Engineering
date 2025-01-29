package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.StatementRepresenation.FieldAssignmentStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.StatementRepresenation.InitializationStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.StatementRepresenation.MethodCallStatement;

public class StatementRepresentationTest {

    // Helper class for testing field assignment
    private static class Example {
        public String exampleField;

        public Example() {
            this.exampleField = "Hello";
        }
    }

    @Test
    public void testInitializationStatementConstructor() throws NoSuchMethodException {
        String className = "Test";
        Constructor<String> constructor = String.class.getConstructor(String.class);
        Object[] parameters = new Object[]{"test"};
        Object cutInstance = new Object();
        
        InitializationStatement initStatement = new InitializationStatement(cutInstance, constructor, className, parameters);
        
        assertNotNull(initStatement);
        assertEquals(cutInstance, initStatement.cutInstance);
    }

    @Test
    public void testInitializationStatementRunWithSuccess() throws NoSuchMethodException {
        String className = "Test";
        Constructor<String> constructor = String.class.getConstructor(String.class);
        Object[] parameters = {"test"};
        Object cutInstance = new Object();
        
        InitializationStatement initStatement = new InitializationStatement(cutInstance, constructor, className, parameters);
        initStatement.run(); // This should not throw an exception as it's creating a String
    }

    @Test
    public void testInitializationStatementToString() throws NoSuchMethodException {
        String className = "Test";
        Constructor<String> constructor = String.class.getConstructor(String.class);
        Object[] parameters = new Object[]{"test"};
        Object cutInstance = new Object();
        
        InitializationStatement initStatement = new InitializationStatement(cutInstance, constructor, className, parameters);
        assertEquals("Test cut = new Test(test);", initStatement.toString());
    }

    @Test
    public void testMethodCallStatementWithStaticMethod() throws NoSuchMethodException {
        Method method = Integer.class.getMethod("parseInt", String.class);
        Object[] parameters = new Object[]{"10"};
        Object cutInstance = new Object(); // Not used for static method

        MethodCallStatement methodCall = new MethodCallStatement(cutInstance, method, parameters);
        methodCall.run(); // This should not throw an exception
    }

    @Test
    public void testMethodCallStatementWithInstanceMethod() throws NoSuchMethodException {
        Method method = String.class.getMethod("length");
        Object[] parameters = new Object[]{};
        Object cutInstance = "test";

        MethodCallStatement methodCall = new MethodCallStatement(cutInstance, method, parameters);
        methodCall.run(); // Should not throw an exception
    }

    @Test
    public void testMethodCallStatementToString() throws NoSuchMethodException {
        Method method = String.class.getMethod("length");
        Object[] parameters = new Object[]{};
        Object cutInstance = new Object();

        MethodCallStatement methodCall = new MethodCallStatement(cutInstance, method, parameters);
        assertEquals("cut.length();", methodCall.toString());
    }

    @Test
    public void testFieldAssignmentStatement() throws Exception {
        Field field = Example.class.getDeclaredField("exampleField");
        Object value = "testValue";
        Object cutInstance = new Example();

        FieldAssignmentStatement fieldAssignment = new FieldAssignmentStatement(cutInstance, field, value);
        fieldAssignment.run();

        assertEquals(value, field.get(cutInstance));
    }

    @Test
    public void testFieldAssignmentStatementToString() throws NoSuchFieldException {
        Field field = Example.class.getDeclaredField("exampleField");
        Object value = "testValue";
        Object cutInstance = new Object();

        FieldAssignmentStatement fieldAssignment = new FieldAssignmentStatement(cutInstance, field, value);
        assertEquals("cut.exampleField = testValue;", fieldAssignment.toString());
    }
}
