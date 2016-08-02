package com.android.hiparker.lierda_light.dao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class ExampleDaoGenerator {

	public static void main(String[] args) throws Exception {  
        Schema schema = new Schema(1, "src.com.android.hiparker.lierda_light");  
  
//        addNote(schema);  
//        addCustomerOrder(schema);
        addGroup(schema);
        addLight(schema);
  
        new DaoGenerator().generateAll(schema, "./");  
		
    }
	
	private static void addLight(Schema schema) {
		Entity note = schema.addEntity("Light");   
        note.addStringProperty("address").primaryKey();  
        note.addStringProperty("name");  
        note.addIntProperty("color");
        note.addIntProperty("value1");
        note.addIntProperty("value2");
        note.addIntProperty("value3");
        note.addIntProperty("value4");
	}

    private static void addGroup(Schema schema) {  
        Entity note = schema.addEntity("Groups");  
        note.addIdProperty();  
        note.addStringProperty("name").notNull();  
        note.addStringProperty("lights");   
        note.addIntProperty("value1");
        note.addIntProperty("value2");
        note.addIntProperty("value3");
        note.addIntProperty("value4");
    }  
    
    private static void addNote(Schema schema) {  
        Entity note = schema.addEntity("Note");  
        note.addIdProperty();  
        note.addStringProperty("text").notNull();  
        note.addStringProperty("comment");  
        note.addDateProperty("date");  
    }  
  
    private static void addCustomerOrder(Schema schema) {  
        Entity customer = schema.addEntity("Customer");  
        customer.addIdProperty();  
        customer.addStringProperty("name").notNull();  
  
        Entity order = schema.addEntity("Order");  
        order.setTableName("ORDERS"); // "ORDER" is a reserved keyword  
        order.addIdProperty();  
        Property orderDate = order.addDateProperty("date").getProperty();  
        Property customerId = order.addLongProperty("customerId").notNull().getProperty();  
        order.addToOne(customer, customerId);  
  
        ToMany customerToOrders = customer.addToMany(order, customerId);  
        customerToOrders.setName("orders");  
        customerToOrders.orderAsc(orderDate);  
    }  
}
