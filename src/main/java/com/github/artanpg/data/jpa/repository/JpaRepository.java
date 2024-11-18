package com.github.artanpg.data.jpa.repository;

import com.github.artanpg.data.commons.repository.CrudRepository;
import com.github.artanpg.data.commons.repository.PagingAndOrderingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.util.Map;
import java.util.Optional;

/**
 * JPA specific extension of {@link com.github.artanpg.data.commons.repository.Repository}
 *
 * @param <T> the type of the entity to handle
 * @param <I> the type of the entity's identifier
 * @author Mohammad Yazdian
 */
public interface JpaRepository<T, I> extends CrudRepository<T, I>, PagingAndOrderingRepository<T, I> {

    /**
     * Search a record using primary key and specified properties
     *
     * @param id    the primary key
     * @param properties standard and vendor-specific properties
     * @return the entity with the given id and properties or {@literal Optional#empty()} if none found record
     */
    Optional<T> findById(I id, Map<String, Object> properties);

    /**
     * Search a record using primary key and lock mode.
     *
     * @param id the primary key
     * @param lockModeType the lock mode
     * @return the entity with the given id and properties or {@literal Optional#empty()} if none found record
     */
    Optional<T> findById(I id, LockModeType lockModeType);

    /**
     * Search a record using primary key and specified properties
     *
     * @param id the primary key
     * @param lockModeType the lock mode
     * @param properties standard and vendor-specific hints
     * @return the entity with the given id and properties or {@literal Optional#empty()} if none found record
     */
    Optional<T> findById(I id, LockModeType lockModeType, Map<String, Object> properties);

    /**
     * Returns a reference to the entity with the given identifier.
     *
     * @param id the primary key
     * @return a reference to the entity with the given id.
     */
    Optional<T> getReferenceById(I id);

    /**
     * Flushes all pending changes to the database.
     */
    void flush();
}
