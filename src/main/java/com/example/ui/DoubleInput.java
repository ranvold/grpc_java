package com.example.ui;

import javax.swing.*;

public class DoubleInput {
   private JTextField xField;
   private JTextField yField;

   public DoubleInput() {
      xField = new JTextField(5);
      yField = new JTextField(5);
   }

   public InputResult showInputDialog() {
      JPanel myPanel = new JPanel();
      myPanel.add(new JLabel("min:"));
      myPanel.add(xField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("max:"));
      myPanel.add(yField);

      while (true) {
         int result = JOptionPane.showConfirmDialog(null, myPanel,
                 "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);

         if (result == JOptionPane.OK_OPTION) {
            String xValue = xField.getText();
            String yValue = yField.getText();

            if (validate(xValue) && validate(yValue)) {
               return new InputResult(xValue, yValue);
            } else {
               JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid values.");
            }
         } else {
            // User canceled or closed the dialog
            return null;
         }
      }
   }

   public static class InputResult {
      private String min;
      private String max;

      public InputResult(String min, String max) {
         this.min = min;
         this.max = max;
      }

      public String getMin() {
         return min;
      }

      public String getMax() {
         return max;
      }
   }

   // Validation method
   public boolean validate(String data) {
      if (data == null || !data.matches("\\d{2}:\\d{2}")) {
         return false;
      }

      try {
         String[] parts = data.split(":");
         int hour = Integer.parseInt(parts[0]);
         int minute = Integer.parseInt(parts[1]);

         return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
      } catch (NumberFormatException e) {
         return false;
      }
   }
   public static void main(String[] args) {
      DoubleInput dialog = new DoubleInput();
      InputResult result = dialog.showInputDialog();
      if (result != null) {
         System.out.println("x value: " + result.getMin());
         System.out.println("y value: " + result.getMax());
      }
   }
}