package edu.farmingdale.recipegenerator;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents an item in the fridge or a recipe ingredient.
 * The item contains properties such as name, quantity, and weight.
 * This class uses JavaFX properties to allow for easy binding in the UI.
 */
public class Item {
    private StringProperty name;
    private IntegerProperty quantity;
    private StringProperty weight;

    /**
     * Constructs an Item with a name, quantity, and weight.
     *
     * @param name     the name of the item
     * @param quantity the quantity of the item
     * @param weight   the weight of the item (can be null)
     */
    public Item(String name, int quantity, String weight) {
        this.name = new SimpleStringProperty(name);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.weight = new SimpleStringProperty(weight);
    }

    /**
     * Constructs an Item with a name and quantity, leaving weight as null.
     *
     * @param name     the name of the item
     * @param quantity the quantity of the item
     */
    public Item(String name, int quantity) {
        this(name, quantity, null);
    }

    /**
     * Default constructor for an empty Item.
     */
    public Item() {
        this("", 0, null);
    }


    /**
     * Gets the name of the item.
     *
     * @return the name of the item
     */
    public String getName() {
        return name.get();
    }

    /**
     * Sets the name of the item.
     *
     * @param name the new name of the item
     */
    public void setName(String name) {
        this.name.set(name);
    }


    /**
     * Property for the name of the item, allowing for easy UI binding.
     *
     * @return the name property
     */
    public StringProperty nameProperty() {
        return name;
    }


    /**
     * Gets the quantity of the item.
     *
     * @return the quantity of the item
     */
    public int getQuantity() {
        return quantity.get();
    }

    /**
     * Sets the quantity of the item.
     *
     * @param quantity the new quantity of the item
     */
    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    /**
     * Property for the quantity of the item, allowing for easy UI binding.
     *
     * @return the quantity property
     */
    public IntegerProperty quantityProperty() {
        return quantity;
    }

    /**
     * Gets the weight of the item.
     *
     * @return the weight of the item, or null if not specified
     */
    public String getWeight() {
        return weight.get();
    }
    /**
     * Sets the weight of the item.
     *
     * @param weight the new weight of the item
     */
    public void setWeight(String weight) {
        this.weight.set(weight);
    }

    /**
     * Property for the weight of the item, allowing for easy UI binding.
     *
     * @return the weight property
     */
    public StringProperty weightProperty() {
        return weight;
    }

}
