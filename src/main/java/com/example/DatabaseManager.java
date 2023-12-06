package com.example;

import com.example.component.Column;
import com.example.component.Database;
import com.example.component.Row;
import com.example.component.Table;
import com.example.component.column.*;

public class DatabaseManager {
    private static DatabaseManager instance;
//    public static DBMS instanceCSW;

    private DatabaseManager(){
    }

    public static DatabaseManager getInstance(){
        if (instance == null){
            instance = new DatabaseManager();
//            instanceCSW = DBMS.getInstance();
        }
        return instance;
    }

    public static Database database;

    public void populateTable() {
        Table table = new Table("testTable" + database.tables.size());
        table.addColumn(new IntegerColumn("column1"));
        table.addColumn(new RealColumn("column2"));
        table.addColumn(new StringColumn("column3"));
        table.addColumn(new CharColumn("column4"));
        table.addColumn(new ColorColumn("column5"));
        table.addColumn(new ColorInvlColumn("column6", "AAAAAA", "FFFFFF"));
        Row row1 = new Row();
        row1.values.add("10");
        row1.values.add("10.0");
        row1.values.add("10");
        row1.values.add("1");
        row1.values.add("AAAAAA");
        row1.values.add("AAAAAA");
        table.addRow(row1);
        Row row2 = new Row();
        row2.values.add("15");
        row2.values.add("15.0");
        row2.values.add("15");
        row2.values.add("3");
        row2.values.add("AAAAAA");
        row2.values.add("AAAAAA");
        table.addRow(row2);
        database.addTable(table);
    }

    public void createDB(String name) {
        database = new Database(name);
    }

    public Boolean addTable(String name){
        if (name != null && !name.isEmpty()) {
            Table table = new Table(name);
            database.addTable(table);
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean deleteTable(int tableIndex){

        if (tableIndex != -1) {
            database.deleteTable(tableIndex);
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean addColumn(int tableIndex, String columnName, ColumnType columnType, String min, String max) {
        if (columnName != null && !columnName.isEmpty()) {
            if (tableIndex != -1) {

                switch (columnType) {
                    case INT -> {
                        Column columnInt = new IntegerColumn(columnName);
                        database.tables.get(tableIndex).addColumn(columnInt);
                    }
                    case REAL -> {
                        Column columnReal = new RealColumn(columnName);
                        database.tables.get(tableIndex).addColumn(columnReal);
                    }
                    case STRING -> {
                        Column columnStr = new StringColumn(columnName);
                        database.tables.get(tableIndex).addColumn(columnStr);
                    }
                    case CHAR -> {
                        Column columnChar = new CharColumn(columnName);
                        database.tables.get(tableIndex).addColumn(columnChar);
                    }
                    case COLOR -> {
                        Column timeColumn = new ColorColumn(columnName);
                        database.tables.get(tableIndex).addColumn(timeColumn);
                    }
                    case COLORINVL -> {
                        Column timeInvlColumn = new ColorInvlColumn(columnName,min,max);
                        database.tables.get(tableIndex).addColumn(timeInvlColumn);
                    }
                }
                for (Row row : database.tables.get(tableIndex).rows) {
                    row.values.add("");
                }
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }


    public Boolean deleteColumn(int tableIndex, int columnIndex/*, CustomTableModel tableModel*/){
        if (columnIndex != -1) {
            database.tables.get(tableIndex).deleteColumn(columnIndex);
            return true;
        } else {
            return false;
        }
    }

    public Boolean addRow(int tableIndex, com.example.component.Row row){
        if (tableIndex != -1) {
            for (int i = row.values.size(); i < database.tables.get(tableIndex).columns.size(); i++) {
                row.values.add("");
            }
            database.tables.get(tableIndex).addRow(row);
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean deleteRow(int tableIndex, int rowIndex/*, CustomTableModel tableModel*/){
        if (rowIndex != -1) {
            database.tables.get(tableIndex).deleteRow(rowIndex);
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean updateCellValue(String value, int tableIndex, int columnIndex, int rowIndex/*, CustomTable table*/){
        Table table = database.tables.get(tableIndex);
        com.example.component.Column column = table.columns.get(columnIndex);
        if (column.validate(value)){
            com.example.component.Row row = table.rows.get(rowIndex);
            row.setAt(columnIndex,value.trim());
            return true;
        }
        return false;
    }
    public boolean tablesIntersection(int tableIndex1, int tableIndex2){
        Table tempTable1 = new Table(database.tables.get(tableIndex1));
        Table tempTable2 = new Table(database.tables.get(tableIndex2));
        System.out.println(database.tables.get(tableIndex1).rows.get(0).values.size());
        System.out.println(tempTable1.rows.get(0).values.size());
        int i = 0;
        while (i<tempTable1.columns.size()){
            boolean flag = false;
            int j = i;
            while (j<tempTable2.columns.size()){
                if (tempTable1.columns.get(i).name.equals(tempTable2.columns.get(j).name) &&
                    tempTable1.columns.get(i).type.equals(tempTable2.columns.get(j).type)){
                    flag = true;
                    Column temp = tempTable2.columns.get(i);
                    tempTable2.columns.set(i,tempTable2.columns.get(j));
                    tempTable2.columns.set(j,temp);
                    for (Row row: tempTable2.rows) {
                        String data = row.getAt(i);
                        row.setAt(i,row.getAt(j));
                        row.setAt(j,data);
                    }
                    i++;
                    break;
                }
                else j++;
            }
            if (!flag){
                tempTable1.deleteColumn(i);
            }
        }
        i = 0;
        while (i<tempTable1.rows.size()){
            boolean flag = true;
            int j = 0;
            if (tempTable2.rows.size() == 0){
                flag = false;
            }
            while (j<tempTable2.rows.size()){
                flag = true;
                for (int k = 0; k < tempTable1.rows.get(i).values.size(); k++) {
                    if (!tempTable1.rows.get(i).getAt(k).equals(tempTable2.rows.get(j).getAt(k))){
                        flag = false;
                        break;
                    }
                }
                if (flag){
                    i++;
                    tempTable2.rows.remove(j);
                    break;
                }
                else j++;
            }
            if (!flag) tempTable1.rows.remove(i);
        }
        addTable(tempTable1.name + " ^ " + tempTable2.name);
        for (Column column: tempTable1.columns) {
            if (column.type.equals(ColumnType.COLORINVL.name())) {
                addColumn(database.tables.size() - 1, column.name, ColumnType.valueOf(column.type),
                    ((ColorInvlColumn) column).getMin(),((ColorInvlColumn) column).getMax());
            }
            else {
                addColumn(database.tables.size() - 1, column.name, ColumnType.valueOf(column.type),"","");
            }
        }
        for (Row row: tempTable1.rows) {
            addRow(database.tables.size()-1,row);
            System.out.println(row.values);
        }
        return true;
    }
}
