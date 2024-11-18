package com.github.artanpg.data.jpa.repository.core;

import com.github.artanpg.data.commons.repository.core.EntityInformation;
import jakarta.persistence.metamodel.SingularAttribute;

import java.util.Set;

/**
 * Extension of {@link EntityInformation} to capture additional JPA
 * specific information about entities.
 *
 * @param <T> the entity type
 * @param <I> the type of the identifier
 * @author Mohammad Yazdian
 */
public interface JpaEntityInformation<T, I> extends EntityInformation<T, I>, JpaEntityMetadata<T> {

    /**
     * Returns the id attribute of the entity.
     *
     * @return the id attribute of the entity
     */
    SingularAttribute<? super T, ?> getIdAttribute();

    /**
     * Returns the name of the id attributes.
     * <p><strong>Note:</strong> If the entity has a composite ID,
     * the name of the all id attributes are returned.</p>
     *
     * @return the name of the id attributes
     */
    Set<String> getIdAttributeNames();

    /**
     * Checks if the entity has a composite primary key.
     *
     * @return {@literal true} if the entity has a composite id
     */
    boolean hasCompositeId();

    /**
     * Extracts the value for the given id attribute from a
     * composite id
     *
     * @param entity      the entity with composite id attribute
     * @param idAttribute the attribute name to extract
     * @return the value of id attribute
     */
    Object getCompositeIdAttributeValue(T entity, String idAttribute);
}
