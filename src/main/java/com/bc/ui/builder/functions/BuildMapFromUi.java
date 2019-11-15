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

import com.bc.selection.SelectionValues;
import com.bc.ui.builder.FromUIBuilder;
import com.bc.ui.builder.impl.MapFromUIBuilder;
import com.bc.ui.builder.model.ComponentModel;
import com.bc.ui.builder.model.impl.ComponentModelImpl;
import java.awt.Container;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author Chinomso Bassey Ikwuagwu on May 21, 2018 6:44:50 PM
 */
public class BuildMapFromUi implements BiFunction<Container, Map, Map> {

    @Override
    public Map apply(Container ui, Map map) {
        final ComponentModel model = new ComponentModelImpl(
                SelectionValues.from(Collections.EMPTY_LIST), ComponentModel.ComponentProperties.DEFAULT, 100
        );
        return new MapFromUIBuilder()
                .componentModel(model)
                .filter(FromUIBuilder.Filter.ACCEPT_ALL)
                .formatter(FromUIBuilder.Formatter.NO_OP)
                .source(map)
                .ui(ui)
                .build();
    }
}
