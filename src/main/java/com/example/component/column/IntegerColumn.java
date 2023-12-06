package com.example.component.column;

import com.example.component.Column;

public class IntegerColumn extends Column {

    public IntegerColumn(String name){
        super(name);
        this.type = ColumnType.INT.name();
    }

    @Override
    public boolean validate(String data) {
        if (data == null || data.isEmpty()){
            return true;
        }
        try {
            Integer.parseInt(data);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
