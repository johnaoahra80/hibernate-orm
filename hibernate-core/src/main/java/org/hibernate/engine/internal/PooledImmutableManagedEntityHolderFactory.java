package org.hibernate.engine.internal;

import org.hibernate.engine.spi.ManagedEntity;

/**
 * Created by johara on 07/09/16.
 */
public class PooledImmutableManagedEntityHolderFactory {

	//todo implement pooled EntityHolders
	public static ManagedEntity getManagedEntityHolder(ManagedEntity entity) {
		return new ImmutableManagedEntityHolder( (ManagedEntity) entity );
	}
}
