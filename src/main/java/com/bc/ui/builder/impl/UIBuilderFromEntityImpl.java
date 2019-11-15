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

package com.bc.ui.builder.impl;

import com.bc.ui.builder.UIBuilderFromEntity;
import com.bc.ui.builder.UIBuilderFromMap;
//import com.bc.appbase.ui.functions.AddAccessToViewRelatedTypes;
//import com.bc.appcore.ObjectFactory;
//import com.bc.appcore.functions.BuildEntityStructure;
import com.bc.util.JsonFormat;
import java.awt.Component;
import java.awt.Container;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2017 9:38:51 PM
 */
public class UIBuilderFromEntityImpl extends AbstractUIBuilder<UIBuilderFromEntity, Object> 
        implements UIBuilderFromEntity {

    private static final Logger logger = Logger.getLogger(UIBuilderFromEntityImpl.class.getName());
    
    private BiConsumer<Container, Map> buildConsumer;
    
    private final BiFunction<Class, Object, Map> entityStructureBuilder;
    
    private final UIBuilderFromMap uiFromMapBuilder;
    
    public UIBuilderFromEntityImpl(
            BiFunction<Class, Object, Map> buildEntityStructure,
            UIBuilderFromMap uiFromMapBuilder) { 
        this.buildConsumer = (ui, structure) -> {};
        this.entityStructureBuilder = Objects.requireNonNull(buildEntityStructure);
        this.uiFromMapBuilder = Objects.requireNonNull(uiFromMapBuilder);
    }

    @Override
    public UIBuilderFromEntity buildConsumer(BiConsumer<Container, Map> buildConsumer) {
        this.buildConsumer = buildConsumer;
        return this;
    }
    
    @Override
    public boolean build(Class entityType, Object entity, Container container) {

        final Map structure = this.entityStructureBuilder.apply(entityType, entity);

        logger.finer(() -> new JsonFormat(true, true, "  ").toJSONString(structure));

        final Container ui = this.build(structure, container);
        
        this.buildConsumer.accept(ui, structure);
        
        return true;
    }
    
    public Container build(Map structure, Container container) {
    
        final Container ui = (Container)uiFromMapBuilder
                .typeTest(this.getTypeTest())
                .sourceType(this.getSourceType())
                .sourceData(structure)
                .targetUI(container)
                .selectionContext(this.getSelectionContext())
                .typeProvider(this.getTypeProvider()) 
                .componentModel(this.getComponentModel())
                .editable(this.isEditable())      
                .build();
        
        return ui;
    }

    @Override
    public Collection<Component> buildComponents(Class sourceType, Object source, Container parentContainer) {
        return uiFromMapBuilder.buildComponents(sourceType, (Map)source, parentContainer);
    }
}
