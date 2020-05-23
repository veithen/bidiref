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
package com.github.veithen.jrel.collection;

import java.util.Collections;
import java.util.Iterator;

public final class SingletonIdentitySet<T> extends AbstractListenableSet<T> {
    private T element;

    public void set(T element) {
        if (this.element == element) {
            return;
        }
        clear();
        this.element = element;
        if (element != null) {
            fireAdded(element);
        }
    }

    public T get() {
        return element;
    }

    @Override
    public void clear() {
        if (element != null) {
            fireRemoved(element);
            element = null;
        }
    }

    @Override
    public boolean add(T element) {
        if (this.element == element) {
            return false;
        }
        clear();
        this.element = element;
        fireAdded(element);
        return true;
    }

    @Override
    public boolean remove(Object element) {
        // TODO: missing listere invocation here
        if (this.element != element) {
            return false;
        }
        this.element = null;
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return element == null ? Collections.emptyIterator() : Collections.singleton(element).iterator();
    }

    @Override
    public boolean contains(Object element) {
        return element != null && this.element == element;
    }

    @Override
    public int size() {
        return element == null ? 0 : 1;
    }
}