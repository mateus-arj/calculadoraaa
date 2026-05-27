package com.example.calculadoraaa;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class HelloController {

    @FXML
    private TextArea visor;

    @FXML
    private TextArea historico;

    private String conta = "";

    @FXML
    void numero(ActionEvent event) {

        Button botao = (Button) event.getSource();

        conta += botao.getText();

        visor.setText(conta);

        visor.positionCaret(conta.length());
    }

    @FXML
    void decimal() {

        conta += ".";

        visor.setText(conta);

        visor.positionCaret(conta.length());
    }

    @FXML
    void operacao(ActionEvent event) {

        Button botao = (Button) event.getSource();

        String op = botao.getText();

        if (op.equals("×")) {
            op = "*";
        }

        if (op.equals("÷")) {
            op = "/";
        }

        if (!conta.isEmpty()) {

            char ultimo = conta.charAt(conta.length() - 1);

            if (ultimo != '+' &&
                    ultimo != '-' &&
                    ultimo != '*' &&
                    ultimo != '/') {

                conta += op;

                visor.setText(conta);

                visor.positionCaret(conta.length());
            }
        }
    }

    @FXML
    void igual() {

        try {

            double resultado = calcularExpressao(conta);

            historico.appendText(
                    conta + " = " + removerZero(resultado) + "\n"
            );

            visor.setText(removerZero(resultado));

            conta = removerZero(resultado);

            visor.positionCaret(conta.length());

        } catch (Exception ex) {

            visor.setText("Erro");
        }
    }

    @FXML
    void limpar() {

        conta = "";

        visor.setText("");
    }

    @FXML
    void apagar() {

        if (!conta.isEmpty()) {

            conta = conta.substring(0, conta.length() - 1);

            visor.setText(conta);

            visor.positionCaret(conta.length());
        }
    }

    private double calcularExpressao(String expressao) {

        String[] partes =
                expressao.split("(?=[-+*/])|(?<=[-+*/])");

        double resultado =
                Double.parseDouble(partes[0]);

        for (int i = 1; i < partes.length; i += 2) {

            String operador = partes[i];

            double numero =
                    Double.parseDouble(partes[i + 1]);

            switch (operador) {

                case "+":
                    resultado += numero;
                    break;

                case "-":
                    resultado -= numero;
                    break;

                case "*":
                    resultado *= numero;
                    break;

                case "/":
                    resultado /= numero;
                    break;
            }
        }

        return resultado;
    }

    private String removerZero(double numero) {

        if (numero == (int) numero) {

            return String.valueOf((int) numero);
        }

        return String.valueOf(numero);
    }
}