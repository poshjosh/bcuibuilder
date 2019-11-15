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

package com.bc.ui;

import com.bc.ui.functions.PositionCenterScreen;
import com.bc.ui.table.cell.ColumnWidths;
import com.bc.ui.table.cell.ColumnWidthsImpl;
import com.bc.ui.table.cell.TableCellDisplayFormat;
import com.bc.ui.table.cell.TableCellDisplayFormatImpl;
import com.bc.ui.table.cell.TableCellSize;
import com.bc.ui.table.cell.TableCellSizeImpl;
import com.bc.ui.table.cell.TableCellSizeManager;
import com.bc.ui.table.cell.TableCellSizeManagerImpl;
import com.bc.ui.table.cell.TableCellUIFactory;
import com.bc.ui.table.cell.TableCellUIFactoryImpl;
import com.bc.ui.table.cell.TableCellUIState;
import com.bc.ui.table.cell.TableCellUIStateImpl;
import com.bc.ui.table.cell.TableCellUIUpdater;
import com.bc.ui.table.cell.TableCellUIUpdaterImpl;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 19, 2018 1:36:33 PM
 */
public class UIContextImpl implements UIContext {
    
    private transient final Logger LOG = Logger.getLogger(UIContextImpl.class.getName());

    private final Window mainWindow;
    private final ImageIcon imageIcon;
    private final JFrame pbarFrame;
    private final ProgressbarPanel pbarPanel;
    
    private final Map<Class, Component> uiCache;

    public UIContextImpl() {
        this(null, null);
    }

    public UIContextImpl(Window mainWindow, ImageIcon imageIcon) {
        this(mainWindow, imageIcon, new JFrame());
    }
    
    public UIContextImpl(Window mainWindow, ImageIcon imageIcon, JFrame progressBarFrame) {
        this.mainWindow = mainWindow;
        this.imageIcon = imageIcon;
        
        this.pbarPanel = new ProgressbarPanelCircular(); 

        this.pbarFrame = progressBarFrame;
        if(this.pbarFrame != null) {
            this.pbarFrame.setUndecorated(true);
            this.pbarFrame.setBackground(this.pbarPanel.getProgressBar().getBackground());
            this.pbarFrame.setPreferredSize(this.pbarPanel.getPreferredSize());
            final boolean setAlwaysOnTopIsOk = false;
            this.pbarFrame.setAlwaysOnTop(setAlwaysOnTopIsOk);
            this.pbarFrame.setType(Window.Type.UTILITY);
            this.pbarFrame.getContentPane().add(pbarPanel);
            this.pbarFrame.pack();
        }
        
        this.uiCache = new HashMap<>();
    }

    @Override
    public void showErrorMessage(Throwable t, Object message, String title) {
        final String displayMsg = t == null ? String.valueOf(message) : 
                message + ". Caused by: " + t.getLocalizedMessage();
//        new ShowTillButtonClick(null, new JButton(" Ok "), false, true, false)
//                .accept(displayMsg, title);
        JOptionPane.showMessageDialog(this.getMainWindowOptional().orElse(null), 
                displayMsg, title, JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void showSuccessMessage(Object message, String title) {
//        new ShowTillButtonClick(null, new JButton(" Ok "), false, true, false)
//                .accept(message, title);
        JOptionPane.showMessageDialog(this.getMainWindowOptional().orElse(null), 
                message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public <T extends Component> T getUI(Class<T> type) {
        Component ui = uiCache.get(type);
        if(ui == null) {
            ui = this.newInstance(type);
            uiCache.put(type, ui);
        }
        return (T)ui;
    }
    
    public <T> T newInstance(Class<T> type) {
        try{
            return type.getConstructor().newInstance();
        }catch(NoSuchMethodException | SecurityException | InstantiationException | 
                IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    } 
    
    @Override
    public void dispose() {
        if(this.pbarFrame != null) {
            if(this.pbarFrame.isVisible()) {
                this.pbarFrame.setVisible(false);
            }
            this.pbarFrame.dispose();
        }
    }

    @Override
    public Optional<Window> getMainWindowOptional() {
        return Optional.ofNullable(this.mainWindow);
    }

    @Override
    public Optional<ImageIcon> getImageIconOptional() {
        return Optional.ofNullable(this.imageIcon);
    }

    @Override
    public void addProgressBarPercent(int val) {
        val = this.pbarPanel.getProgressBar().getValue() + val;
        this.showProgressBar(val == -1 ? null : ""+val+'%', 0, val, 100);
    }
    
    @Override
    public void addProgressBarPercent(String msg, int val) {
        val = this.pbarPanel.getProgressBar().getValue() + val;
        this.showProgressBar(msg, 0, val, 100);
    }
    
    @Override
    public void showProgressBarPercent(int val) {
        this.showProgressBar(val == -1 ? null : ""+val+'%', 0, val, 100);
    }

    @Override
    public void showProgressBarPercent(String msg, int val) {
        this.showProgressBar(msg, 0, val, 100);
    }
    
    @Override
    public void showProgressBar(String msg, int min, int val, int max) {
        
        if(SwingUtilities.isEventDispatchThread()) {
            
            showProgress(msg, min, val, max);
            
        }else{
            
            java.awt.EventQueue.invokeLater(() -> {
                showProgress(msg, min, val, max);
            });
        }
    }

    @Override
    public void showProgress(String msg, int min, int val, int max) {    
        
        final JProgressBar pbar = pbarPanel.getProgressBar();
        
        if(val >= max) {
            if(pbarFrame != null && pbarFrame.isVisible()) {
                pbarFrame.setVisible(false);
            }
        }else{
            
            final boolean indeterminate =  val < min;
            if(pbar.isIndeterminate() != indeterminate) {
                
                pbar.setIndeterminate(indeterminate);
                
                (new Timer(50, (ae) -> {
                    try{
                        final int iv = Math.min(100, pbar.getValue() + 1);
                        if(iv == 100) {
                            pbar.setValue(0);
                        }else{
                            pbar.setValue(iv);
                        }
                    }catch(RuntimeException ex) {
                        LOG.log(Level.WARNING, null, ex);
                    }
                })).start();
                
            }
            
            pbar.setStringPainted(msg != null);

            if(pbarFrame != null && !pbarFrame.isVisible()) {
                
                this.positionCenterScreen(pbarFrame);

                pbarFrame.setVisible(true);
            }
        }
        if(msg != null) {
            pbar.setString(msg);
        }
        if(min != pbar.getMinimum()) {
            pbar.setMinimum(min);
        }
        if(val != pbar.getValue()) {
            pbar.setValue(val);
        }
        if(max != pbar.getMaximum()) {
            pbar.setMaximum(max);
        }
    }

    @Override
    public JProgressBar getProgressBar() {
        return pbarPanel.getProgressBar();
    }
    
    @Override
    public boolean positionFullScreen(Component c) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width, screenSize.height - 50);
            c.setLocation(0, 0);
            c.setSize(custom);
            c.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }
    
    @Override
    public boolean positionHalfScreenLeft(Component c) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width/2, screenSize.height - 50);
            c.setLocation(0, 0);
            c.setSize(custom); 
            c.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }

    @Override
    public boolean positionHalfScreenRight(Component c) {
        try{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension custom = new Dimension(screenSize.width/2, screenSize.height - 50);
            c.setLocation(custom.width, 0);
            c.setSize(custom); 
            c.setPreferredSize(custom);
            return true;
        }catch(Exception ignored) { 
            return false;
        }
    }

    @Override
    public boolean positionCenterScreen(Component c) {
        return new PositionCenterScreen().apply(c);
    }

    @Override
    public void scrollTo(JTable table, int start, int end) {
        table.setRowSelectionInterval(start, end);
        final Rectangle rect = table.getCellRect(start, 0, true);
        final JScrollPane scrolls = this.getScrolls(table, null);
        if(scrolls != null) {
            scrolls.scrollRectToVisible(rect);
        }
        table.scrollRectToVisible(rect); 
    }
    
    @Override
    public JScrollPane getScrolls(JTable table, JScrollPane outputIfNone) {
        JScrollPane output = null;
        Container parent = table.getParent(); 
        while(true) {
            if(parent instanceof JScrollPane) {
                output = (JScrollPane)parent;
                break;
            }
            if(parent == null) {
                break;
            }
            parent = parent.getParent();
        }
        return output == null ? outputIfNone : output;
    }

//    @Override
    @Override
    public void updateTableUI(JTable table, Class entityType, int serialColumnIndex) {
        
        final TableCellUIUpdater tableUIUpdater = this.getTableCellUIUpdater();
        
        tableUIUpdater
                .cellUIFactory(this.getTableCellUIFactory(table.getModel(), entityType, serialColumnIndex))
                .update(table);
    }
    
//    @Override
    @Override
    public TableCellUIFactory getTableCellUIFactory(
            TableModel tableModel, Class entityType, int serialColumnIndex) {
        
        final TableCellUIState cellUIState = new TableCellUIStateImpl();
        
        final TableCellDisplayFormat cellDisplayFormat = 
                this.getTableCellDisplayFormat(serialColumnIndex);
        
        final TableCellSize cellSize = new TableCellSizeImpl(cellDisplayFormat, 0, Integer.MAX_VALUE); 
      
        final ColumnWidths columnWidths = this.getColumnWidths();
        
        final TableCellSizeManager cellSizeManager = new TableCellSizeManagerImpl(columnWidths);
        
        final TableCellUIFactory cellUIFactory = new TableCellUIFactoryImpl(
                cellUIState, cellSize, cellDisplayFormat, cellSizeManager
        );
        
        return cellUIFactory;
    }
    
//    @Override
    @Override
    public TableCellDisplayFormat getTableCellDisplayFormat(int serialColumnIndex) {
        final TableCellDisplayFormat cellDisplayValue = new TableCellDisplayFormatImpl();
        return cellDisplayValue;
    }

//    @Override
    @Override
    public TableCellUIUpdater getTableCellUIUpdater() {
        
        final TableCellUIUpdater cellUIUpdater = new TableCellUIUpdaterImpl();
        
        return cellUIUpdater;
    }

//as    @Override
    @Override
    public ColumnWidths getColumnWidths() {
        return new ColumnWidthsImpl();
    }
}
