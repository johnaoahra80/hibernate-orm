package org.hibernate.engine.internal;

import org.hibernate.engine.spi.ManagedEntity;
import stormpot.Allocator;
import stormpot.Poolable;
import stormpot.Slot;

/**
 * Created by johara on 07/09/16.
 */
public class PoolableImmutableManagedEntityAllocator implements Allocator<PoolableImmutableManagedEntityHolder> {

	@Override
	public PoolableImmutableManagedEntityHolder allocate(Slot slot) throws Exception {
		return new PoolableImmutableManagedEntityHolder( null , slot );
	}

	@Override
	public void deallocate(PoolableImmutableManagedEntityHolder poolable) throws Exception {
		poolable.release();
	}
}
