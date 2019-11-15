/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.ui.builder;

import com.bc.ui.builder.model.ComponentModel;
import com.bc.typeprovider.MemberTypeProvider;
import com.bc.selection.SelectionContext;
import java.awt.Component;
import java.awt.Container;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2017 9:47:16 PM
 */
public interface UIBuilder<I, O extends Component> {

    UIBuilder<I, O> composer(BiConsumer<Container, Collection<Component>> composer);
    
    UIBuilder<I, O> typeTest(Predicate<Class> test);
    
    UIBuilder<I, O> sourceType(Class sourceType);
    
    UIBuilder<I, O> sourceData(I sourceData);
    
    UIBuilder<I, O> targetUI(O target);
    
    UIBuilder<I, O> selectionContext(SelectionContext selectionContext);
    
    UIBuilder<I, O> typeProvider(MemberTypeProvider typeProvider);
    
    UIBuilder<I, O> componentModel(ComponentModel componentModel);
    
    UIBuilder<I, O> editable(Boolean editable);
    
    boolean isBuilt();
    
    O build();

    Collection<Component> buildComponents(Class sourceType, I source, O parentContainer);
}
