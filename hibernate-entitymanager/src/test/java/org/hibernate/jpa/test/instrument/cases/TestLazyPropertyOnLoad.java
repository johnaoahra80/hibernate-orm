/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.instrument.cases;

import org.hibernate.Hibernate;
import org.hibernate.jpa.AvailableSettings;
import org.hibernate.jpa.test.instrument.domain.Customer;
import org.hibernate.jpa.test.instrument.domain.CustomerInventory;
import org.hibernate.jpa.test.instrument.domain.CustomerInventoryPK;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class TestLazyPropertyOnLoad extends AbstractExecutable {


	@Override
	protected Map buildSettings() {
		Map<Object, Object> settings = new Properties();
		settings.putAll( super.buildSettings() );
		settings.put( "hibernate.enable_specj_proprietary_syntax", "true" );
		settings.put( "hibernate.enable_lazy_load_no_trans", "true" );

//		settings.put( AvailableSettings.ENHANCER_ENABLE_DIRTY_TRACKING, "true" );
//		settings.put( AvailableSettings.ENHANCER_ENABLE_LAZY_INITIALIZATION, "true" );
//		settings.put( AvailableSettings.ENHANCER_ENABLE_ASSOCIATION_MANAGEMENT, "true" );

		return settings;
	}

	@Override
	public void prepare() {
		super.prepare();
		EntityManager em = getOrCreateEntityManager();

		em.getTransaction().begin();

		Customer newCustomer = new Customer( "Joe", "Bloggs" );
		newCustomer.setId( 1 );
		newCustomer.addInventory( 10, new BigDecimal( 100 ) );

		em.persist( newCustomer );

		em.getTransaction().commit();

	}

	@Override
	public void execute() throws Exception {
		String query = "select c from Customer c";
		EntityManager em = getOrCreateEntityManager();

		em.getTransaction().begin();
		Customer customer = em.find( Customer.class, 1 );
//		Customer customer = (Customer) em.createQuery( query ).getSingleResult();
		assertFalse( Hibernate.isPropertyInitialized( customer, "customerInventories" ) );

		em.getTransaction().commit();
		em.close();
		assertFalse( Hibernate.isPropertyInitialized( customer, "customerInventories") );

		List<CustomerInventory> inventoryList = customer.getInventories();

		assertNotNull( inventoryList );

	}

	@Override
	public Class[] getAnnotatedClasses() {
		return new Class[]{
				Customer.class,
				CustomerInventory.class,
				CustomerInventoryPK.class
		};
	}

}
