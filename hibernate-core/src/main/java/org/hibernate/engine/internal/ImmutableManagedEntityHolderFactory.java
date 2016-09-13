package org.hibernate.engine.internal;

import org.hibernate.engine.spi.ManagedEntity;
import stormpot.BlazePool;
import stormpot.Config;
import stormpot.Pool;
import stormpot.Timeout;

import java.util.concurrent.TimeUnit;

/**
 * Created by johara on 07/09/16.
 */
public class ImmutableManagedEntityHolderFactory {

	Pool<PoolableImmutableManagedEntityHolder> pool;
	PoolableImmutableManagedEntityAllocator allocator;
	Config<PoolableImmutableManagedEntityHolder> config;
	Timeout timeout;

	public ImmutableManagedEntityHolderFactory() {
		initialize();
	}

	private void initialize(){

		//todo add to hibernate settings
		String poolSizeProperty = System.getProperty( "org.hibernate.immutableManagedEntityHolderPoolSize" );
		Integer poolSize = 10;
		if( poolSizeProperty != null ){
			poolSize = Integer.parseInt( poolSizeProperty );
		}

		allocator = new PoolableImmutableManagedEntityAllocator();
		config = new Config<PoolableImmutableManagedEntityHolder>().setAllocator(allocator);
		config.setSize( poolSize );
		pool = new BlazePool<PoolableImmutableManagedEntityHolder>(config);
		timeout = new Timeout(1, TimeUnit.SECONDS);

	}

	public ImmutableManagedEntityHolder getManagedEntityHolder(ManagedEntity entity){
		try {
			return  pool.claim(timeout);
		} catch (InterruptedException e) {
			//todo throw appropriate Exception
			e.printStackTrace();
		}
		return null;
	}

	public void releaseImmutableEntityHolder(ImmutableManagedEntityHolder entity){
		if( entity != null && entity instanceof PoolableImmutableManagedEntityHolder) {
			((PoolableImmutableManagedEntityHolder) entity).release();
		}
	}
}
