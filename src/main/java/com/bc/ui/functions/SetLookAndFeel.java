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

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2018 3:04:20 PM
 */
public class SetLookAndFeel implements Serializable, 
        Function<String, Optional<javax.swing.UIManager.LookAndFeelInfo>> {

    private transient static final Logger LOG = Logger.getLogger(SetLookAndFeel.class.getName());

    @Override
    public Optional<javax.swing.UIManager.LookAndFeelInfo> apply(String name) {
    
        javax.swing.UIManager.LookAndFeelInfo output = null;
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (name.equalsIgnoreCase(info.getName())) {
                    
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    
                    LOG.log(Level.INFO, "Setting look and feel to: {0}", info.getClassName());
                    
                    output = info;
                    
                    break;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Exception setting look and feel to: " + name, e);
        }
        
        return Optional.ofNullable(output);
    }
}
