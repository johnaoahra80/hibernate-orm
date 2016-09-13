/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.engine.internal;

import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.ManagedEntity;

/**
 * Defines a wrapper for an ImmutableManagedEntity to retain linked list of session ManagedEntities in a particular
 * PersistenceContext
 *
 * @author Gail Badner
 * @author John O'Hara
 */
public class ImmutableManagedEntityHolder implements ManagedEntity{
	protected ManagedEntity managedEntity;
	protected ManagedEntity previous;
	protected ManagedEntity next;

	public ImmutableManagedEntityHolder(ManagedEntity immutableManagedEntity) {
		this.managedEntity = immutableManagedEntity;
	}

	@Override
	public Object $$_hibernate_getEntityInstance() {
		return managedEntity.$$_hibernate_getEntityInstance();
	}

	@Override
	public EntityEntry $$_hibernate_getEntityEntry() {
		return managedEntity.$$_hibernate_getEntityEntry();
	}

	@Override
	public void $$_hibernate_setEntityEntry(EntityEntry entityEntry) {
		// need to think about implications for memory leaks here if we don't removed reference to EntityEntry
		if ( entityEntry == null ) {
			if ( canClearEntityEntryReference() ) {
				managedEntity.$$_hibernate_setEntityEntry( null );
			}
			// otherwise, do nothing.
		}
		else {
			// TODO: we may want to do something different here if
			// managedEntity is in the process of being deleted.
			// An immutable ManagedEntity can be associated with
			// multiple PersistenceContexts. Changing the status
			// to DELETED probably should not happen directly
			// in the ManagedEntity because that would affect all
			// PersistenceContexts to which the ManagedEntity is
			// associated.
			managedEntity.$$_hibernate_setEntityEntry( entityEntry );
		}
	}

	@Override
	public ManagedEntity $$_hibernate_getPreviousManagedEntity() {
		// previous reference cannot be stored in an immutable ManagedEntity;
		// previous reference is maintained by this ManagedEntityHolder.
		return previous;
	}

	@Override
	public void $$_hibernate_setPreviousManagedEntity(ManagedEntity previous) {
		// previous reference cannot be stored in an immutable ManagedEntity;
		// previous reference is maintained by this ManagedEntityHolder.
		this.previous = previous;
	}

	@Override
	public ManagedEntity $$_hibernate_getNextManagedEntity() {
		// next reference cannot be stored in an immutable ManagedEntity;
		// next reference is maintained by this ManagedEntityHolder.
		return next;
	}

	@Override
	public void $$_hibernate_setNextManagedEntity(ManagedEntity next) {
		// next reference cannot be stored in an immutable ManagedEntity;
		// next reference is maintained by this ManagedEntityHolder.
		this.next = next;
	}

	public ManagedEntity getManagedEntity(){
		return this.managedEntity;
	}

	/*
	Check instance type of EntityEntry and if type is ImmutableEntityEntry, check to see if entity is referenced cached in the second level cache
	 */
	private boolean canClearEntityEntryReference(){

		if( managedEntity.$$_hibernate_getEntityEntry() == null ) {
			return true;
		}

		if( !(managedEntity.$$_hibernate_getEntityEntry() instanceof ImmutableEntityEntry) ) {
			return true;
		}
		else if( managedEntity.$$_hibernate_getEntityEntry().getPersister().canUseReferenceCacheEntries() ) {
			return false;
		}

		return true;

	}
}