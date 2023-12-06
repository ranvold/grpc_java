package com.example.ui;



import com.example.component.Row;
import org.example.EditCellRequest;
import org.example.EditCellResponse;
import org.example.GetColumnsResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.RemoteException;
import java.util.List;

public class CustomTable extends JTable {

    public CustomTable(DefaultTableModel tableModel) {
        super(tableModel);
        addPropertyChangeListener(evt -> {
            if ("tableCellEditor".equals(evt.getPropertyName())) {
                if (!isEditing()){
                    try {
                        processEditingStopped();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void processEditingStopped() throws RemoteException {
        int editingRow = getEditingRow();
        int editingColumn = getEditingColumn();

        if (editingRow != -1 && editingColumn != -1) {
            Object newValue = getValueAt(editingRow, editingColumn);
            int selectedTab = DBMS.tabbedPane.getSelectedIndex();
            EditCellResponse editCellResponse = DBMS.blockingStub.editCell(EditCellRequest.newBuilder()
                    .setTableIndex(selectedTab).setRowIndex(editingRow)
                    .setColumnIndex(editingColumn).setValue((String) newValue).build());
            boolean result = editCellResponse.getSuccess();
            if(!result){
                List<Row> rows = DBMS.getRows(selectedTab);
                String data = rows.get(editingRow).getAt(editingColumn);
                if (data != null){
                    this.setValueAt(data, editingRow, editingColumn);
                }
                else {
                    this.setValueAt("", editingRow, editingColumn);
                }
            }
        }

    }
}