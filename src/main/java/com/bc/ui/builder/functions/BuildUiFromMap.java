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

package com.bc.ui.builder.functions;

import com.bc.typeprovider.MemberTypeProvider;
import com.bc.typeprovider.TypeProviders;
import com.bc.selection.SelectionContext;
import com.bc.selection.SelectionValues;
import com.bc.ui.builder.impl.UIBuilderFromEntityMap;
import com.bc.ui.builder.model.ComponentModel;
import com.bc.ui.builder.model.impl.ComponentModelImpl;
import com.bc.ui.functions.ComputeMaxSizeFromValues;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 * @author Chinomso Bassey Ikwuagwu on May 21, 2018 6:38:40 PM
 */
public class BuildUiFromMap implements BiFunction<Map, Font, Container>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(BuildUiFromMap.class.getName());

    private final MemberTypeProvider typeProvider;
    
    private final ComponentModel componentModel;
    
    private final Dimension defaultEntrySize;

    public BuildUiFromMap(Dimension defaultEntrySize) {
        this(
                TypeProviders.fromValueType(), 
                new ComponentModelImpl(
                        SelectionValues.from(Collections.EMPTY_LIST), 
                        ComponentModel.ComponentProperties.DEFAULT, 
                        100
                ),
                defaultEntrySize
        );
    }
    
    public BuildUiFromMap(
            MemberTypeProvider typeProvider, 
            ComponentModel componentModel,
            Dimension defaultEntrySize) {
        this.typeProvider = Objects.requireNonNull(typeProvider);
        this.componentModel = Objects.requireNonNull(componentModel);
        this.defaultEntrySize = Objects.requireNonNull(defaultEntrySize);
    }
    
    @Override
    public Container apply(Map map, Font font) {
        
        final BiConsumer<Container, Collection<Component>> entryComposer = (container, children) -> {
            composeUiEntries(container, children, font);
        }; 
        
        final Container ui = new UIBuilderFromEntityMap()
                .composer(entryComposer)
                .componentModel(componentModel)
                .typeProvider(typeProvider)
                .selectionContext(SelectionContext.NO_OP)
//                .sourceType(sourceType)
                .sourceData(map)
                .build();
        
        return ui;
    }
    
    private void composeUiEntries(Container container, Collection<Component> children, Font font) {
        
        final Dimension temp = new ComputeMaxSizeFromValues(componentModel, this.defaultEntrySize).apply(children, font);
        final float factor = 1.2f;
        final int minW = 200; final int minH = 20;
        final int w = (int)(temp.getWidth() * factor);
        final int entryW = w < minW ? minW : w;
        final int h = (int)(temp.getHeight() * factor);
        final int entryH = h < minH ? minH : h;
        final Dimension entrySize = new Dimension(entryW, entryH);
        
        final Dimension size = new Dimension(entrySize.width * 2, entrySize.height * children.size());
        LOG.fine(() -> "Entry size: " + entrySize + ", container size: " + size + ", container: " + container);
        
        container.setLayout(new GridLayout(children.size(), 2));
        container.setPreferredSize(size);
//        container.setSize(size);
        container.setFont(font);

        for(Component component : children) {
            final JLabel label = new JLabel(component.getName());
            container.add(label);
            container.add(component);
            label.setPreferredSize(entrySize);
//            label.setSize(entrySize);
            label.setFont(font);
            component.setPreferredSize(entrySize);
//            component.setSize(entrySize);
            component.setFont(font);
        }
    }
}
