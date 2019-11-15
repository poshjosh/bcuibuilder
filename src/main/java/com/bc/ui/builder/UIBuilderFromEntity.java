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

//import com.bc.appcore.ObjectFactory;
import com.bc.ui.builder.model.ComponentModel;
import com.bc.typeprovider.MemberTypeProvider;
import com.bc.selection.SelectionContext;
import java.awt.Container;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2017 9:57:53 PM
 */
public interface UIBuilderFromEntity extends UIBuilder<Object, Container> {

    @Override
    Container build();

    UIBuilderFromEntity buildConsumer(BiConsumer<Container, Map> buildConsumer);
    
    @Override
    UIBuilderFromEntity sourceType(Class entityType);
    
    @Override
    UIBuilderFromEntity sourceData(Object entity);
    
    @Override
    UIBuilderFromEntity targetUI(Container target);
    
    @Override
    UIBuilderFromEntity typeProvider(MemberTypeProvider typeProvider);
    
    @Override
    UIBuilderFromEntity selectionContext(SelectionContext selectionContext);
    
    @Override
    UIBuilderFromEntity componentModel(ComponentModel componentModel);
    
    @Override
    UIBuilderFromEntity editable(Boolean editable);
}
