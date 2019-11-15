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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.Serializable;
import java.util.function.Function;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 22, 2018 12:15:08 AM
 */
public class PositionCenterScreen implements Function<Component, Boolean>, Serializable {

    @Override
    public Boolean apply(Component component) {
        try{
            final Dimension dim = component.getSize(); // c.getPreferredSize();
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final int left = screenSize.width/2 - dim.width/2;
            final int top = screenSize.height/2 - dim.height/2;
            component.setLocation(left, top);
            return Boolean.TRUE;
        }catch(Exception ignored) {
            return Boolean.FALSE;
        }
    }
}
