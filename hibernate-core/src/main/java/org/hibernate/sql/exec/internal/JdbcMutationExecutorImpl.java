/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.exec.internal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.resource.jdbc.spi.LogicalConnectionImplementor;
import org.hibernate.sql.exec.spi.ExecutionContext;
import org.hibernate.sql.exec.spi.JdbcMutation;
import org.hibernate.sql.exec.spi.JdbcMutationExecutor;
import org.hibernate.sql.exec.spi.JdbcParameterBinder;
import org.hibernate.sql.exec.spi.PreparedStatementCreator;

/**
 * @author Steve Ebersole
 */
public class JdbcMutationExecutorImpl implements JdbcMutationExecutor {
	public static final JdbcMutationExecutorImpl CALL_AFTER_INSTANCE = new JdbcMutationExecutorImpl( true );
	public static final JdbcMutationExecutorImpl NO_CALL_AFTER_INSTANCE = new JdbcMutationExecutorImpl( false );

	private final boolean callAfterStatement;

	public JdbcMutationExecutorImpl(boolean callAfterStatement) {
		this.callAfterStatement = callAfterStatement;
	}

	public int execute(
			JdbcMutation jdbcMutation,
			ExecutionContext executionContext,
			PreparedStatementCreator statementCreator) {
		final LogicalConnectionImplementor logicalConnection = executionContext.getSession().getJdbcCoordinator().getLogicalConnection();
		final Connection connection = logicalConnection.getPhysicalConnection();

		final JdbcServices jdbcServices = executionContext.getSession().getFactory().getServiceRegistry().getService( JdbcServices.class );

		final String sql = jdbcMutation.getSql();
		try {
			jdbcServices.getSqlStatementLogger().logStatement( sql );

			// prepare the query
			final PreparedStatement preparedStatement = statementCreator.create( connection, sql );
			logicalConnection.getResourceRegistry().register( preparedStatement, true );

			try {
				if ( executionContext.getQueryOptions().getTimeout() != null ) {
					preparedStatement.setQueryTimeout( executionContext.getQueryOptions().getTimeout() );
				}

				// bind parameters
				// 		todo : validate that all query parameters were bound?
				int paramBindingPosition = 1;
				for ( JdbcParameterBinder parameterBinder : jdbcMutation.getParameterBinders() ) {
					paramBindingPosition += parameterBinder.bindParameterValue(
							preparedStatement,
							paramBindingPosition,
							executionContext.getParameterBindingContext(),
							executionContext.getSession()
					);
				}

				return preparedStatement.executeUpdate();
			}
			finally {
				logicalConnection.getResourceRegistry().release( preparedStatement );
			}
		}
		catch (SQLException e) {
			throw jdbcServices.getSqlExceptionHelper().convert(
					e,
					"JDBC exception executing SQL [" + sql + "]"
			);
		}
		finally {
			if ( callAfterStatement ) {
				logicalConnection.afterStatement();
			}
		}
	}
}
