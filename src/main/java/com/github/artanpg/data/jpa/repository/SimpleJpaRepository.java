package com.github.artanpg.data.jpa.repository;

import com.github.artanpg.data.commons.domain.Orders;
import com.github.artanpg.data.commons.domain.Page;
import com.github.artanpg.data.commons.domain.Pageable;
import com.github.artanpg.data.commons.utils.Assert;
import com.github.artanpg.data.jpa.repository.core.JpaEntityInformation;
import com.github.artanpg.data.jpa.repository.core.SimpleJpaEntityInformation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The simple implementation class of the {@link JpaRepository}
 * interface using the {@link EntityManager} interface.
 *
 * <p><strong>Important note:</strong> Transaction management is the
 * responsibility of the user</p>
 *
 * @param <T> the type of the entity to handle
 * @param <I> the type of the entity's identifier
 */
public abstract class SimpleJpaRepository<T, I> implements JpaRepository<T, I> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleJpaRepository.class);

    private static final String ID_MUST_NOT_BE_NULL = "The given id must not be null.";

    private final EntityManager entityManager;
    private final JpaEntityInformation<T, ?> entityInformation;

    protected SimpleJpaRepository(EntityManager entityManager, Class<T> domainClass) {
        Assert.notNull(entityManager, "The entityManager must not be null.");
        Assert.notNull(domainClass, "The domainClass must not be null.");

        this.entityManager = entityManager;
        this.entityInformation = new SimpleJpaEntityInformation<>(domainClass, entityManager);
    }

    @Override
    public Optional<T> findById(I id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return Optional.ofNullable(entityManager.find(entityInformation.getJavaType(), id));
    }

    @Override
    public Optional<T> findById(I id, Map<String, Object> hints) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return Optional.ofNullable(entityManager.find(entityInformation.getJavaType(), id, hints));
    }

    @Override
    public Optional<T> findById(I id, LockModeType lockModeType) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return Optional.ofNullable(entityManager.find(entityInformation.getJavaType(), id, lockModeType));
    }

    @Override
    public Optional<T> findById(I id, LockModeType lockModeType, Map<String, Object> hints) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return Optional.ofNullable(entityManager.find(entityInformation.getJavaType(), id, lockModeType, hints));
    }

    @Override
    public Optional<T> getReferenceById(I id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return Optional.ofNullable(entityManager.getReference(entityInformation.getJavaType(), id));
    }

    @Override
    public List<T> findAll() {
        return getQuery(entityInformation.getJavaType()).getResultList();
    }

    @Override
    public T save(T entity) {
        Assert.notNull(entity, "The entity must not be null.");

        if (entityInformation.isNewRecord(entity)) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    @Override
    public List<T> saveAll(List<T> entities) {
        return null;
    }

    @Override
    public void delete(T entity) {

    }

    @Override
    public void delete(List<T> entities) {

    }

    @Override
    public void deleteById(I id) {

    }

    @Override
    public void deleteById(List<I> ids) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public long count(String fieldName) {
        return 0;
    }

    @Override
    public boolean existsById(I id) {
        return false;
    }

    @Override
    public List<T> findAll(Orders orders) {
        return null;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public void flush() {
        entityManager.flush();
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected TypedQuery<T> getQuery(Class<T> domainClass) {
        return getQuery(domainClass, null);
    }

    protected TypedQuery<T> getQuery(Class<T> domainClass, Orders orders, Predicate... predicate) {
        Assert.notNull(domainClass, "domainClass must not be null.");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(domainClass);

        Root<T> root = criteriaQuery.from(domainClass);

        if (Objects.nonNull(predicate) &&  predicate.length > 0) {
            criteriaQuery.where(predicate);
        }

        criteriaQuery.select(root);

//        if (orders.iterator().hasNext()) {
//            criteriaQuery.orderBy(toOrders(sort, root, criteriaBuilder));
//        }

        return entityManager.createQuery(criteriaQuery);
    }

    protected TypedQuery<T> getQuery(T entity) {
        return getQuery(entity, null);
    }

    protected TypedQuery<T> getQuery(T entity, Orders orders, Predicate... predicate) {
        Assert.notNull(entity, "entity must not be null.");

        EntityType<T> entityType = (EntityType<T>) entityManager.getMetamodel().entity(entity.getClass());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityType.getJavaType());

        Root<T> root = criteriaQuery.from(entityType);

        if (Objects.nonNull(predicate) &&  predicate.length > 0) {
            criteriaQuery.where(predicate);
        }

        criteriaQuery.select(root);

//        if (orders.iterator().hasNext()) {
//            criteriaQuery.orderBy(toOrders(sort, root, criteriaBuilder));
//        }

        return entityManager.createQuery(criteriaQuery);
    }
}
