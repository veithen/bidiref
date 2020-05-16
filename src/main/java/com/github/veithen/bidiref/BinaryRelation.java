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
package com.github.veithen.bidiref;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

public abstract class BinaryRelation<Type1,Type2,ReferenceHolder1 extends ReferenceHolder<Type2>,ReferenceHolder2 extends ReferenceHolder<Type1>,Self extends BinaryRelation<Type1,Type2,ReferenceHolder1,ReferenceHolder2,Self,Converse>,Converse extends BinaryRelation<Type2,Type1,ReferenceHolder2,ReferenceHolder1,Converse,Self>> implements BiPredicate<Type1,Type2> {
    private Function<Type1,ReferenceHolder1> referenceHolderFunction;

    /**
     * Get the converse, i.e. the binary relation with both ends reversed.
     * 
     * @return the converse relation
     */
    public abstract Converse getConverse();

    public final synchronized void bind(Function<Type1,ReferenceHolder1> referenceHolderFunction) {
        Objects.requireNonNull(referenceHolderFunction);
        if (this.referenceHolderFunction != null) {
            throw new IllegalStateException("Already bound");
        }
        this.referenceHolderFunction = referenceHolderFunction;
    }

    public final void bind(Function<Type1,ReferenceHolder1> referenceHolderFunction1, Function<Type2,ReferenceHolder2> referenceHolderFunction2) {
        bind(referenceHolderFunction1);
        getConverse().bind(referenceHolderFunction2);
    }

    public final synchronized ReferenceHolder1 getReferenceHolder(Type1 owner) {
        if (referenceHolderFunction == null) {
            throw new IllegalStateException("Not bound");
        }
        return referenceHolderFunction.apply(owner);
    }

    @Override
    public final boolean test(Type1 o1, Type2 o2) {
        return getReferenceHolder(o1).contains(o2);
    }
}