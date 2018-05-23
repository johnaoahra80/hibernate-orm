/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.metamodel.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.persistence.EntityGraph;

import org.hibernate.EntityNameResolver;
import org.hibernate.graph.spi.EntityGraphImplementor;
import org.hibernate.metamodel.NotNavigableException;
import org.hibernate.metamodel.RuntimeModel;
import org.hibernate.metamodel.model.creation.spi.InFlightRuntimeModel;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.metamodel.model.domain.spi.EmbeddedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.EntityDescriptor;
import org.hibernate.metamodel.model.domain.spi.EntityHierarchy;
import org.hibernate.metamodel.model.domain.spi.MappedSuperclassDescriptor;
import org.hibernate.metamodel.model.domain.spi.PersistentCollectionDescriptor;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractRuntimeModel implements RuntimeModel {
	private final Set<EntityHierarchy> entityHierarchySet;
	private final Map<String,EntityDescriptor<?>> entityDescriptorMap;
	private final Map<String,MappedSuperclassDescriptor<?>> mappedSuperclassDescriptorMap;
	private final Map<String,EmbeddedTypeDescriptor<?>> embeddedDescriptorMap;
	private final Map<String,PersistentCollectionDescriptor<?,?,?>> collectionDescriptorMap;

	private final Map<String,String> nameImportMap;
	private final Set<EntityNameResolver> entityNameResolvers;

	private final Map<String,EntityGraphImplementor<?>> entityGraphMap;

	public AbstractRuntimeModel() {
		this.entityDescriptorMap = new ConcurrentHashMap<>();
		this.entityHierarchySet = ConcurrentHashMap.newKeySet();
		this.mappedSuperclassDescriptorMap = new ConcurrentHashMap<>();
		this.embeddedDescriptorMap = new ConcurrentHashMap<>();
		this.collectionDescriptorMap = new ConcurrentHashMap<>();
		this.nameImportMap = new ConcurrentHashMap<>();
		this.entityNameResolvers = ConcurrentHashMap.newKeySet();
		this.entityGraphMap = new ConcurrentHashMap<>();
	}

	public AbstractRuntimeModel(InFlightRuntimeModel inFlightModel) {
		this(
				inFlightModel.getEntityHierarchySet(),
				inFlightModel.getEntityDescriptorMap(),
				inFlightModel.getMappedSuperclassDescriptorMap(),
				inFlightModel.getEmbeddedDescriptorMap(),
				inFlightModel.getCollectionDescriptorMap(),
				inFlightModel.getEntityNameResolvers(),
				inFlightModel.getNameImportMap(),
				inFlightModel.getEntityGraphMap()
		);
	}

	private AbstractRuntimeModel(
			Set<EntityHierarchy> entityHierarchySet,
			Map<String, EntityDescriptor<?>> entityDescriptorMap,
			Map<String, MappedSuperclassDescriptor<?>> mappedSuperclassDescriptorMap,
			Map<String, EmbeddedTypeDescriptor<?>> embeddedDescriptorMap,
			Map<String, PersistentCollectionDescriptor<?, ?, ?>> collectionDescriptorMap,
			Set<EntityNameResolver> entityNameResolvers,
			Map<String, String> nameImportMap,
			Map<String, EntityGraphImplementor<?>> entityGraphMap) {
		this.entityHierarchySet = Collections.unmodifiableSet( entityHierarchySet );
		this.entityDescriptorMap = Collections.unmodifiableMap( entityDescriptorMap );
		this.mappedSuperclassDescriptorMap = Collections.unmodifiableMap( mappedSuperclassDescriptorMap );
		this.embeddedDescriptorMap = Collections.unmodifiableMap( embeddedDescriptorMap );
		this.collectionDescriptorMap = Collections.unmodifiableMap( collectionDescriptorMap );
		this.nameImportMap = Collections.unmodifiableMap( nameImportMap );
		this.entityNameResolvers = Collections.unmodifiableSet( entityNameResolvers );

		// NOTE : EntityGraph map is mutable during runtime
		this.entityGraphMap = new ConcurrentHashMap<>( entityGraphMap );
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// EntityHierarchy

	protected Set<EntityHierarchy> getEntityHierarchySet() {
		return entityHierarchySet;
	}

	@Override
	public void visitEntityHierarchies(Consumer<EntityHierarchy> action) {
		entityHierarchySet.forEach( action );
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// EntityDescriptor

	protected Map<String, EntityDescriptor<?>> getEntityDescriptorMap() {
		return entityDescriptorMap;
	}

	@Override
	public <T> EntityDescriptor<T> getEntityDescriptor(Class<T> javaType) {
		return getEntityDescriptor( javaType.getName() );
	}

	@Override
	public <T> EntityDescriptor<T> getEntityDescriptor(NavigableRole name) {
		return getEntityDescriptor( name.getFullPath() );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> EntityDescriptor<T> getEntityDescriptor(String entityName) throws NotNavigableException {
		final EntityDescriptor<T> descriptor = (EntityDescriptor<T>) entityDescriptorMap.get( entityName );

		if ( descriptor == null ) {
			throw new NotNavigableException( entityName );
		}

		return descriptor;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> EntityDescriptor<T> findEntityDescriptor(Class<T> javaType) {
		return (EntityDescriptor<T>) entityDescriptorMap.get( javaType.getName() );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> EntityDescriptor<T> findEntityDescriptor(String entityName) {
		entityName = getImportedName( entityName );
		return (EntityDescriptor<T>) entityDescriptorMap.get( entityName );
	}

	@Override
	public void visitEntityDescriptors(Consumer<EntityDescriptor<?>> action) {
		entityDescriptorMap.values().forEach( action );
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// MappedSuperclassDescriptor


	protected Map<String, MappedSuperclassDescriptor<?>> getMappedSuperclassDescriptorMap() {
		return mappedSuperclassDescriptorMap;
	}

	@Override
	public <T> MappedSuperclassDescriptor<T> getMappedSuperclassDescriptor(NavigableRole role) throws NotNavigableException {
		return getMappedSuperclassDescriptor( role.getFullPath() );
	}

	@Override
	public <T> MappedSuperclassDescriptor<T> getMappedSuperclassDescriptor(Class<T> javaType) throws NotNavigableException {
		return getMappedSuperclassDescriptor( javaType.getName() );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> MappedSuperclassDescriptor<T> getMappedSuperclassDescriptor(String name) throws NotNavigableException {
		final MappedSuperclassDescriptor<T> descriptor = (MappedSuperclassDescriptor<T>) mappedSuperclassDescriptorMap.get( name );

		if ( descriptor == null ) {
			throw new NotNavigableException( name );
		}

		return descriptor;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> MappedSuperclassDescriptor<T> findMappedSuperclassDescriptor(Class<T> javaType) {
		return (MappedSuperclassDescriptor<T>) mappedSuperclassDescriptorMap.get( javaType.getName() );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> MappedSuperclassDescriptor<T> findMappedSuperclassDescriptor(String name) {
		name = getImportedName( name );
		return (MappedSuperclassDescriptor<T>) mappedSuperclassDescriptorMap.get( name );
	}

	@Override
	public void visitMappedSuperclassDescriptors(Consumer<MappedSuperclassDescriptor<?>> action) {
		mappedSuperclassDescriptorMap.values().forEach( action );
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// EmbeddedTypeDescriptor


	protected Map<String, EmbeddedTypeDescriptor<?>> getEmbeddedDescriptorMap() {
		return embeddedDescriptorMap;
	}

	@Override
	public <T> EmbeddedTypeDescriptor<T> findEmbeddedDescriptor(Class<T> javaType) {
		return findEmbeddedDescriptor( javaType.getName() );
	}

	@Override
	public <T> EmbeddedTypeDescriptor<T> findEmbeddedDescriptor(NavigableRole name) {
		return findEmbeddedDescriptor( name.getFullPath() );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> EmbeddedTypeDescriptor<T> findEmbeddedDescriptor(String name) {
		return (EmbeddedTypeDescriptor<T>) embeddedDescriptorMap.get( name );
	}

	@Override
	public void visitEmbeddedDescriptors(Consumer<EmbeddedTypeDescriptor<?>> action) {
		embeddedDescriptorMap.values().forEach( action );
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// PersistentCollectionDescriptor

	protected Map<String, PersistentCollectionDescriptor<?, ?, ?>> getCollectionDescriptorMap() {
		return collectionDescriptorMap;
	}

	@Override
	public <O, C, E> PersistentCollectionDescriptor<O, C, E> getCollectionDescriptor(NavigableRole name) throws NotNavigableException {
		return getCollectionDescriptor( name.getFullPath() );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <O, C, E> PersistentCollectionDescriptor<O, C, E> getCollectionDescriptor(String name) throws NotNavigableException {
		final PersistentCollectionDescriptor descriptor = findCollectionDescriptor( name );
		if ( descriptor == null ) {
			throw new NotNavigableException( name );
		}
		return descriptor;
	}

	@Override
	public <O,C,E> PersistentCollectionDescriptor<O,C,E> findCollectionDescriptor(NavigableRole name) {
		return findCollectionDescriptor( name.getFullPath() );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <O,C,E> PersistentCollectionDescriptor<O,C,E> findCollectionDescriptor(String name) {
		return (PersistentCollectionDescriptor<O,C,E>) collectionDescriptorMap.get( name );
	}

	@Override
	public void visitCollectionDescriptors(Consumer<PersistentCollectionDescriptor<?,?,?>> action) {
		collectionDescriptorMap.values().forEach( action );
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// EntityGraph


	protected Map<String, EntityGraphImplementor<?>> getEntityGraphMap() {
		return entityGraphMap;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> EntityGraphImplementor<? super T> findEntityGraph(String name) {
		return (EntityGraphImplementor<T>) entityGraphMap.get( name );
	}

	@Override
	public <T> List<EntityGraph<? super T>> findEntityGraphForType(Class<T> baseType) {
		return findEntityGraphForType( baseType.getName() );
	}

	@Override
	public <T> List<EntityGraph<? super T>> findEntityGraphForType(String baseTypeName) {
		final EntityDescriptor<? extends T> entityDescriptor = findEntityDescriptor( baseTypeName );
		if ( entityDescriptor == null ) {
			throw new IllegalArgumentException( "Not an entity : " + baseTypeName );
		}

		final List<EntityGraph<? super T>> results = new ArrayList<>();

		for ( EntityGraph entityGraph : entityGraphMap.values() ) {
			if ( !EntityGraphImplementor.class.isInstance( entityGraph ) ) {
				continue;
			}

			final EntityGraphImplementor egi = (EntityGraphImplementor) entityGraph;
			if ( egi.appliesTo( entityDescriptor ) ) {
				results.add( egi );
			}
		}

		return results;
	}

	@Override
	public void visitEntityGraphs(Consumer<EntityGraph<?>> action) {
		entityGraphMap.values().forEach( action );
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// EntityNameResolver

	@Override
	public Set<EntityNameResolver> getEntityNameResolvers() {
		return entityNameResolvers;
	}

	@Override
	public void visitEntityNameResolvers(Consumer<EntityNameResolver> action) {
		entityNameResolvers.forEach( action );
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// name imports

	protected Map<String, String> getNameImportMap() {
		return nameImportMap;
	}

	@Override
	public String getImportedName(String name) {
		return nameImportMap.getOrDefault( name, name );
	}
}