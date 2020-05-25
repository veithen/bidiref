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
package com.github.veithen.jrel.association;

public final class OneToManyAssociation<T1,T2> extends ToManyAssociation<T1,T2,MutableReference<T1>> {
    private final ManyToOneAssociation<T2,T1> converse;

    OneToManyAssociation(Class<T1> type, ManyToOneAssociation<T2,T1> converse) {
        super(type);
        this.converse = converse;
    }

    public OneToManyAssociation(Class<T1> type1, Class<T2> type2) {
        super(type1);
        converse = new ManyToOneAssociation<T2,T1>(type2, this);
    }

    public ManyToOneAssociation<T2,T1> getConverse() {
        return converse;
    }
}
