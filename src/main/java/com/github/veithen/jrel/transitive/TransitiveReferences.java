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

import com.github.veithen.jrel.ReferenceHolder;
import com.github.veithen.jrel.References;
import com.github.veithen.jrel.association.MutableReference;
import com.github.veithen.jrel.collection.SetListener;
import com.github.veithen.jrel.collection.LinkedIdentityHashSet;
import com.github.veithen.jrel.collection.ListenableSet;

final class TransitiveReferences<T> implements Set<T>, ListenableSet<T>, References<T> {
    private final TransitiveClosure<T> closure;
    private final T owner;
    private ReferenceHolder<T> referenceHolder;
    private LinkedIdentityHashSet<T> set;

    TransitiveReferences(TransitiveClosure<T> closure, T owner) {
        this.closure = closure;
        this.owner = owner;
    }

    void init() {
        if (set != null) {
            return;
        }
        set = new LinkedIdentityHashSet<>();
        SetListener<T> transitiveReferencesListener = new SetListener<T>() {
            @Override
            public void added(T object) {
                set.add(object);
            }

            @Override
            public void removed(T object) {
                maybeRemove(object);
            }
        };
        referenceHolder = closure.getRelation().getReferenceHolder(owner);
        SetListener<T> directReferenceListener = new SetListener<T>() {
            @Override
            public void added(T object) {
                set.add(object);
                ListenableSet<T> transitiveReferences = closure.getReferenceHolder(object).asSet();
                set.addAll(transitiveReferences);
                transitiveReferences.addListener(transitiveReferencesListener);
            }

            @Override
            public void removed(T object) {
                maybeRemove(object);
                ListenableSet<T> transitiveReferences = closure.getReferenceHolder(object).asSet();
                transitiveReferences.forEach(TransitiveReferences.this::maybeRemove);
                transitiveReferences.removeListener(transitiveReferencesListener);
            }
        };
        referenceHolder.asSet().addListener(directReferenceListener);
        referenceHolder.asSet().forEach(directReferenceListener::added);
    }

    private void maybeRemove(T object) {
        // Optimization if the underlying association is many-to-one.
        if (!(referenceHolder instanceof MutableReference)) {
            if (referenceHolder.asSet().contains(object)) {
                return;
            }
            for (T reference : referenceHolder.asSet()) {
                if (closure.getReferenceHolder(reference).contains(object)) {
                    return;
                }
            }
        }
        set.remove(object);
    }

    @Override
    public ListenableSet<T> asSet() {
        return this;
    }

    public void addListener(SetListener<? super T> listener) {
        init();
        set.addListener(listener);
    }

    public void removeListener(SetListener<? super T> listener) {
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
        Iterator<T> it = set.iterator();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
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
