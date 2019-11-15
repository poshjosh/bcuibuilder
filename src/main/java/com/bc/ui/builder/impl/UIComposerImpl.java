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

package com.bc.ui.builder.impl;

import com.bc.ui.layout.SequentialLayout;
import java.awt.Component;
import java.awt.Container;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author Chinomso Bassey Ikwuagwu on May 10, 2018 9:29:18 PM
 */
public class UIComposerImpl implements Serializable, BiConsumer<Container, Collection<Component>> {

    private final SequentialLayout layout;

    public UIComposerImpl(SequentialLayout layout) {
        this.layout = Objects.requireNonNull(layout);
    }
    
    @Override
    public void accept(Container container, Collection<Component> children) {
        layout.addComponents(container, children);
    }
}
