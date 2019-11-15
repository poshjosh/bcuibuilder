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

import com.bc.ui.table.cell.ColumnWidths;
import com.bc.ui.table.cell.TableCellDisplayFormat;
import com.bc.ui.table.cell.TableCellUIFactory;
import com.bc.ui.table.cell.TableCellUIUpdater;
import java.awt.Component;
import java.awt.Window;
import java.util.Optional;
import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on May 26, 2018 7:14:27 PM
 */
public interface UIContext extends MessagePrompt {
    
    Optional<Window> getMainWindowOptional();

    Optional<ImageIcon> getImageIconOptional();
    
    <T extends Component> T getUI(Class<T> type);
    
    void addProgressBarPercent(int val);

    void addProgressBarPercent(String msg, int val);

    void dispose();

    //as    @Override
    ColumnWidths getColumnWidths();

    JProgressBar getProgressBar();

    JScrollPane getScrolls(JTable table, JScrollPane outputIfNone);

    //    @Override
    TableCellDisplayFormat getTableCellDisplayFormat(int serialColumnIndex);

    //    @Override
    TableCellUIFactory getTableCellUIFactory(TableModel tableModel, Class entityType, int serialColumnIndex);

    //    @Override
    TableCellUIUpdater getTableCellUIUpdater();

    boolean positionCenterScreen(Component c);

    boolean positionFullScreen(Component c);

    boolean positionHalfScreenLeft(Component c);

    boolean positionHalfScreenRight(Component c);

    void scrollTo(JTable table, int start, int end);

    void showProgress(String msg, int min, int val, int max);

    void showProgressBar(String msg, int min, int val, int max);

    void showProgressBarPercent(int val);

    void showProgressBarPercent(String msg, int val);

    //    @Override
    void updateTableUI(JTable table, Class entityType, int serialColumnIndex);

}
