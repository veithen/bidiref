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

import com.github.veithen.jrel.DomainObject;

public final class OneToOneAssociation<T1 extends DomainObject,T2 extends DomainObject> extends ToOneAssociation<T1,T2,MutableReference<T1>> {
    private final OneToOneAssociation<T2,T1> converse;

    OneToOneAssociation(OneToOneAssociation<T2,T1> converse) {
        this.converse = converse;
    }

    public OneToOneAssociation() {
        converse = new OneToOneAssociation<T2,T1>(this);
    }

    public OneToOneAssociation<T2,T1> getConverse() {
        return converse;
    }
}
