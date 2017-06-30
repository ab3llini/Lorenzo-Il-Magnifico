package client.view.gui.lobby;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableCellEntry {

    private final SimpleStringProperty value;

    public TableCellEntry(String value) {
        this.value = new SimpleStringProperty(value);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getName(){
        return value.getName();
    }

    public void setName(String nName){
        this.value.set(nName);
    }

    public StringProperty nameProperty(){
        return value;
    }
}
