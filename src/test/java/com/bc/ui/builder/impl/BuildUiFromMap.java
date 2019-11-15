/*
 * Copyright 2018 NUROX Ltd.
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

package com.bc.ui.builder.impl;

import com.bc.typeprovider.MemberTypeProvider;
import com.bc.typeprovider.TypeProviders;
import com.bc.selection.SelectionContext;
import java.awt.Component;
import java.awt.Container;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Chinomso Bassey Ikwuagwu on May 21, 2018 6:38:40 PM
 */
public class BuildUiFromMap implements Function<Map, Container>{

    @Override
    public Container apply(Map map) {
        
//        final ComponentModel model = new ComponentModelImpl();

        final MemberTypeProvider typeProvider =  TypeProviders.fromValueType();
        
        final BiConsumer<Container, Collection<Component>> composer = new SimpleUIComposer(); 
        
        final Container ui = new UIBuilderFromEntityMap()
//                .componentModel(model)
                .composer(composer)
                .typeProvider(typeProvider)
                .selectionContext(SelectionContext.NO_OP)
//                .sourceType(sourceType)
                .sourceData(map)
                .build();
        
        return ui;
    }
}
