/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hikaricp;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;

import org.hibernate.testing.common.connections.BaseTransactionIsolationConfigTest;

import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;

/**
 * @author Steve Ebersole
 */
public class HikariTransactionIsolationConfigTest extends BaseTransactionIsolationConfigTest {

	@Override
	protected void assertCorrectConnectionProviderClass(ConnectionProvider connectionProvider) {
		assertTyping( HikariCPConnectionProvider.class, connectionProvider );
	}

}
