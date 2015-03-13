package org.hibernate.engine.spi;

import org.hibernate.LockMode;
import org.hibernate.persister.entity.EntityPersister;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by johara on 13/03/15.
 */
public interface EntityEntry {
	LockMode getLockMode();

	void setLockMode(LockMode lockMode);

	Status getStatus();

	void setStatus(Status status);

	Serializable getId();

	Object[] getLoadedState();

	Object[] getDeletedState();

	void setDeletedState(Object[] deletedState);

	boolean isExistsInDatabase();

	Object getVersion();

	EntityPersister getPersister();

	EntityKey getEntityKey();

	String getEntityName();

	boolean isBeingReplicated();

	Object getRowId();

	void postUpdate(Object entity, Object[] updatedState, Object nextVersion);

	void postDelete();

	void postInsert(Object[] insertedState);

	boolean isNullifiable(boolean earlyInsert, SessionImplementor session);

	Object getLoadedValue(String propertyName);

	boolean requiresDirtyCheck(Object entity);

	boolean isModifiableEntity();

	void forceLocked(Object entity, Object nextVersion);

	boolean isReadOnly();

	void setReadOnly(boolean readOnly, Object entity);

	@Override
	String toString();

	boolean isLoadedWithLazyPropertiesUnfetched();

	void serialize(ObjectOutputStream oos) throws IOException;

	//the following methods are handling extraState contracts.
	//they are not shared by a common superclass to avoid alignment padding
	//we are trading off duplication for padding efficiency
	void addExtraState(EntityEntryExtraState extraState);

	<T extends EntityEntryExtraState> T getExtraState(Class<T> extraStateType);
}
