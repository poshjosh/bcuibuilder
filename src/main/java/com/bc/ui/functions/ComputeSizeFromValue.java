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

package com.bc.ui.functions;

import com.bc.ui.builder.model.ComponentModel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2018 3:45:27 PM
 */
public class ComputeSizeFromValue implements BiFunction<Component, Font, Dimension>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(ComputeSizeFromValue.class.getName());
    
    private final ComponentModel componentModel;
    
    private final Dimension defaultSize;

    public ComputeSizeFromValue(Dimension defaultSize) {
        this(null, defaultSize);
    }
    
    public ComputeSizeFromValue(ComponentModel componentModel, Dimension defaultSize) {
        this.componentModel = componentModel;
        this.defaultSize = Objects.requireNonNull(defaultSize);
    }

    @Override
    public Dimension apply(Component c, Font font) {
        
        final String value = this.getValue(c, null);
        
        final FontMetrics fm = getFontMetrics(c, font, null);
        LOG.finest(() -> "Font metrics: " + fm + ", component: " + c.getClass().getSimpleName());
        
        double width = defaultSize.getWidth();
        
        if(value != null && fm != null) {
            
            try{
                final double w = width;
                width = fm.getStringBounds(value, c.getGraphics()).getWidth();
                LOG.finest(() -> "Computed width: " + w + " for value: " + value + 
                        ", component: " + c.getClass().getSimpleName());
            }catch(Exception ignored) { }
        }
        
        LOG.finest(() -> "Computed height: " + (fm==null?null:fm.getHeight()) + " for value: " + value + 
                ", component: " + c.getClass().getSimpleName());
                
        final double height = fm == null ? defaultSize.getHeight() : fm.getHeight();
        
        return new Dimension((int)width, (int)height);
    }
    
    private FontMetrics getFontMetrics(Component c, Font font, FontMetrics resultIfNone) {
        FontMetrics fm = null;
        try{
            fm = c.getFontMetrics(font);
        }catch(Exception ignored) {  }
        return fm == null ? resultIfNone : fm;
    }
    
    public String getValue(Component c, String outputIfNone) {
        final Object value = componentModel == null ? null : componentModel.getValue(c, null);
        final String sval = value == null ? null : value.toString();
        final int valLen = sval == null ? 0 : sval.length();
        final String name = c.getName();
        final int nameLen = name == null ? 0 : name.length();
        final String output = valLen >= nameLen ? sval : name;
        return output == null ? outputIfNone : output;
    }
}
