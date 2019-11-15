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

package com.bc.ui;

import java.awt.Color;
import javax.swing.JProgressBar;

/**
 * @author Chinomso Bassey Ikwuagwu on May 20, 2019 10:47:20 AM
 */
public class ProgressbarPanelCircular extends ProgressbarPanel {

    /**
     * Creates new form ProgressbarPanel
     */
    public ProgressbarPanelCircular() {
        
        final java.awt.Color color = new java.awt.Color(0, 0, 0, 0);
        
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setOpaque(false);
        
        final java.awt.Dimension dim = new java.awt.Dimension(160, 145);
        setMaximumSize(dim);
        setMinimumSize(dim);
        setPreferredSize(dim);

        final JProgressBar progressBar = getProgressBar();
        progressBar.setUI(new ProgressCircleUI());
        progressBar.setOpaque(false);
        progressBar.setBackground(color);
        progressBar.setForeground(Color.ORANGE);
        progressBar.setMaximumSize(dim);
        progressBar.setMinimumSize(dim);
        progressBar.setPreferredSize(dim);
    }
}
