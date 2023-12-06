package com.example.component.column;


import com.example.component.Column;

public class ColorInvlColumn  extends Column {

  private String min;
  private String max;
  public ColorInvlColumn (String name, String min, String max) {
    super(name);
    this.type = ColumnType.COLORINVL.name();
    this.min = min;
    this.max = max;
  }

//  @Override
//  public boolean validate(String data) {
//    // Перевірка, чи вхідний рядок є правильним RGB кодом
//    // Наприклад, припускаємо, що RGB код складається з шести шістнадцяткових цифр
//    return data.matches("[0-9a-fA-F]{6}");
//  }


  @Override
  public boolean validate(String data) {
    if (data == null || !data.matches("[0-9a-fA-F]{6}")) {
      return false;
    }

    return isWithinRange(data, min, max);
  }

  private boolean isWithinRange(String time, String minTime, String maxTime) {
    return time.compareTo(minTime) >= 0 && time.compareTo(maxTime) <= 0;
  }

  public static boolean validateMinMax(String min, String max) {
    if (min != null && min.matches("[0-9a-fA-F]{6}") && max != null
        && max.matches("[0-9a-fA-F]{6}") && (min.compareToIgnoreCase(max)) != 1) {
      return true;
    }
    return false;
  }

  public String getMin() {
    return min;
  }

  public void setMin(String min) {
    this.min = min;
  }

  public String getMax() {
    return max;
  }

  public void setMax(String max) {
    this.max = max;
  }
}