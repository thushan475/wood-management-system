package lk.ijse.wood_management.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CalculatorController {

    @FXML
    private TextField displayField;

    @FXML
    private Label currentTotal;

    private double total = 0;
    private String operator = "";
    private boolean isNewNumber = true;



    private void addDigit(String digit) {
        if (isNewNumber) {
            displayField.setText(digit);
            isNewNumber = false;
        } else {
            displayField.setText(displayField.getText() + digit);
        }
    }

    @FXML private void numZeroClick()  { addDigit("0"); }
    @FXML private void numOneClick()   { addDigit("1"); }
    @FXML private void numTwoClick()   { addDigit("2"); }
    @FXML private void numThreeClick() { addDigit("3"); }
    @FXML private void numFourClick()  { addDigit("4"); }
    @FXML private void numFiveClick()  { addDigit("5"); }
    @FXML private void numSixClick()   { addDigit("6"); }
    @FXML private void numSevenClick() { addDigit("7"); }
    @FXML private void numEightClick() { addDigit("8"); }
    @FXML private void numNineClick()  { addDigit("9"); }



    @FXML
    private void decimalClick() {
        if (isNewNumber) {
            displayField.setText("0.");
            isNewNumber = false;
        } else if (!displayField.getText().contains(".")) {
            displayField.setText(displayField.getText() + ".");
        }
    }



    private void setOperator(String op) {
        calculate();
        operator = op;
        displayField.setText(String.valueOf(total));
        isNewNumber = true;
    }

    @FXML private void additionOperator() { setOperator("+"); }
    @FXML private void subOperator()      { setOperator("-"); }
    @FXML private void mulOperator()      { setOperator("*"); }
    @FXML private void divOperator()      { setOperator("/"); }
    @FXML private void modulus()          { setOperator("%"); }



    private void calculate() {
        if (displayField.getText().isEmpty()) return;

        double currentValue = Double.parseDouble(displayField.getText());

        if (operator.isEmpty()) {
            total = currentValue;
        } else {
            switch (operator) {
                case "+" -> total += currentValue;
                case "-" -> total -= currentValue;
                case "*" -> total *= currentValue;
                case "/" -> {
                    if (currentValue == 0) {
                        displayField.setText("Error");
                        isNewNumber = true;
                        return;
                    }
                    total /= currentValue;
                }
                case "%" -> total %= currentValue;
            }
        }
        currentTotal.setText(String.valueOf(total));
    }


    @FXML
    private void printTotal() {
        calculate();
        displayField.setText(String.valueOf(total));
        operator = "";
        isNewNumber = true;
    }


    @FXML
    private void clear() {
        total = 0;
        operator = "";
        displayField.setText("");
        currentTotal.setText("");
        isNewNumber = true;
    }
}
