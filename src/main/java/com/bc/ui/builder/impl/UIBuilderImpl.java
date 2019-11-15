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

import com.bc.ui.builder.UIBuilder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 8, 2017 12:28:40 PM
 */
public class UIBuilderImpl<T> 
        extends AbstractUIBuilder<UIBuilder<T, Container>, T> 
        implements UIBuilder<T, Container> {
    
    private static final Logger LOG = Logger.getLogger(UIBuilderImpl.class.getName());

    @FunctionalInterface
    public static interface ContainerAttributesUpdater {
        void update(Container parentContainer, Container container, String name, int depth);
    }
    
    public static class ContainerAttributesUpdaterImpl implements ContainerAttributesUpdater{
        @Override
        public void update(Container parentContainer, Container container, String name, int depth) {
            final Color color = parentContainer.getBackground(); //getBgColor(parentContainer, container, name, depth);
            if(color != null) {
                container.setBackground(color);
            }
            if(container instanceof JComponent) {
                final Border border = getBorder(parentContainer, container, name, depth);
                if(border != null) {
                    ((JComponent)container).setBorder(border);
                }
            }
        }
        public Border getBorder(Container parentContainer, Container container, String name, int depth) {
            final boolean noname = name == null || name.isEmpty();
            final Border border;
            if(noname) {
                border = new BevelBorder(BevelBorder.RAISED);
            }else{
                final BevelBorder bevelBorder = new BevelBorder(BevelBorder.RAISED);
                final String title = Character.toTitleCase(name.charAt(0))+name.substring(1);
                final Font font = Font.decode("MONOSPACED-BOLD-24");
                border = new TitledBorder(bevelBorder, title, 
                        TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, font);
            }
            return border;
        }
        public Color getBgColor(Container parentContainer, Container container, String name, int depth) {
            final Color parentColor = parentContainer.getBackground();
            if(parentColor == null) {
                return null;
            }else{
                Color color = parentColor;
                while(depth > 0) {
                    color = color.brighter();
                    --depth;
                }
                return color;
            }
        }
    }
    
    private int depth;
    
    private final BiFunction<Class, T, Map> toMap;
    
    private final ContainerAttributesUpdater containerAttributesUpdater;

    public UIBuilderImpl(BiFunction<Class, T, Map> toMap) {
        this(toMap, new ContainerAttributesUpdaterImpl());
    }
    
    public UIBuilderImpl(BiFunction<Class, T, Map> toMap, 
            ContainerAttributesUpdater containerAttributesUpdater) {
        this.toMap = Objects.requireNonNull(toMap);
        this.containerAttributesUpdater = Objects.requireNonNull(containerAttributesUpdater);
    }
    
    @Override
    public boolean build(Class sourceType, T source, Container container) {
        
        LOG.log(Level.FINE, "Building: {0}", sourceType);
        
        final boolean output;
        
        if(!this.getTypeTest().test(sourceType)) {
        
            LOG.log(Level.FINER, "Filter rejected {0}", sourceType.getName());

            output = false;
            
        }else{
            
            final Collection<Component> components = this.buildComponents(sourceType, source, container);

            this.getComposer().accept(container, components);
            
            output = true;
        }
        
        return output;
    }
    
    public boolean isParent(Container parentContainer, Class sourceType, 
            String name, Object value, Class valueType) {
        return false;
    }
    
    @Override
    public Collection<Component> buildComponents(Class sourceType, T source, Container parentContainer) {
        
        final Map sourceData = this.toMap.apply(sourceType, source);
        
        return this.buildComponents(sourceType, source, sourceData, parentContainer);
    }
    
    public Collection<Component> buildComponents(
            Class sourceType, T source, Map sourceData, Container parentContainer) {
        
//        final Class sourceType = sourceData != null ? sourceData.getClass() : Map.class;
        
        final List<Component> components = new ArrayList();
        
        final Set entries = sourceData.entrySet(); 

        for(Object oval : entries) {

            final Map.Entry entry = (Map.Entry)oval;

            final String name = entry.getKey().toString();
            final Object value = entry.getValue();

            final Component ui = this.buildUI(parentContainer, sourceType, name, value, null);

            if(ui != null) {

                LOG.log(Level.FINER, "Adding ui: {0}", ui);
                ui.setName(name);
                components.add(ui);
            }
        }
        
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "Type: {0} keys: {1}, components: {2}", 
                    new Object[]{sourceType.getName(), sourceData.keySet(), components.size()});
        }
        
        return components;
    }
    
    public Component buildUI(Container parentContainer, Class sourceType, 
            String name, Object value, Component outputIfNone) {
        
        final Class valueTypeIfNone = null; //Object.class;
        final Class valueType = this.getTypeProvider().getType(sourceType, name, value, valueTypeIfNone);
        
        Objects.requireNonNull(valueType, "Failed to resolve type of " + 
                sourceType.getName()+"#"+name+" = "+value+", using "+
                this.getTypeProvider().getClass().getName());
        
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "{0}#{1} has type: {2}", 
                    new Object[]{sourceType.getName(), name, valueType});
        }

        return this.buildUI(parentContainer, sourceType, name, value, valueType, outputIfNone);
    }
    
    public Component buildUI(Container parentContainer, Class sourceType, 
            String name, Object value, Class valueType, Component outputIfNone) {
        
        LOG.finer(() -> MessageFormat.format("For {0}, {1} {2} = {3}", 
                sourceType.getSimpleName(), valueType.getSimpleName(), name, value));
        
        final String type;
        
        final Component entryUI;

        if(this.isParent(parentContainer, sourceType, name, value, valueType)) {
            
            type = "Container type";
            
            entryUI = this.buildUIForParent(parentContainer, sourceType, name, (T)value, valueType, outputIfNone);

        }else if(value instanceof Collection) {
            
            type = "Collection type";
            
            final Collection collection = (Collection)value;
            
            entryUI = this.buildUIForCollection(parentContainer, sourceType, 
                    name, collection, valueType, outputIfNone);
        }else{

            type = "Instance type";

            entryUI = this.getComponentModel().getComponent(sourceType, valueType, name, value);
        }
        
        LOG.finer(() -> MessageFormat.format("{0} for {1}, {2} {3} = {4}", 
                type, sourceType.getSimpleName(), valueType.getSimpleName(), name, value));
        
        if(entryUI != null) {
            entryUI.setName(name);
        }
        
        return entryUI == null ? outputIfNone : entryUI;
    }
    
    public Component buildUIForParent(Container parentContainer, Class sourceType, 
            String name, T child, Class valueType, Component outputIfNone) {
        
        final Level level = Level.FINER;
        
        final Component entryUI;

        if(!this.getTypeTest().test(valueType)) {

            if(LOG.isLoggable(level)) {
                LOG.log(level, "Filter rejected {0}#{1} with type: {2}", 
                        new Object[]{sourceType.getName(), name, valueType});
            }

            entryUI = null;

        }else{

            final Container childUI = this.createNamedContainer(valueType, child, name);

            ++depth;

            if(this.build(valueType, child, childUI)) {

                childUI.setName(name);

                if(LOG.isLoggable(level)) {
                    LOG.log(level, "Set name of: {0} to: {1}", 
                            new Object[]{childUI.getClass().getName(), name});
                }

                this.containerAttributesUpdater.update(parentContainer, childUI, name, depth);

                entryUI = childUI;

            }else{

                throw new UnsupportedOperationException("Failed to build ui for type "+sourceType+'#'+valueType+", with data names: "+toMap.apply(valueType, child).keySet());
            }

            --depth;
        }
        
        if(entryUI != null) {
            entryUI.setName(name);
        }
        
        return entryUI == null ? outputIfNone : entryUI;
    }

    public Component buildUIForCollection(Container parentContainer, Class sourceType, 
            String name, Collection value, Class valueType, Component outputIfNone) {
        
        final Level level = Level.FINER;
        
        final Component entryUI;

        final List<java.lang.reflect.Type> typeArgs = this.getTypeProvider()
                .getGenericTypeArguments(sourceType, name, null);
        
        final Class collectionGenericType = typeArgs.isEmpty() ? Object.class : (Class)typeArgs.get(0);
//        final Class collectionGenericType = (Class)typeArgs.get(0);

        if(LOG.isLoggable(level)) {
            LOG.log(level, "Collection type: {0}#{1} with generic type: {2}", 
                    new Object[]{sourceType.getName(), name, collectionGenericType.getName()});
        }

        final String genericName = collectionGenericType.getSimpleName();

        if(!this.getTypeTest().test(collectionGenericType)) {

            entryUI = null;

        }else{

            ++depth;

            final List<Component> components = new ArrayList();

            final Collection collection = (Collection)value;

            for(Object e : collection) {

                final Component c = this.buildUI(
                        parentContainer, valueType, genericName, e, collectionGenericType, null);

                if(c != null) {
                    components.add(c);
                }
            }

            final Container container = this.createNamedContainer(valueType, value, name); 

            this.getComposer().accept(container, components);

            container.setName(name);

            this.containerAttributesUpdater.update(parentContainer, container, name, depth);

            entryUI = container;

            --depth;
        }
        
        return entryUI == null ? outputIfNone : entryUI;
    }
}
