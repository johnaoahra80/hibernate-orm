/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
package org.hibernate.engine.jdbc.internal.proxy;

import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic support for building {@link ResultSet}-based proxy handlers
 *
 * @author Steve Ebersole
 */
public abstract class MysqlAbstractResultSetProxyHandler extends AbstractProxyHandler {

    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
            MysqlAbstractResultSetProxyHandler.class.getName());

	private ResultSet resultSet;

    private Map<Object, Integer> colMap = null;

	public MysqlAbstractResultSetProxyHandler(ResultSet resultSet) {
		super( resultSet.hashCode() );
		this.resultSet = resultSet;
	}

	protected abstract JdbcServices getJdbcServices();

	protected abstract JdbcResourceRegistry getResourceRegistry();

	protected abstract Statement getExposableStatement();

	protected final ResultSet getResultSet() {
		errorIfInvalid();
		return resultSet;
	}

	protected final ResultSet getResultSetWithoutChecks() {
		return resultSet;
	}

	@Override
	protected Object continueInvocation(Object proxy, Method method, Object[] args) throws Throwable {
		final String methodName = method.getName();
		LOG.tracev( "Handling invocation of ResultSet method [{0}]", methodName );

		// other methods allowed while invalid ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		if ( "close".equals( methodName ) ) {
			explicitClose( ( ResultSet ) proxy );
			return null;
		}
		if ( "invalidate".equals( methodName ) ) {
			invalidateHandle();
			return null;
		}

		errorIfInvalid();

		// handle the JDBC 4 Wrapper#isWrapperFor and Wrapper#unwrap calls
		//		these cause problems to the whole proxy scheme though as we need to return the raw objects
		if ( "isWrapperFor".equals( methodName ) && args.length == 1 ) {
			return method.invoke( getResultSetWithoutChecks(), args );
		}
		if ( "unwrap".equals( methodName ) && args.length == 1 ) {
			return method.invoke( getResultSetWithoutChecks(), args );
		}

		if ( "getWrappedObject".equals( methodName ) ) {
			return getResultSetWithoutChecks();
		}

		if ( "getStatement".equals( methodName ) ) {
			return getExposableStatement();
		}


        /*
        * Mysql Specific
        */

        if ( "next".equals( methodName ) ) {
            return resultSet.next();
        }

        if ( "getMetaData".equals( methodName ) ) {
            return resultSet.getMetaData();
        }

        if ( "getInt".equals( methodName ) ) {
            if("java.lang.Integer".equals(args[0].getClass().getName())){
                return resultSet.getInt((Integer) args[0]);
            }
            else if("java.lang.String".equals(args[0].getClass().getName())){
                return resultSet.getInt(getColIndex(args[0]));
            }
        }

        if ( "getString".equals( methodName ) ) {
            if("java.lang.String".equals(args[0].getClass().getName())){
                return resultSet.getString(getColIndex(args[0]));
            }
            else {
                throw new Exception("Need to check this case");

            }
//            return resultSet.getInt((Integer) args[0]);
        }

        if ( "wasNull".equals( methodName ) ) {
            return resultSet.wasNull();

        }

        /*
        * END OF - Mysql Specific
        */

        try {
            //Original: calling method with column name
			//return method.invoke( resultSet, args );

            //new lookup column postion from resultset proxy
            //assume only one args for our test

                return method.invoke( resultSet, args );
		}
		catch ( InvocationTargetException e ) {
			Throwable realException = e.getTargetException();
            if (SQLException.class.isInstance(realException)) throw getJdbcServices().getSqlExceptionHelper().convert((SQLException)realException,
                                                                                                                      realException.getMessage());
            throw realException;
		}
	}

    private int getColIndex(Object colName){
        if(colMap==null){
            colMap = new HashMap<Object, Integer>();
        }
        try {
            colMap.put(colName, resultSet.findColumn((String) colName));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colMap.get(colName).intValue();
    }

	private void explicitClose(ResultSet proxy) {
		if ( isValid() ) {
			getResourceRegistry().release( proxy );
		}
	}

	protected void invalidateHandle() {
		resultSet = null;
		invalidate();
	}
}
