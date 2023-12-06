package com.example.component.column;


import com.example.component.Column;

public class ColorColumn extends Column {

    public ColorColumn(String name) {
        super(name);
        this.type =  ColumnType.COLOR.name();
    }

    @Override
    public boolean validate(String data) {
        return data.matches("[0-9a-fA-F]{6}");
    }

}