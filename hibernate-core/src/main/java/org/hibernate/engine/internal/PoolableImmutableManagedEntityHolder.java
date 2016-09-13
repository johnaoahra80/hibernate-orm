/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.engine.internal;

import org.hibernate.engine.spi.ManagedEntity;
import stormpot.Poolable;
import stormpot.Slot;

/**
 * Defines a wrapper for an ImmutableManagedEntity to retain linked list of session ManagedEntities in a particular
 * PersistenceContext
 *
 * @author Gail Badner
 * @author John O'Hara
 */
public class PoolableImmutableManagedEntityHolder extends ImmutableManagedEntityHolder implements Poolable {

	private final Slot slot;

	public PoolableImmutableManagedEntityHolder(ManagedEntity immutableManagedEntity, Slot slot) {
		super( immutableManagedEntity );
		this.slot = slot;
	}

	@Override
	public void release() {
		this.managedEntity = null;
		this.next = null;
		this.previous = null;
		this.slot.release( this );
	}
}

