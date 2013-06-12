package org.hibernate.test.tool.instrument;

import org.apache.tools.ant.Project;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.bytecode.buildtime.internal.JavassistInstrumenter;
import org.hibernate.bytecode.buildtime.spi.Logger;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.tool.instrument.javassist.InstrumentTask;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.HashSet;

import static org.junit.Assert.fail;

/**
 * @author John O'Hara (johara at redhat dot com)
 */
//@TestForIssue(jiraKey = "HHH-6780")
public class IntrumentEntityTest extends BaseCoreFunctionalTestCase {

    private static final LoggerBridge logger = new LoggerBridge();

    private static Address address;

    //need to dynmically deduce the file location
    private static String DIR_PATH = "/home/john/Work/community/hibernate/johnaoahra80/hibernate-orm/hibernate-core/target/classes/test/org/hibernate/test/tool/instrument/";

    private static String FIRST_NAME = "Joe";
    private static String SECOND_NAME = "Bloggs";


    @Override
    public String[] getMappings() {
        return new String[]{"tool/instrument/CustomerEmbedded.hbm.xml"};
    }

    private static LoggerBridge getLogger() {
        return logger;
    }


    @BeforeClass
    public static void setupIntrumenation() {


        //bytecode enhance entities
        HashSet<File> files = new HashSet<File>();

        files.add(new File(DIR_PATH + "CustomerEmbeddedEnhanced.class"));
        files.add(new File(DIR_PATH + "CustomerNonEmbeddedEnhanced.class"));

        InstrumentTask instrumentTask = new InstrumentTask();

        JavassistInstrumenter javassistInstrumenter = new JavassistInstrumenter(getLogger(), instrumentTask);

        javassistInstrumenter.execute(files);

        //create dummy address
        address = new Address("Some Street", null, "London", null,
                "UK", "111222", "0207111222");
    }

    @Test
    public void testNonEmbeddedCustomer() throws Exception {
        try {
            Session s = openSession();
            Transaction tx = s.beginTransaction();
            CustomerNonEmbedded CustomerNonEmbedded = new CustomerNonEmbedded(FIRST_NAME, SECOND_NAME, address);
            s.persist(CustomerNonEmbedded);
            tx.commit();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail();

        }
    }

    @Test
    public void testEmbeddedCustomer() throws Exception {
        try {
            Session s = openSession();
            Transaction tx = s.beginTransaction();
            CustomerEmbedded customerEmbedded = new CustomerEmbedded(FIRST_NAME, SECOND_NAME, address);
            s.persist(customerEmbedded);
            tx.commit();
            s.close();
            fail();
        } catch (Exception e) {
            e.printStackTrace();
            fail();

        }
    }


    @Test
    public void testNonEmbeddedEnhancedCustomer() throws Exception {
        try {
            Session s = openSession();
            Transaction tx = s.beginTransaction();
            CustomerNonEmbeddedEnhanced customerNonEmbeddedEnhanced = new CustomerNonEmbeddedEnhanced(FIRST_NAME, SECOND_NAME, address);
            s.persist(customerNonEmbeddedEnhanced);
            tx.commit();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail();

        }
    }

    @Test
    public void testEmbeddedEnhancedCustomer() throws Exception {
        try {
            Session s = openSession();
            Transaction tx = s.beginTransaction();
            CustomerEmbeddedEnhanced customerEmbeddedEnhanced = new CustomerEmbeddedEnhanced(FIRST_NAME, SECOND_NAME, address);
            s.persist(customerEmbeddedEnhanced);
            tx.commit();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail();

        }
    }


    protected static class LoggerBridge implements Logger {
        public void trace(String message) {
            log(message, Project.MSG_VERBOSE);
        }


        public void debug(String message) {
            log(message, Project.MSG_DEBUG);
        }

        public void info(String message) {
            log(message, Project.MSG_INFO);
        }

        public void warn(String message) {
            log(message, Project.MSG_WARN);
        }

        public void error(String message) {
            log(message, Project.MSG_ERR);
        }

        private void log(String message, int msgVerbose) {
            //do nothing
        }
    }
}
