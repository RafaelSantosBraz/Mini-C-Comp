/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import org.antlr.v4.runtime.Token;

/**
 * FACADE?
 *
 * @author wellington
 */
public class Util {

    //<editor-fold defaultstate="collapsed" desc="SINGLETON">
    private static Util instance;

    public static Util getInstance() {
        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }
    //</editor-fold>

    public void error(int numError, Token currentToken, Token aditionalToken) {
        switch (numError) {
            // símbolo já declarado
            case 0:
                System.err.println("Símbolo "
                        + currentToken.getText()
                        + " em ["
                        + currentToken.getLine()
                        + ":"
                        + currentToken.getCharPositionInLine()
                        + "] declarado anteriormente em ["
                        + aditionalToken.getLine()
                        + ":"
                        + aditionalToken.getCharPositionInLine()
                        + "]."
                );
                break;
        }
    }
}
