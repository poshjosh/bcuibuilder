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

import com.bc.ui.builder.UIBuilderFromMap;
import java.awt.Component;
import java.awt.Container;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 22, 2017 5:15:16 PM
 */
public class UIBuilderFromEntityMap extends UIBuilderImpl<Map> implements UIBuilderFromMap {

    private static final Logger LOG = Logger.getLogger(UIBuilderFromEntityMap.class.getName());
        
    public UIBuilderFromEntityMap() {
        super((type, map) -> map);
    }

    @Override
    public Collection<Component> buildComponents(Class sourceType, Map source, Map sourceData, Container parentContainer) {
        return super.buildComponents(sourceType, source, sourceData, parentContainer);
    }
    
    @Override
    public boolean isParent(Container parentContainer, Class sourceType, 
            String name, Object value, Class valueType) {
        return value != null && Map.class.isAssignableFrom(value.getClass()) || 
                Map.class.isAssignableFrom(valueType);
    }

    @Override
    public Component buildUI(Container parentContainer, Class sourceType, 
            String name, Object value, Class valueType, Component outputIfNone) {
//System.out.println(valueType.getSimpleName()+' '+name+'='+value+". @"+this.getClass());                
        final Component output;
        
        final boolean selectionType = this.getSelectionContext().isSelectionType(valueType);

        if(selectionType) {    

            if(LOG.isLoggable(Level.FINER)) {
                LOG.log(Level.FINER, "Selection type: {0}#{1}", new Object[]{sourceType.getName(), name});
            }

            output = this.getComponentModel().getComponent(sourceType, valueType, name, value); 
            
        }else{

            output = super.buildUI(parentContainer, sourceType, name, value, valueType, outputIfNone);
        }  
            
        return output == null ? outputIfNone : output;
    }
}
