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

package com.bc.ui.builder.wip;

import com.bc.ui.functions.PositionCenterScreen;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 21, 2018 10:50:41 PM
 */
public class MessageUi implements BiFunction<Object, String, MessageUi>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(MessageUi.class.getName());

    private JFrame frame;
    
    private final AbstractButton north;
    
    private final AbstractButton south;

    private final boolean alwaysOnTop;
    
    private final boolean resizable; 
            
    private final boolean undecorated;

    private final AtomicBoolean done;
        
    public MessageUi() {
        this(" Ok ", " Ok ");
    }

    public MessageUi(String northBtnText, String southBtnText) {
        this(northBtnText == null ? null : new JButton(northBtnText),
                southBtnText == null ? null : new JButton(southBtnText),
                true, true, false);
    }
    
    public MessageUi(AbstractButton north, AbstractButton south, 
            boolean alwaysOnTop, boolean resizable, boolean undecorated) {
        this.north = north;
        this.south = south;
        if(north == null && south == null) {
            throw new NullPointerException();
        }
        this.alwaysOnTop = alwaysOnTop;
        this.resizable = resizable;
        this.undecorated = undecorated;
        this.done = new AtomicBoolean(false);
    }
    
    @Override
    public MessageUi apply(Object message, String title) {
        
        Objects.requireNonNull(message);
        
        LOG.fine(() -> "Preparing to display message with title: " + title);

        if(java.awt.EventQueue.isDispatchThread()) {
            show(message, title);
        }else{
            java.awt.EventQueue.invokeLater(() -> {
                show(message, title);
            });
        }
        
        return this;
    }
    
    private void show(Object message, String title) {
        try{

            if(frame != null) {
                frame.dispose();
            }

            frame = title == null ? new JFrame() : new JFrame(title);
            frame.setResizable(resizable);
            frame.setUndecorated(undecorated);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setAlwaysOnTop(alwaysOnTop);
            final Container contentPane = frame.getContentPane();
            contentPane.setLayout(new BorderLayout());

            if(north != null) {
                contentPane.add(north, BorderLayout.NORTH);
            }
            
            final Container container = this.getContainer(message);
            
            final JScrollPane scrolls = new JScrollPane(container);

            contentPane.add(scrolls, BorderLayout.CENTER);

            if(south != null) {
                contentPane.add(south, BorderLayout.SOUTH);
            }

            final ActionListener listener = (ae) -> {
                frame.setVisible(false);
                done.set(true);
            };

            if(north != null) north.addActionListener(listener);
            if(south != null) south.addActionListener(listener);

            frame.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosing(WindowEvent e) {
                    done.set(true);
                }
            });

            new PositionCenterScreen().apply(frame);

            frame.pack();

            LOG.fine(() -> "Displaying message with title: " + title);

            frame.setVisible(true);

        }catch(RuntimeException e) {
            if(frame != null) {
                frame.dispose();
            }
            LOG.log(Level.WARNING, null, e);
        }
    }
    
    public Container getContainer(Object message) {
        if(message instanceof Container) {
            return (Container)message;
        }else{
            return new JLabel(message.toString());
        }
    }
    
    public synchronized void waitTillDone() {
        try{
            while(!done.get()) {
                this.wait(500);
            }
        }catch(RuntimeException | InterruptedException e) {
            LOG.log(Level.WARNING, null, e);
        }finally{
            this.notifyAll();
        }
    }
}
