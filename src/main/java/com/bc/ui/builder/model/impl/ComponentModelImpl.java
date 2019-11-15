/*
 * Copyright 2017 NUROX Ltd.
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

package com.bc.ui.builder.model.impl;

import com.bc.selection.Selection;
import com.bc.selection.SelectionImpl;
import com.bc.selection.SelectionValues;
import com.bc.ui.JCheckBoxMenuItemListComboBox;
import com.bc.ui.builder.model.ComponentModel;
import com.bc.ui.date.DateFromUIBuilder;
import com.bc.ui.date.DateFromUIBuilderImpl;
import com.bc.ui.date.DatePanel;
import com.bc.ui.date.DateUIUpdater;
import com.bc.ui.date.DateUIUpdaterImpl;
import com.bc.ui.table.cell.TableCellComponentModelImpl;
import com.bc.ui.table.cell.TableCellTextArea;
import java.awt.Component;
import java.awt.Font;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 6, 2017 11:05:40 AM
 */
public class ComponentModelImpl extends TableCellComponentModelImpl implements ComponentModel {

    private transient static final Logger LOG = Logger.getLogger(ComponentModelImpl.class.getName());
    
    private final SelectionValues selectionValues;
    
    private final DateFromUIBuilder dateFromUIBuilder;
    
    private final DateUIUpdater dateUIUpdater;
    
    private final ComponentProperties componentProperties;

    private final int contentLengthAboveWhichTextAreaIsUsed;

    public ComponentModelImpl() {
        this(SelectionValues.from(Collections.EMPTY_LIST), ComponentModel.ComponentProperties.DEFAULT, 100);
    }
    
    public ComponentModelImpl(
            SelectionValues selectionValues, 
            ComponentProperties componentProperties, 
            int contentLengthAboveWhichTextAreaIsUsed) {
        this(selectionValues, new DateFromUIBuilderImpl(), new DateUIUpdaterImpl(), 
                componentProperties, contentLengthAboveWhichTextAreaIsUsed);
    }
    
    public ComponentModelImpl(SelectionValues selectionValues, 
            DateFromUIBuilder dateFromUIBuilder, DateUIUpdater dateUIUpdater) {
        this(selectionValues, dateFromUIBuilder, dateUIUpdater, 
                ComponentModel.ComponentProperties.DEFAULT, 50);
    }
    
    public ComponentModelImpl(SelectionValues selectionValues, 
            DateFromUIBuilder dateFromUIBuilder, DateUIUpdater dateUIUpdater, 
            ComponentProperties componentProperties, int contentLengthAboveWhichTextAreaIsUsed) {
        this.selectionValues = Objects.requireNonNull(selectionValues);
        this.dateFromUIBuilder = Objects.requireNonNull(dateFromUIBuilder);
        this.dateFromUIBuilder.defaultHousrs(0).defaultMinutes(0);
        this.dateUIUpdater = Objects.requireNonNull(dateUIUpdater);
        this.componentProperties = Objects.requireNonNull(componentProperties);
        this.contentLengthAboveWhichTextAreaIsUsed = contentLengthAboveWhichTextAreaIsUsed;
    }

    @Override
    public ComponentModel deriveNewFrom(ComponentProperties properties) {
        return new ComponentModelImpl(this.selectionValues, 
                this.dateFromUIBuilder, this.dateUIUpdater, properties, 
                this.contentLengthAboveWhichTextAreaIsUsed);
    }

    @Override
    public Component getComponent(Class parentType, Class valueType, String name, Object value) {
//System.out.println(valueType.getSimpleName()+' '+name+'='+value+". @"+this.getClass());                
        final Component component = this.doGetComponent(parentType, valueType, name, value);
        
        component.setName(name);
        
        this.setValue(component, value);
        
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "Name: {0}, value type: {1}, component type: {2}", 
                    new Object[]{name, valueType==null?null:valueType.getName(), component.getClass().getName()});
        }
        
        return component;
    }
    
    protected Component doGetComponent(Class parentType, Class valueType, String name, Object value) {
        
        LOG.finer(() -> "doGetComponent(..) For type: " + (parentType==null?null:parentType.getName()) 
                + ", " + (valueType==null?"NULL":valueType.getSimpleName()) + ' ' + name + '=' + value);                        

        final Component component;
        
        List<Selection> selectionList;
        
        if (valueType == Boolean.class || valueType == boolean.class) {
            
            component = this.getBooleanComponent(valueType, name, value);
            
        }else if(Date.class.isAssignableFrom(valueType)) {
            
            component = this.getDateComponent(valueType, name, value);
            
        }else if(Object[].class.isAssignableFrom(valueType)) {    
            
            final Collection collection = Arrays.asList((Object[])value);
            
            component = getSelectionComponent(parentType, valueType, name, collection);

        }else if(Collection.class.isAssignableFrom(valueType)) {    
            
            final Collection collection = (Collection)value;
            
            component = getSelectionComponent(parentType, valueType, name, collection);

        }else if( ! (selectionList = this.getSelectionValues(parentType, valueType, name, value)).isEmpty()) {
            
            component = this.getSelectionComponent(valueType, name, value, selectionList);
            
        }else if(this.isPasswordName(name)) {    
            
            component = this.getPasswordComponent(valueType, name, value);
            
        }else if(CharSequence.class.isAssignableFrom(valueType)) {    
            
            component = this.getTextComponent(valueType, name, value);
            
        }else{
            
            component = this.getFormattedTextComponent(valueType, name, value);
        }
        
        final ComponentProperties props = this.getComponentProperties();
        component.setFont(props.getFont(component));
        
        if(props.getWidth(component) > 0) {
            component.setPreferredSize(props.getDimension(component));
        }
        
        component.setEnabled(props.isEnabled(component));

        LOG.finer(() -> "doGetComponent(..) Component: " + component.getClass().getName()+ 
                ", For type: " + (parentType==null?null:parentType.getName()) + 
                ", " + (valueType==null?"NULL":valueType.getSimpleName()) + ' ' + name + '=' + value); 
        
        return component;
    }
    
    @Override
    public boolean isPasswordName(String name) {
        name = name.toLowerCase();
        return name.contains("password") || name.equals("pass");
    }

    @Override
    public List<Selection> getSelectionValues(Class parentType, Class valueType, String name, Object value) {
//System.out.println("getSelectionValues(..) "+valueType.getSimpleName()+' '+name+'='+value+". @"+this.getClass());                        
        final List<Selection> values = selectionValues.getSelectionValues(valueType);
        return values;
    }
    
    @Override
    protected Object doGetValue(Component component, Object outputIfNone) {
        
        Objects.requireNonNull(component);
        
        Object value;
        
        if(component instanceof DatePanel) {
            value = dateFromUIBuilder.ui(component).build(null);
        }else if(component instanceof JComboBox) {
            final Object [] selected = ((JComboBox)component).getSelectedObjects();
            value = selected == null ? null : this.getValue(Arrays.asList(selected));
        }else if(component instanceof JList) {
            final List selected = ((JList)component).getSelectedValuesList();
            value = this.getValue(selected);
        }else if(component instanceof JCheckBoxMenuItemListComboBox) {
            final JCheckBoxMenuItemListComboBox comboBox = (JCheckBoxMenuItemListComboBox)component;
            final List selected = comboBox.getSelectedValuesList();
            value = this.getValue(selected);
        }else{
            
            value = super.doGetValue(component, outputIfNone);
        }
        
        return value;
    }
    
    private Object getValue(List selected) {
        final Object value;
        if(selected == null) {
            value = null;
        }else if(selected.size() == 1) {
            value = this.toActualValue(selected.get(0));
        }else{
            value = this.toActualValues(selected);
        }
        return value;
    }
    
    public List toActualValues(List selected) {
        final List output;
        if(selected == null || selected.isEmpty()) {
            output = Collections.EMPTY_LIST;
        }else{
            output= new ArrayList(selected.size());
            for(Object sel : selected) {
                final Object actual = this.toActualValue(sel);
                output.add(actual);
            }
        }
        return output;
    }
    
    public Object toActualValue(Object selected) {
        final Object actual;
        if(selected instanceof Selection) {
            actual = ((Selection)selected).getValue();
        }else{
            actual = selected;
        }
        return actual;
    }
    
    @Override
    public Object setValue(Component component, Object value) {
        
        if(component instanceof JScrollPane) {
            component = ((JScrollPane)component).getViewport().getView();
        }
        
        Objects.requireNonNull(component);
        
        value = this.format(value);
        
        if(component instanceof DatePanel) {
            final DatePanel dateTimePanel = (DatePanel)component;
            final Calendar cal = Calendar.getInstance();
            if(value != null) {
                Date date = (Date)value;
                cal.setTime(date);
                this.dateUIUpdater.update(dateTimePanel, cal);
            }else{
                this.dateUIUpdater.updateMonth(dateTimePanel.getMonthCombobox(), cal);
                this.dateUIUpdater.updateYear(dateTimePanel.getYearCombobox(), cal);
            }
        }else if(component instanceof JComboBox) {
            ((JComboBox)component).setSelectedItem(new SelectionImpl(component.getName(), value));
        }else if(component instanceof JList) {
            ((JList)component).setSelectedValue(new SelectionImpl(component.getName(), value), true);
        }else if(component instanceof JCheckBoxMenuItemListComboBox) {
            final JCheckBoxMenuItemListComboBox jx = (JCheckBoxMenuItemListComboBox)component;
            jx.setSelectedValue(new SelectionImpl(component.getName(), value));
        }else{
            value = super.setValue(component, value);
        }
        
        return value;
    }
    
    public Component getBooleanComponent(Class valueType, String name, Object value) {
        final JCheckBox component = new JCheckBox();
        return component;
    }
    
    public Component getDateComponent(Class valueType, String name, Object value) {
        final JPanel panel = new JPanel();
        final Font font = this.componentProperties.getFont(panel);
        final int height = this.componentProperties.getHeight(panel);
        final DatePanel dtp = new DatePanel(font, height, 78, height, 4);
        for(Component c : dtp.getComponents()) {
            if(c instanceof JTextField) {
                final JTextField tf = ((JTextField)c);
                tf.setEditable(this.componentProperties.isEditable(c));
//System.out.println("Editable: "+tf.isEditable()+", component: "+tf.getClass().getName()+". @"+this.getClass());                                        
            }
        }
        return dtp;
    }
    
    public Component getSelectionComponent(Class parentType, Class valueType, String name, Collection c) {
        
        final Selection noSelection = new SelectionImpl("Select " + name, null);

        List<Selection> selectionList = SelectionValues.from(noSelection, c).getSelectionValues("");

        return this.getSelectionComponent(valueType, name, c, selectionList);
    }

    public Component getSelectionComponent(Class valueType, 
            String name, Object value, List<Selection> selectionList) {
        
        LOG.finer(() -> MessageFormat.format("{0} {1} = {2};\nSelection values: {3}", 
                (valueType==null?"NULL":valueType.getSimpleName()), name, value, selectionList));
        
        final JComboBox component = new JComboBox(selectionList.toArray(new Selection[0]));
        
        return component;
    }
    
    public Component getPasswordComponent(Class valueType, String name, Object value) {
        final JPasswordField component = new JPasswordField();
        return component;
    }
    
    public Component getFormattedTextComponent(Class valueType, String name, Object value) {
        
        final JTextComponent component = new JFormattedTextField();
        
        component.setEditable(this.componentProperties.isEditable(component));
//System.out.println("Editable: "+component.isEditable()+", component: "+component.getClass().getName()+". @"+this.getClass());                                        
        return component;
    }
    
    public Component getTextComponent(Class valueType, String name, Object value) {
        final JTextComponent component;
        if(this.getDisplaySize(valueType, name, value) <= this.contentLengthAboveWhichTextAreaIsUsed) {
            component = this.getTextField(valueType, name, value);
        }else{
            component = this.getTextArea(valueType, name, value);
        }
        
        component.setEditable(this.componentProperties.isEditable(component));
//System.out.println("Editable: "+component.isEditable()+", component: "+component.getClass().getName()+". @"+this.getClass());                                        
        return component;
    }

    public JTextField getTextField(Class valueType, String name, Object value) {
        final JTextField component = new JTextField();
        return component;
    }
    
    public JTextArea getTextArea(Class valueType, String name, Object value) {
        
        final JTextArea component = new TableCellTextArea();
        
        return component;
    }
    
    public int getDisplaySize(Class valueType, String name, Object value) {
        //@todo use app.getJpaContext() to compute display sizes
        // NOTE: app may be null
        return value == null ? -1 : value.toString().length();
    }
    
    public SelectionValues getSelectionValues() {
        return selectionValues;
    }

    public DateFromUIBuilder getDateFromUIBuilder() {
        return dateFromUIBuilder;
    }

    public DateUIUpdater getDateUIUpdater() {
        return dateUIUpdater;
    }

    @Override
    public ComponentProperties getComponentProperties() {
        return componentProperties;
    }
    
    public int getContentLengthAboveWhichTextAreaIsUsed() {
        return contentLengthAboveWhichTextAreaIsUsed;
    }
}
