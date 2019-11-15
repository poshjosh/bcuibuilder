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

package com.bc.ui.builder.model;

import com.bc.ui.builder.model.impl.ComponentPropertiesImpl;
import com.bc.selection.Selection;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 6, 2017 11:05:31 AM
 */
public interface ComponentModel {

    interface ComponentProperties {
        
        ComponentProperties DEFAULT = new ComponentPropertiesImpl();

        Font getFont(Component component);
        
        default Dimension getDimension(Component component) {
            return new Dimension(this.getWidth(component), this.getHeight(component));
        }

        int getWidth(Component component);

        int getHeight(Component component);

        boolean isEnabled(Component component);

        boolean isEditable(Component component);
    }

    boolean isPasswordName(String name);
        
    ComponentModel deriveNewFrom(ComponentProperties properties);
    
    ComponentProperties getComponentProperties();
    
    Component getComponent(Class parentType, Class valueType, String name, Object value);
    
    List<Selection> getSelectionValues(Class parentType, Class valueType, String name, Object value);
    
    Object getValue(Component component, Object outputIfNone);
    
    <T> T setValue(Component component, T value);
}
