# bcuibuilder
### Java library for building FORM like user interfaces and extracting FORM data from those user interfaces

## Usage

```java
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
```

### These are the helper methods

```java    
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
```

### Here is an extract from the Person model 

```java
    public class Person implements Serializable {

        private Boolean alive;

        private String firstName;

        private String otherName;

        private String surName;

        private Date dateOfBirth;

        private Person parent;

        private List<Person> children;

        public Person() { }

        // Getters and setters
    }
```

### Here is the Form we built

![Form UI built from map entries](https://github.com/poshjosh/bcuibuilder/blob/master/src/test/resources/META-INF/form_built_from_map_entries.png)        

## Let's now understand what was happening

For our examples we use a Person model. Each person may have one parent or many children.
The root of this family, in this case, is the father.

```java
        System.out.println("Creating family");
        final Person father = createPerson(null, "Big", "Daddy");
        final Person son = createPerson(father, "Daddy Jnr", father.getSurName());
        final Person daughter = createPerson(father, "Little Mary", father.getSurName());
        father.setChildren(Arrays.asList(son, daughter));
```

We use our Person model to build a Map using a MapBuilder.
MapBuilder uses Person fields as Map keys.
```java
        System.out.println("Building family map");
        final MapBuilder mapBuilder = new MapBuilderImpl();

        final Map sourceData = mapBuilder
                .source(father)
                .nullsAllowed(false)
                .build();
        
        sourceData.put("children", father.getChildren().stream().map((p) -> p.getFirstName()).toArray());

        System.out.println("Printing source data: " + sourceData);
        System.out.println(new com.bc.util.JsonFormat(true, true, "  ").toJSONString(sourceData));
```

We build a Form for displaying the Person model data from the entries of the Map we created earlier.

```java
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
```

We display the Form 
```java
        final JScrollPane scrolls = new JScrollPane(ui);

        JOptionPane.showMessageDialog(null, scrolls, "Please Edit Me", JOptionPane.PLAIN_MESSAGE);
```

### Now its time to extract data from the Form

```java

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
```
