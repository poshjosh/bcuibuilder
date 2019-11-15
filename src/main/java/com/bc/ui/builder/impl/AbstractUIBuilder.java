/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance sourceData the License.
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

import com.bc.ui.builder.model.impl.ComponentPropertiesImpl;
import com.bc.ui.builder.UIBuilder;
import com.bc.typeprovider.MemberTypeProvider;
import com.bc.typeprovider.TypeProviders;
import com.bc.selection.SelectionContext;
import com.bc.ui.builder.model.ComponentModel;
import com.bc.ui.builder.model.ComponentModel.ComponentProperties;
import com.bc.ui.builder.model.impl.ComponentModelImpl;
import com.bc.ui.layout.VerticalLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 25, 2017 10:00:57 PM
 */
public abstract class AbstractUIBuilder<U extends UIBuilder<I, Container>, I> 
        implements UIBuilder<I, Container> {

    private static final Logger logger = Logger.getLogger(AbstractUIBuilder.class.getName());
    
    private boolean built;
    private Class sourceType;
    private I sourceData;
    private Container targetUI;
    private MemberTypeProvider typeProvider;
    private SelectionContext selectionContext;
    private ComponentModel componentModel;
    private BiConsumer<Container, Collection<Component>> composer;
    private Predicate<Class> typeTest;
    private Boolean editable;
    
    public AbstractUIBuilder() {
        this.editable = Boolean.TRUE;
    }
    
    public abstract boolean build(Class sourceType, I source, Container container);
    
    public void init() {

        if(this.typeTest == null) {
            this.typeTest = (cls) -> true;
        }

        if(this.composer == null) {
            this.composer = new UIComposerImpl(new VerticalLayout());
        }
        
        if(this.sourceType == null) {
            this.sourceType(this.sourceData.getClass());
        }

        if(this.targetUI == null) {
            this.targetUI(this.createNamedContainer(this.sourceType, this.sourceData, null));
        }
        
        if(this.typeProvider == null) {
            this.typeProvider = TypeProviders.fromValueType();
        }
        
        if(this.selectionContext == null) {
            this.selectionContext = SelectionContext.NO_OP;
        }
        
        if(this.componentModel == null) {
            this.componentModel = new ComponentModelImpl();
        }
        
        ComponentProperties componentProperties = this.componentModel.getComponentProperties();
        
        if(componentProperties.isEditable(targetUI) != this.editable) {
            
            componentProperties = new ComponentPropertiesImpl(componentProperties) {
                @Override
                public boolean isEditable(Component component) {
                    return editable;
                }
            };

            logger.log(Level.FINE, "Updating `editable` property of components to: {0}", editable);

            this.componentModel(componentModel.deriveNewFrom(componentProperties));
        }
    }
    
    @Override
    public Container build() {
        
        if(this.isBuilt()) {
            throw new IllegalStateException("build() method may only be called once");
        }
        
        this.built = true;
        
        this.init();
        
        if(this.build(this.sourceType, this.sourceData, this.targetUI)) {

            return this.targetUI;
            
        }else{
            
            throw new IllegalArgumentException("Build failed");
        }
    }

    public Container createNamedContainer(Class sourceType, Object sourceData, String name) {
        final JPanel panel = new JPanel();
        panel.setName(name);
        return panel;
    }
    
    @Override
    public U typeTest(Predicate<Class> typeTest) {
        this.typeTest = typeTest;
        return (U)this;
    }
    
    @Override
    public U composer(BiConsumer<Container, Collection<Component>> component) {
        this.composer = component;
        return (U)this;
    }

    @Override
    public U sourceType(Class sourceType) {
        this.sourceType = sourceType;
        return (U)this;
    }
    
    @Override
    public U sourceData(I source) {
        this.sourceData = source;
        return (U)this;
    }

    @Override
    public U targetUI(Container target) {
        this.targetUI = target;
        return (U)this;
    }

    @Override
    public U typeProvider(MemberTypeProvider typeProvider) {
        this.typeProvider = typeProvider;
        return (U)this;
    }

    @Override
    public U selectionContext(SelectionContext selectionContext) {
        this.selectionContext = selectionContext;
        return (U)this;
    }
    
    @Override
    public U componentModel(ComponentModel componentModel) {
        this.componentModel = componentModel;
        return (U)this;
    }

    @Override
    public U editable(Boolean editable) {
        this.editable = editable;
        return (U)this;
    }

    @Override
    public boolean isBuilt() {
        return this.built;
    }

    public MemberTypeProvider getTypeProvider() {
        return typeProvider;
    }

    public SelectionContext getSelectionContext() {
        return selectionContext;
    }

    public ComponentModel getComponentModel() {
        return componentModel;
    }

    public boolean isEditable() {
        return editable;
    }

    public Class getSourceType() {
        return sourceType;
    }

    public I getSourceData() {
        return sourceData;
    }

    public Container getTargetUI() {
        return targetUI;
    }

    public Predicate<Class> getTypeTest() {
        return typeTest;
    }

    public BiConsumer<Container, Collection<Component>> getComposer() {
        return composer;
    }
}
