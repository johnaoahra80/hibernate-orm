/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.tool.instrument.annotations;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
import org.hibernate.test.annotations.Company;
import org.hibernate.test.annotations.Flight;
import org.hibernate.test.annotations.Sky;
import org.hibernate.testing.jta.TestingJtaBootstrap;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.type.StandardBasicTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * @author John O'Hara
 */
public class CustomerEntityTest extends BaseCoreFunctionalTestCase {
    private DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);

    @Test
    public void testLoad() throws Exception {
        EmbeddedAddress embeddedAddress = new EmbeddedAddress("Some Street", null, "London", null,
                "UK", "111222", "0207111222");
        //put an object in DB
        assertEquals("CustomerEmbeddedAnnotation", configuration().getClassMapping(CustomerEmbeddedAnnotation.class.getName()).getTable().getName());

        Session s = openSession();
        Transaction tx = s.beginTransaction();
        CustomerEmbeddedAnnotation customer = new CustomerEmbeddedAnnotation();
        customer.setId(Long.valueOf(1));
        customer.setName("Joe");
        customer.setAddress(embeddedAddress);
        s.save(customer);
        s.flush();
        tx.commit();
        s.close();

        //read it
        s = openSession();
        tx = s.beginTransaction();
        customer = (CustomerEmbeddedAnnotation) s.get(CustomerEmbeddedAnnotation.class, Long.valueOf(1));
        assertNotNull(customer);
        assertEquals(Long.valueOf(1), customer.getId());
        assertEquals("Joe", customer.getName());
        tx.commit();
        s.close();
    }


    @Override
    protected Class[] getAnnotatedClasses() {
        return new Class[]{
                CustomerEmbeddedAnnotation.class,
                EmbeddedAddress.class
        };
    }

    @Override
    public void configure(Configuration cfg) {
        cfg.setProperty( "hibernate.ejb.use_class_enhancer", "true" );
    }
    // tests are leaving data around, so drop/recreate schema for now.  this is wha the old tests did

    @Override
    protected boolean createSchema() {
        return false;
    }

    @Before
    public void runCreateSchema() {
        schemaExport().create(false, true);
    }

    private SchemaExport schemaExport() {
        return new SchemaExport(serviceRegistry(), configuration());
    }

    @After
    public void runDropSchema() {
        schemaExport().drop(false, true);
    }

}

