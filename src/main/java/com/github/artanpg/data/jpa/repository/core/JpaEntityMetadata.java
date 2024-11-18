package com.github.artanpg.data.jpa.repository.core;

import com.github.artanpg.data.commons.repository.core.EntityMetadata;

/**
 * JPA specific extension of {@link EntityMetadata}.
 *
 * @param <T> entity type
 * @author Mohammad Yazdian
 */
public interface JpaEntityMetadata<T> extends EntityMetadata<T> {

    /**
     * Returns the name of the entity.
     *
     * @return the name of the entity
     */
    String getEntityName();
}
