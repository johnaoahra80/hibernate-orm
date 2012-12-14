package org.hibernate.test.positionalExtract;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/29/12
 * Time: 10:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestPositionalExtractMysql  {
    Configuration configuration;
    SessionFactory sessionFactory;
    ServiceRegistry serviceRegistry;

    public String[] getMappings() {
        return new String[] { "positionalExtract/car.hbm.xml" };
    }

    @Before
    public void buildSessionFactory() {
        configuration = new Configuration();
        configuration.configure("org/hibernate/test/positionalExtract/hibernate.cfg.xml");
        serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    @Test
    @SuppressWarnings( {"unchecked"})
    public void testPersistCar() {
        int id;

        Session session = sessionFactory.openSession();

        Car c = new Car("Ferrari", "F355", 3496);

        Transaction tx = session.beginTransaction();
        session.persist(c);
        session.flush();

        id = c.getId();

        session.clear();
        //s = (Search) sess.createCriteria(Search.class).uniqueResult();

        Assert.assertEquals(1,id);

        c = (Car) session.load(c.getClass(),1);

        Assert.assertEquals("Ferrari", c.getMake());
        Assert.assertEquals("F355", c.getModel());
        Assert.assertEquals(3496, c.getEngineCapacity());

    }

}
