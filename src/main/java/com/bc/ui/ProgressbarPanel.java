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
 * @author Chinomso Bassey Ikwuagwu on May 20, 2019 12:23:12 PM
 */
public class ProgressbarPanel extends javax.swing.JPanel {

    private final javax.swing.JProgressBar progressBar;

    /**
     * Creates new form ProgressbarPanel
     */
    public ProgressbarPanel() {

        progressBar = new javax.swing.JProgressBar();

        setFont(new java.awt.Font("Monospaced", 0, 24)); // NOI18N
        progressBar.setFont(new java.awt.Font("Monospaced", 0, 24)); // NOI18N
        progressBar.setBorderPainted(false);
        progressBar.setDoubleBuffered(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }


    public JProgressBar getProgressBar() {
        return progressBar;
    }
}

