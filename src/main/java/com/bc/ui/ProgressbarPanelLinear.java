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
 * @author Chinomso Bassey Ikwuagwu on May 20, 2019 12:25:46 PM
 */
public class ProgressbarPanelLinear extends ProgressbarPanel{

    public ProgressbarPanelLinear() {

        final java.awt.Color color = new java.awt.Color(0, 0, 102);
        
        this.setBorder(javax.swing.BorderFactory.createLineBorder(color, 2));

        final java.awt.Dimension dim= new java.awt.Dimension(512, 32);
        final java.awt.Dimension dimMin = new java.awt.Dimension(256, 32);
        setMaximumSize(dim);
        setMinimumSize(dimMin);
        setPreferredSize(dim);

        final JProgressBar progressBar = getProgressBar();
        progressBar.setBackground(Color.WHITE);
        progressBar.setForeground(color);
        progressBar.setMaximumSize(dim);
        progressBar.setMinimumSize(dimMin);
        progressBar.setPreferredSize(dim);
    }
}
