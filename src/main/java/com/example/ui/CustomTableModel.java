package com.example.ui;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class CustomTableModel extends DefaultTableModel {

    public CustomTableModel() {
        super();
    }

    public CustomTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }
    
    public void removeColumn(int column) {
        for (int i = 0; i < getRowCount(); i++) {
            Vector rowData = (Vector) dataVector.elementAt(i);
            rowData.removeElementAt(column);
        }
        
        columnIdentifiers.removeElementAt(column);
        
        fireTableStructureChanged();
    }
}