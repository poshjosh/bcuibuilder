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

import com.bc.ui.ProgressCircleUI;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * @author Chinomso Bassey Ikwuagwu on May 20, 2019 11:10:23 AM
 */
public class CircularProgressTest {
  public JComponent makeUI() {
    JProgressBar progress = new JProgressBar();
    progress.setOpaque(false);
//    progress.setBackground(new Color(0, 0, 0, 0));
    // use JProgressBar#setUI(...) method
    progress.setUI(new ProgressCircleUI());
    progress.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    progress.setStringPainted(true);
    progress.setFont(progress.getFont().deriveFont(24f));
    progress.setForeground(Color.ORANGE);

    (new Timer(50, e -> {
      int iv = Math.min(100, progress.getValue() + 1);
      progress.setValue(iv);
    })).start();
//      progress.setValue(50);

    JPanel p = new JPanel();
    p.setOpaque(false);
//    p.setBackground(new Color(0, 0, 0, 0));
    p.add(progress);
    
    return p;
  }
  public static void main(String... args) {
    EventQueue.invokeLater(() -> {
      JFrame f = new JFrame();
//      f.setOpaque(false);
      f.setUndecorated(true);
      f.setBackground(new Color(0, 0, 0, 0));
      f.setType(Window.Type.UTILITY);
      f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      f.getContentPane().add(new CircularProgressTest().makeUI());
      f.setSize(320, 240);
      f.setLocationRelativeTo(null);
      f.setVisible(true);
    });
  }
}