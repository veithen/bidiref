/*-
 * #%L
 * jrel
 * %%
 * Copyright (C) 2020 Andreas Veithen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.veithen.jrel.transitive;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.github.veithen.bidiref.CollectionListener;
import com.github.veithen.bidiref.LinkedIdentityHashSet;
import com.github.veithen.bidiref.ListenableCollection;
import com.github.veithen.bidiref.MutableReferenceHolder;
import com.github.veithen.bidiref.ReferenceHolder;
import com.github.veithen.bidiref.SnapshotableCollection;

public final class TransitiveReferences<T> implements Set<T>, ListenableCollection<T>, SnapshotableCollection<T>, ReferenceHolder<T> {
    private final TransitiveRelation<T> relation;
    private final T owner;
    private MutableReferenceHolder<T> referenceHolder;
    private LinkedIdentityHashSet<T> set;

    TransitiveReferences(TransitiveRelation<T> relation, T owner) {
        this.relation = relation;
        this.owner = owner;
    }

    void init() {
        if (set != null) {
            return;
        }
        set = new LinkedIdentityHashSet<>();
        CollectionListener<T> transitiveReferencesListener = new CollectionListener<T>() {
            @Override
            public void added(T object) {
                set.add(object);
            }

            @Override
            public void removed(T object) {
                maybeRemove(object);
            }
        };
        referenceHolder = relation.getAssociation().getReferenceHolder(owner);
        CollectionListener<T> directReferenceListener = new CollectionListener<T>() {
            @Override
            public void added(T object) {
                set.add(object);
                TransitiveReferences<T> transitiveReferences = relation.getReferenceHolder(object);
                set.addAll(transitiveReferences);
                transitiveReferences.addListener(transitiveReferencesListener);
            }

            @Override
            public void removed(T object) {
                maybeRemove(object);
                TransitiveReferences<T> transitiveReferences = relation.getReferenceHolder(object);
                transitiveReferences.forEach(TransitiveReferences.this::maybeRemove);
                transitiveReferences.addListener(transitiveReferencesListener);
            }
        };
        referenceHolder.addListener(directReferenceListener);
        referenceHolder.forEach(directReferenceListener::added);
    }

    private void maybeRemove(T object) {
        if (referenceHolder.contains(object)) {
            return;
        }
        for (T reference : referenceHolder) {
            if (relation.getReferenceHolder(reference).contains(object)) {
                return;
            }
        }
        set.remove(object);
    }

    public void addListener(CollectionListener<? super T> listener) {
        init();
        set.addListener(listener);
    }

    public void removeListener(CollectionListener<? super T> listener) {
        init();
        set.removeListener(listener);
    }

    public boolean contains(Object object) {
        init();
        return set.contains(object);
    }

    public boolean isEmpty() {
        init();
        return set.isEmpty();
    }

    public Object[] toArray() {
        init();
        return set.toArray();
    }

    public <V> V[] toArray(V[] a) {
        init();
        return set.toArray(a);
    }

    public int size() {
        init();
        return set.size();
    }

    public Iterator<T> iterator() {
        init();
        // TODO: wrap the iterator to forbid calling remove
        return set.iterator();
    }

    public Iterable<T> snapshot() {
        init();
        return set.snapshot();
    }

    public boolean containsAll(Collection<?> c) {
        init();
        return set.containsAll(c);
    }

    public String toString() {
        init();
        return set.toString();
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}