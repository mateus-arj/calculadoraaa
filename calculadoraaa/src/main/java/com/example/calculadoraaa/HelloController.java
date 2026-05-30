package com.example.calculadoraaa;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.util.ArrayList;

public class HelloController {

    private double resolverParenteses(String expressao,
                                      boolean prioridadeMatematica) {

        while (expressao.contains("(")) {

            int abre = expressao.lastIndexOf("(");

            int fecha = expressao.indexOf(")", abre);

            String dentro =
                    expressao.substring(abre + 1, fecha);

            double resultadoInterno;

            if (prioridadeMatematica) {

                resultadoInterno =
                        calcularComPrioridade(dentro);

            } else {

                resultadoInterno =
                        calcularEsquerdaDireita(dentro);
            }

            expressao =
                    expressao.substring(0, abre)
                            + removerZero(resultadoInterno)
                            + expressao.substring(fecha + 1);
        }

        if (prioridadeMatematica) {

            return calcularComPrioridade(expressao);

        } else {

            return calcularEsquerdaDireita(expressao);
        }
    }

    private ArrayList<String> tokenizar(String expressao) {

        ArrayList<String> tokens = new ArrayList<>();

        String numero = "";

        for (int i = 0; i < expressao.length(); i++) {

            char c = expressao.charAt(i);

            if (Character.isDigit(c) || c == '.') {

                numero += c;

            } else {

                if (c == '-' &&
                        (i == 0 ||
                                expressao.charAt(i - 1) == '+' ||
                                expressao.charAt(i - 1) == '-' ||
                                expressao.charAt(i - 1) == '*' ||
                                expressao.charAt(i - 1) == '/')) {

                    numero += c;

                } else {

                    if (!numero.isEmpty()) {

                        tokens.add(numero);

                        numero = "";
                    }

                    tokens.add(String.valueOf(c));
                }
            }
        }

        if (!numero.isEmpty()) {

            tokens.add(numero);
        }

        return tokens;
    }

    @FXML
    private TextArea visor;

    @FXML
    private TextArea historico;

    @FXML
    private Button modoBotao;

    private String conta = "";

    private boolean modoMatematico = false;

    @FXML
    void numero(ActionEvent event) {

        Button botao = (Button) event.getSource();

        conta += botao.getText();

        visor.setText(conta);

        visor.positionCaret(conta.length());
    }

    @FXML
    void parentese(ActionEvent event) {

        Button botao = (Button) event.getSource();

        String p = botao.getText();

        if (p.equals("(")) {

            if (!conta.isEmpty()) {

                char ultimo = conta.charAt(conta.length() - 1);

                if (Character.isDigit(ultimo) || ultimo == ')') {

                    conta += "*";
                }
            }
        }

        if (p.equals(")")) {

            if (conta.isEmpty()) {
                return;
            }

            char ultimo = conta.charAt(conta.length() - 1);

            if (ultimo == '(') {
                return;
            }
        }

        conta += p;

        visor.setText(conta);

        visor.positionCaret(conta.length());
    }




    @FXML
    void decimal() {

        int i = conta.length() - 1;

        while (i >= 0 &&
                conta.charAt(i) != '+' &&
                conta.charAt(i) != '-' &&
                conta.charAt(i) != '*' &&
                conta.charAt(i) != '/' &&
                conta.charAt(i) != '(' &&
                conta.charAt(i) != ')') {

            if (conta.charAt(i) == '.') {
                return;
            }

            i--;
        }

        conta += ".";

        visor.setText(conta);

        visor.positionCaret(conta.length());
    }

    @FXML
    void operacao(ActionEvent event) {

        Button botao = (Button) event.getSource();

        String op = botao.getText();

        if (op.equals("×")) op = "*";

        if (op.equals("÷")) op = "/";

        if (conta.isEmpty()) {

            if (op.equals("-")) {

                conta = "-";

                visor.setText(conta);

                visor.positionCaret(conta.length());
            }

            return;
        }

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

    @FXML
    void igual() {

        try {

            if (!parentesesBalanceados()) {

                visor.setText("Erro");

                return;
            }

            if (terminaComOperador()) {

                visor.setText("Erro");

                return;
            }

            double resultado;

            if (modoMatematico) {

                resultado = resolverParenteses(conta, true);

            } else {

                resultado = resolverParenteses(conta, false);
            }

            historico.appendText(
                    conta + " = " + removerZero(resultado) + "\n"
            );

            visor.setText(removerZero(resultado));

            conta = removerZero(resultado);

        } catch (Exception ex) {

            visor.setText("Erro");
        }
    }




    @FXML
    void alternarModo() {

        modoMatematico = !modoMatematico;

        if (modoMatematico) {

            modoBotao.setText("Modo: Matemático");

        } else {

            modoBotao.setText("Modo: Normal");
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

    private double calcularEsquerdaDireita(String expressao) {

        ArrayList<String> lista = tokenizar(expressao);

        double resultado =
                Double.parseDouble(lista.get(0));

        for (int i = 1; i < lista.size(); i += 2) {

            String operador = lista.get(i);

            double numero =
                    Double.parseDouble(lista.get(i + 1));

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

    private double calcularComPrioridade(String expressao) {

        ArrayList<String> lista = tokenizar(expressao);

        for (int i = 0; i < lista.size(); i++) {

            String item = lista.get(i);

            if (item.equals("*") || item.equals("/")) {

                double a =
                        Double.parseDouble(lista.get(i - 1));

                double b =
                        Double.parseDouble(lista.get(i + 1));

                double resultado;

                if (item.equals("*")) {

                    resultado = a * b;

                } else {

                    resultado = a / b;
                }

                lista.set(i - 1,
                        String.valueOf(resultado));

                lista.remove(i);

                lista.remove(i);

                i--;
            }
        }

        double resultado =
                Double.parseDouble(lista.get(0));

        for (int i = 1; i < lista.size(); i += 2) {

            String operador = lista.get(i);

            double numero =
                    Double.parseDouble(lista.get(i + 1));

            switch (operador) {

                case "+":
                    resultado += numero;
                    break;

                case "-":
                    resultado -= numero;
                    break;
            }
        }

        return resultado;
    }

    private boolean terminaComOperador() {

        if (conta.isEmpty()) {
            return true;
        }

        char ultimo = conta.charAt(conta.length() - 1);

        return ultimo == '+'
                || ultimo == '-'
                || ultimo == '*'
                || ultimo == '/'
                || ultimo == '(';
    }


    private boolean parentesesBalanceados() {

        int contador = 0;

        for (int i = 0; i < conta.length(); i++) {

            char c = conta.charAt(i);

            if (c == '(') {
                contador++;
            }

            if (c == ')') {
                contador--;
            }

            if (contador < 0) {
                return false;
            }
        }

        return contador == 0;
    }




    private String removerZero(double numero) {

        if (numero == (int) numero) {

            return String.valueOf((int) numero);
        }

        return String.valueOf(numero);
    }
}