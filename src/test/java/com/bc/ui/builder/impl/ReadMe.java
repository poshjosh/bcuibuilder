package com.bc.ui.builder.impl;

import com.bc.objectgraph.MapBuilder;
import com.bc.objectgraph.MapBuilderImpl;
import com.bc.selection.SelectionContext;
import com.bc.typeprovider.MemberTypeProvider;
import com.bc.typeprovider.TypeProviders;
import com.bc.ui.builder.FromUIBuilder;
import com.bc.ui.builder.UIBuilder;
import java.awt.Component;
import java.awt.Container;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author USER
 */
public class ReadMe {
    
    public static void main(String... args) {
        new ReadMe().run();
    }
    
    public void run() {
            
        System.out.println("Creating family");
        final Person father = createPerson(null, "Big", "Daddy");
        final Person son = createPerson(father, "Daddy Jnr", father.getSurName());
        final Person daughter = createPerson(father, "Little Mary", father.getSurName());
        father.setChildren(Arrays.asList(son, daughter));

        System.out.println("Building family map");
        final MapBuilder mapBuilder = new MapBuilderImpl();

        final Map sourceData = mapBuilder
                .source(father)
                .nullsAllowed(false)
                .build();
        
        sourceData.put("children", father.getChildren().stream().map((p) -> p.getFirstName()).toArray());

        System.out.println("Printing source data: " + sourceData);
        System.out.println(new com.bc.util.JsonFormat(true, true, "  ").toJSONString(sourceData));

        final MemberTypeProvider typeProvider =  TypeProviders.fromValueType();
        
        final BiConsumer<Container, Collection<Component>> composer = new SimpleUIComposer(); 
        
        // Create a UIBuilder for building the UI from Map entries
        //
        final UIBuilder<Map, Container> uiBuilder = new UIBuilderFromMapImpl();
               
        final Container ui = uiBuilder
                .composer(composer)
                .typeProvider(typeProvider)
                .selectionContext(SelectionContext.NO_OP)
                .sourceData(sourceData)
                .build();

        final JScrollPane scrolls = new JScrollPane(ui);

        JOptionPane.showMessageDialog(null, scrolls, "Please Edit Me", JOptionPane.PLAIN_MESSAGE);
        
        // At this point the user edits form values. Then we need to extract the updated values
        
        final int count = ui.getComponentCount();
        for(int i=0; i<count; i++) {
            final Component c = ui.getComponent(i);
            if(c instanceof JTextComponent) {
//                System.out.println(c.getName() + '=' + ((JTextComponent)c).getText());
            }
        }

        // Create a FromUIBuilder instance for extracting form data from the UI  
        //
        final FromUIBuilder<Container, Map> mapFromUiBuilder = new MapFromUIBuilder();
                
        final Map targetData = mapFromUiBuilder
                .filter(FromUIBuilder.Filter.ACCEPT_ALL)
                .formatter(FromUIBuilder.Formatter.NO_OP)
                .source(sourceData)
                .ui(ui)
                .build();

        System.out.println("Printing target data: " + targetData);
        System.out.println(new com.bc.util.JsonFormat(true, true, "  ").toJSONString(targetData));

// Here is the form we built        
//![Form UI built from map entries](https://github.com/poshjosh/bcuibuilder/blob/master/src/test/resources/META-INF/form_built_from_map_entries.png)        
    }
    
    public Person createPerson(Person parent, String firstname, String surname) {
        final Person person = new Person();
        person.setAlive(true);
//        person.setChildren(Collections.EMPTY_LIST);
        person.setDateOfBirth(getRandomDateOfBirth(parent));
        person.setFirstName(firstname);
//        person.setOtherName(???);
//        person.setParent(???);
        person.setSurName(surname);
        return person;
    }
    
    public Date getRandomDateOfBirth(Person parent) {
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
        return dateOfBirth.getTime();
    }
    
    private void validate(Person person, UIBuilderImpl<Map> uiBuilder, Container ui) {
        
        Boolean isParent = uiBuilder.isParent(
                ui, person.getClass(), "firstName", person.getFirstName(), person.getFirstName().getClass());
        assertTrue(isParent);

        isParent = uiBuilder.isParent(
                ui, person.getClass(), "invalidField", "invalidFieldValue", String.class);
        assertFalse(isParent);
    }
}
