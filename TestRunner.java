import java.lang.reflect.*;
import java.util.*;

/**
 * Performs all methods of a class containing only boolean methods
 * where true indicates a pass and false indicates a failure. Run
 * the tests from the terminal with TestRunner &lt;name of class&gt;
 * or by instantiating Testrunner with a parameter containing the 
 * full class name (with package names prepended) and call its
 * run-method.
 */
class TestRunner {
    public static void main(String[] args) {
        TestRunner runner = new TestRunner( args[0] );
        
        runner.run();
    }

    Logger log;
    Class<?> testClass;
    Object testClassInstance;
    Method[] tests;

    public TestRunner( String className ) {
        this.testClass = tryFindClass( className );
        this.log = new Logger();
        this.testClassInstance = tryCreateInstance(testClass);
        this.tests = testClass.getDeclaredMethods();
    }

    public Class tryFindClass( String className ) {
        try {
            return Class.forName( className );
        } catch ( ClassNotFoundException e ) {
            System.out.println("The class " + className + " was not found");
            System.exit(0);
        }

        return null;
    }

    public Object tryCreateInstance(Class someClass) {
        try {
            return someClass.newInstance();
        } catch (InstantiationException e) {
            System.out.println("InstantiationException thrown when trying to construct class");
            System.exit(0);
        } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException thrown when trying to construct class");
            System.exit(0);
        }

        return null;
    }

    public boolean run() {
        boolean[] successes = new boolean[ this.tests.length ];
        performTests( successes );
        boolean allSuccessful = allSuccessful( successes );

        if ( !allSuccessful ) {
            log.print();
        }

        return allSuccessful;
    }

    private void performTests( boolean[] successes ) {

        for (int i = 0; i < tests.length; i++) {
            Method test = tests[i];

            String testName = camelCaseToSpaces( test.getName() );

            log.store("Performing test \"" + testName + "\"");
            boolean pass = false;

            try {
                pass = (boolean) test.invoke( testClassInstance, new Object[]{} );
            } catch (IllegalAccessException e) {
                System.out.println("IllegalAccessException thrown");
            } catch (InvocationTargetException e) {
                System.out.println("InvocationTargetException thrown");
            }
            
            if ( !pass ) {
                log.store( "--- FAILURE: \"" + testName +"\"" );
            } else {
                log.store( "--- Success: \"" + testName +"\"" );
            }

            successes[i] = pass;
        }

    }

    private String camelCaseToSpaces(String original) {
        if (original.length() == 0) {
            return original;
        }

        String res = "";

        char[] chArr = original.toCharArray();

        res += ( chArr[0] + "" ).toUpperCase();

        for (int i = 1; i < original.length(); i++) {
            if ( Character.isUpperCase( chArr[i] )) {
                res += " " + ( chArr[i] + "" ).toLowerCase();
            } else if ( chArr[i] == '_' ) {
                res += " ";
            } else {
                res += chArr[i];
            }
        }

        return res;
    }

    boolean allSuccessful(boolean[] successes) {
        for (boolean b : successes)
            if ( !b ) return false;
        return true;
    }
}

class Logger {
    private List<String> log = new LinkedList<>();
    
    public Logger store(Object... args) {
        for (Object o : args) {
            log.add(o.toString());
        }

        return this;
    }

    public Logger print() {
        for (String s : log) {
            System.out.println(s);
        }

        return this;
    }
}