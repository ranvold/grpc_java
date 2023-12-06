package com.example.component.column;


import com.example.component.Column;

public class StringColumn extends Column {

    public StringColumn(String name){
        super(name);
        this.type = ColumnType.STRING.name();
    }
    @Override
    public boolean validate(String data) {
        return true;
    }
}
