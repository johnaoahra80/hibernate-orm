/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.ast.consume.spi;

/**
 * Access to appending SQL fragments to an in-flight buffer
 *
 * @author Steve Ebersole
 */
public interface SqlAppender {
	/**
	 * Add the passed fragment into the in-flight buffer
	 */
	void appendSql(String fragment);
}