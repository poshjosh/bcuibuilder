/*
 * Copyright 2019 NUROX Ltd.
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Collection;
import java.util.function.BiConsumer;
import javax.swing.JLabel;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 27, 2019 2:07:32 PM
 */
public class SimpleUIComposer implements BiConsumer<Container, Collection<Component>>{

    @Override
    public void accept(Container container, Collection<Component> children) {

        container.setLayout(new GridLayout(children.size(), 2));
        
        for(Component component : children) {

            final String name = component.getName();
//            final Dimension preferredSize = component.getPreferredSize();
//            final Dimension size = component.getSize();
//            final Color background = component.getBackground();
//            final Color foreground = component.getForeground();
            final Font font = component.getFont();
            
            final JLabel label = new JLabel(name);
            label.setLabelFor(component);
//            label.setPreferredSize(preferredSize);
//            label.setSize(size);
//            label.setBackground(background);
//            label.setForeground(foreground);
            if(font != null) {
                label.setFont(font);
            }
            
            container.add(label);

            container.add(component);
        }
    }
}
