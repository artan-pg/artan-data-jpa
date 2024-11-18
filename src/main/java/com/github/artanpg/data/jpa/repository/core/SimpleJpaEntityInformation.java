package com.github.artanpg.data.jpa.repository.core;

import com.github.artanpg.data.commons.repository.core.support.AbstractEntityInformation;
import com.github.artanpg.data.commons.utils.Assert;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.IdentifiableType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;

import java.util.Set;

/**
 * Simple implementations of {@link JpaEntityInformation}.
 *
 * @param <T> the entity type
 * @param <I> the type of the identifier
 * @author Mohammad Yazdian
 */
public class SimpleJpaEntityInformation<T, I> extends AbstractEntityInformation<T, I> implements JpaEntityInformation<T, I> {

    private final String entityName;
    private final PersistenceUnitUtil persistenceUnitUtil;
    private final IdMetadata<T> idMetadata;

    public SimpleJpaEntityInformation(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass);

        Assert.notNull(entityManager, "The entityManager must not be null.");

        Metamodel metamodel = entityManager.getMetamodel();
        this.persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

        ManagedType<T> managedType = metamodel.managedType(domainClass);
        Assert.notNull(managedType, "The given domainClass can not be found in the given Metamodel object.");

        this.entityName = managedType instanceof EntityType<?> entityType ?
                entityType.getName() :
                domainClass.getSimpleName();

        if (!(managedType instanceof IdentifiableType<T> identifiableType)) {
            throw new IllegalArgumentException("The given domainClass does not contain an id attribute");
        }

        idMetadata = IdMetadata.of(identifiableType);
    }

    @Override
    public SingularAttribute<? super T, ?> getIdAttribute() {
        return idMetadata.getSimpleIdAttribute();
    }

    @Override
    public Set<String> getIdAttributeNames() {
        return idMetadata.getIdAttributeNames();
    }

    @Override
    public boolean hasCompositeId() {
        return idMetadata.hasCompositeId();
    }

    @Override
    public Object getCompositeIdAttributeValue(T entity, String idAttribute) {
        return idMetadata.getCompositeIdAttributeValue(entity, idAttribute);
    }

    @SuppressWarnings("unchecked")
    @Override
    public I getId(T entity) {
        return (I) persistenceUnitUtil.getIdentifier(entity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<I> getIdType() {
        return (Class<I>) idMetadata.getIdType();
    }

    @Override
    public String getEntityName() {
        return entityName;
    }
}
