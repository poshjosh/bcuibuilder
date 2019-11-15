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
import java.io.Serializable;
import java.util.Collection;
import java.util.function.BiFunction;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2018 3:57:55 PM
 */
public class ComputeMaxSizeFromValues implements BiFunction<Collection<Component>, Font, Dimension>, Serializable {
    
    private final ComputeSizeFromValue computeSize;

    public ComputeMaxSizeFromValues(Dimension defaultEntrySize) {
        this(null, defaultEntrySize);
    }
    
    public ComputeMaxSizeFromValues(ComponentModel componentModel, Dimension defaultEntrySize) {
        this.computeSize = new ComputeSizeFromValue(componentModel, defaultEntrySize);
    }

    @Override
    public Dimension apply(Collection<Component> x, Font font) {
        
        double maxWidth = 0;
        double maxHeight = 0;
        
        for(Component c : x) {
            
            final Dimension dim = this.computeSize.apply(c, font);
            
            maxWidth = Math.max(maxWidth, dim.getWidth());
            maxHeight = Math.max(maxHeight, dim.getHeight());
        }
        
        return new Dimension((int)maxWidth, (int)maxHeight);
    }
}
