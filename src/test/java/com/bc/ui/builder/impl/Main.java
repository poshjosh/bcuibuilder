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

package com.bc.ui.builder.impl;

import com.bc.ui.UIContextImpl;
import com.bc.util.JsonFormat;
import com.bc.objectgraph.MapBuilder;
import com.bc.objectgraph.MapBuilderImpl;
import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.LogManager;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

/**
 * @author Chinomso Bassey Ikwuagwu on May 10, 2018 11:44:19 AM
 */
public class Main {

    public static void main(String [] args) {
        
        final UIContextImpl uic = new UIContextImpl();
        try{

    //        uic.showProgressBar("10%", 0, 10, 100);
            uic.showProgressBarPercent(-1);

            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try(InputStream in = classLoader.getResourceAsStream("META-INF/logging.properties")) {

                LogManager.getLogManager().readConfiguration(in);

            }catch(IOException e) {
                e.printStackTrace();
            }
        

//        try{
//        for(int i=0; i < 7; i++) {
//            Thread.sleep(1000);
//            uic.showProgressBar((i * 20) + "%", 0, i * 20, 100);
//        } 
//        }catch(Throwable t) {
//            t.printStackTrace();
//        }
        System.out.println("Creating family");
        final Person father = createPerson(null, "James", "Akuno");
        final Class sourceType = father.getClass();
        final Person son = createPerson(father, "John", father.getSurName());
        final Person daughter = createPerson(father, "Mary", father.getSurName());
        father.setChildren(Arrays.asList(son, daughter));
        
        
        System.out.println("Building family map");
        final MapBuilder mapBuilder = new MapBuilderImpl()
                .recursionFilter((type, instance) -> false)
                .filter(MapBuilder.Filter.ACCEPT_ALL)
//                .sourceType(sourceType)
                .source(father)
                .nullsAllowed(false)
//                .maxDepth(2)
//                .typesToIgnore(???)
                .maxCollectionSize(10);
        
        final Map sourceData = mapBuilder.build();
        sourceData.put("children", father.getChildren().stream().map((p) -> p.getFirstName()).toArray());
        
        System.out.println("Printing source data");
        System.out.println(new JsonFormat(true, true, "  ").toJSONString(sourceData));

        final Container ui = new BuildUiFromMap().apply(sourceData);

        final JScrollPane scrolls = new JScrollPane(ui);

//        JOptionPane.showMessageDialog(null, scrolls, "Please Edit Me", JOptionPane.PLAIN_MESSAGE);
        
        uic.showSuccessMessage(scrolls, "Please Edit Me");
        
        final int count = ui.getComponentCount();
        for(int i=0; i<count; i++) {
            final Component c = ui.getComponent(i);
            if(c instanceof JTextComponent) {
//                System.out.println(c.getName() + '=' + ((JTextComponent)c).getText());
            }
        }
        
        final Map targetData = new BuildMapFromUi().apply(ui, sourceData);
        
        System.out.println("Printing target data");
        System.out.println(new JsonFormat(true, true, "  ").toJSONString(targetData));
//        Boolean isParent = uiBuilder.isParent(
//                ui, sourceType, "firstName", person.getFirstName(), person.getFirstName().getClass());
//        assertTrue(isParent);

//        isParent = uiBuilder.isParent(
//                ui, sourceType, "invalidField", "invalidFieldValue", String.class);
//        assertFalse(isParent);
        }catch(Throwable t) {
            t.printStackTrace();
        }finally{
            uic.showProgressBarPercent(100);
            System.exit(0);
        }
    }
    
    private static Person createPerson(Person parent, String firstname, String surname) {
        final ThreadLocalRandom rdm = ThreadLocalRandom.current();
        final Person person = new Person();
        person.setAlive(true);
//        person.setChildren(Collections.EMPTY_LIST);
        final Calendar dateOfBirth = Calendar.getInstance();
        final int year;
        if(parent == null || parent.getDateOfBirth() == null) {
            year = 1970  + rdm.nextInt(10);
        }else{
            final Calendar temp = Calendar.getInstance();
            temp.setTime(parent.getDateOfBirth());
            year = temp.get(Calendar.YEAR) + rdm.nextInt(30);
        }
        dateOfBirth.set(year, rdm.nextInt(1, 11), rdm.nextInt(1, 28), 0, 0, 0);
        person.setDateOfBirth(dateOfBirth.getTime());
        person.setFirstName(firstname);
//        person.setOtherName(???);
//        person.setParent(???);
        person.setSurName(surname);
        return person;
    }
}
