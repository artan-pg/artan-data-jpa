package com.github.artanpg.data.jpa.repository.core;

import com.github.artanpg.data.commons.utils.Assert;
import jakarta.persistence.IdClass;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.IdentifiableType;
import jakarta.persistence.metamodel.SingularAttribute;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simple value object to encapsulate id specific metadata.
 *
 * @param <T> the entity type
 * @author Mohammad Yazdian
 */
class IdMetadata<T> implements Iterable<SingularAttribute<? super T, ?>> {

    private final Map<String, Field> fieldCache = new ConcurrentHashMap<>();

    private final IdentifiableType<T> identifiableType;
    private final Set<SingularAttribute<? super T, ?>> idClassAttributes;
    private final Set<SingularAttribute<? super T, ?>> idAttributes;

    private Class<?> idType;

    private IdMetadata(IdentifiableType<T> identifiableType) {
        this.identifiableType = identifiableType;
        this.idClassAttributes = getIdClassAttributes(identifiableType);
        this.idAttributes = identifiableType.hasSingleIdAttribute() ?
                Collections.singleton(identifiableType.getId(identifiableType.getIdType().getJavaType())) :
                identifiableType.getIdClassAttributes();
    }

    static <T> IdMetadata<T> of(IdentifiableType<T> identifiableType) {
        return new IdMetadata<>(identifiableType);
    }

    SingularAttribute<? super T, ?> getSimpleIdAttribute() {
        return idAttributes.iterator().next();
    }

    Set<String> getIdAttributeNames() {
        return idAttributes.stream().map(Attribute::getName).collect(Collectors.toUnmodifiableSet());
    }

    boolean hasCompositeId() {
        return idClassAttributes.isEmpty() && idAttributes.size() == 1;
    }

    Object getCompositeIdAttributeValue(T entity, String idAttribute) {
        Assert.notNull(entity, "The entity must not be null.");
        Assert.notNull(idAttribute, "The idAttribute must not be null.");
        Assert.isFalse(hasCompositeId(), "Entity must have a composite Id.");

        String key = generateFieldCacheKey(entity, idAttribute);
        if (fieldCache.containsKey(key)) {
            return invokeGetterField(fieldCache.get(key), entity);
        }

        return invokeGetterField(cacheAndReturn(key, idAttribute), entity);
    }

    private String generateFieldCacheKey(T entity, String idAttribute) {
        return entity.getClass().getSimpleName() + "." + idAttribute;
    }

    private Object invokeGetterField(Field field, T entity) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Field cacheAndReturn(String key, String idAttribute) {
        Field field = filterAndGetField(idAttribute);
        fieldCache.put(key, field);
        return field;
    }

    private Field filterAndGetField(String idAttribute) {
        return idAttributes.stream()
                .filter(singularAttribute -> singularAttribute.getName().equals(idAttribute))
                .map(Attribute::getJavaMember)
                .filter(Field.class::isInstance)
                .map(Field.class::cast)
                .findFirst()
                .orElseThrow();
    }

    Class<?> getIdType() {
        if (Objects.nonNull(idType)) {
            return idType;
        }

        idType = lookupIdType(identifiableType);

        if (Objects.isNull(idType)) {
            throw new IllegalStateException("Cannot resolve Id type from " + identifiableType);
        }

        return this.idType;
    }

    private Class<?> lookupIdType(IdentifiableType<?> identifiableType) {
        Class<?> lookupIdClass = lookupIdClass(identifiableType);
        if (Objects.nonNull(lookupIdClass)) {
            return lookupIdClass;
        }

        return Objects.nonNull(identifiableType.getIdType()) ? identifiableType.getIdType().getJavaType() : null;
    }

    private Class<?> lookupIdClass(IdentifiableType<?> identifiableType) {
        IdClass idClass = Objects.nonNull(identifiableType.getJavaType())
                ? identifiableType.getJavaType().getAnnotation(IdClass.class)
                : null;
        return Objects.nonNull(idClass) ? idClass.value() : null;
    }

    @Override
    public Iterator<SingularAttribute<? super T, ?>> iterator() {
        return idAttributes.iterator();
    }

    private Set<SingularAttribute<? super T, ?>> getIdClassAttributes(IdentifiableType<T> identifiableType) {
        try {
            return identifiableType.getIdClassAttributes();
        } catch (IllegalArgumentException e) {
            return Collections.emptySet();
        }
    }
}
