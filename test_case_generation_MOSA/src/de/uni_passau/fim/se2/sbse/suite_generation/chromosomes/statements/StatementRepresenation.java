package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Represents different types of statements in a test case, including initialization of the Class Under Test (CUT),
 * method calls, and field assignments.
 *
 * @author Tayebwa Ian
 */
public abstract class StatementRepresenation implements Statement {

    protected Object cutInstance;

    /**
     * Constructor for initializing the CUT with a non-private constructor.
     * @param cutInstance The instance of the Class Under Test.
     */
    public StatementRepresenation(Object cutInstance) {
        this.cutInstance = cutInstance;
    }

    @Override
    public abstract void run();

    @Override
    public abstract String toString();

    /**
     * A concrete implementation for initializing the CUT.
     */
    public static class InitializationStatement extends StatementRepresenation {

        private final String className;
        private final Object[] parameters;
        private final Constructor<?> constructor;

        public InitializationStatement(
            Object cutInstance,
            Constructor<?> constructor,
            String className,
            Object... parameters
        ) {
            super(cutInstance);
            this.className = className;
            this.parameters = parameters;
            this.constructor = constructor;
        }

        @Override
        public void run() {
            try {
                constructor.newInstance(parameters);
            } catch (Exception e) {
                System.err.println("Failed to Institiate a constructor");
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(className + " cut = new " + className).append("(");
            for (int i = 0; i < parameters.length; i++) {
                sb.append(parameters[i] != null ? parameters[i].toString() : "null");
                if (i < parameters.length - 1) sb.append(", ");
            }
            sb.append(");");
            return sb.toString();
        }
    }

    /**
     * Statement for calling a method on the CUT, either dynamic or static.
     */
    public static class MethodCallStatement extends StatementRepresenation {

        private final Method method;
        private final Object[] parameters;

        public MethodCallStatement(Object cutInstance, Method method, Object... parameters) {
            super(cutInstance);
            this.method = method;
            this.parameters = parameters;
        }

        @Override
        public void run() {
            try {
                if (Modifier.isStatic(method.getModifiers())) {
                    // Handle static method calls
                    method.invoke(null, parameters);
                } else {
                    // Handle instance method calls
                    method.invoke(cutInstance, parameters);
                }
            } catch (Exception e) {
                System.err.println("Failed to invoke method: " + method.getName());
                e.printStackTrace();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("cut.").append(method.getName()).append("(");
            for (int i = 0; i < parameters.length; i++) {
                sb.append(parameters[i] != null ? parameters[i].toString() : "null");
                if (i < parameters.length - 1) sb.append(", ");
            }
            sb.append(");");
            return sb.toString();
        }
    }

    /**
     * Statement for assigning values to instance fields of the CUT.
     */
    public static class FieldAssignmentStatement extends StatementRepresenation {

        private final Field field;
        private final Object value;

        public FieldAssignmentStatement(Object cutInstance, Field field, Object value) {
            super(cutInstance);
            this.field = field;
            this.value = value;
        }

        @Override
        public void run() {
            try {
                field.setAccessible(true); // Allow access to private fields
                field.set(cutInstance, value);
            } catch (Exception e) {
                System.err.println("Failed to assign field: " + field.getName());
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "cut." + field.getName() + " = " + (value != null ? value.toString() : "null") + ";";
        }
    }
}
