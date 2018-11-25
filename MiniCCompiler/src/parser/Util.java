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

    public boolean compatibilityTypeTest(Integer t1, Integer t2) {
        switch (t1) {
            case CGrammarLexer.NUMINT:
            case CGrammarLexer.NDOUBLE:
            case CGrammarLexer.CHARC:
                if (t2 == CGrammarLexer.STR) {
                    return false;
                } else {
                    return true;
                }
            default:
                return false;
        }

    }

    public void error(int numError, Token currentToken, Token aditionalToken) {
        switch (numError) {
            // símbolo já declarado
            case 0:
                System.err.println("Erro (0): Símbolo "
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
            case 1:
                System.err.println("Erro (1): a expressão entre ["
                        + currentToken.getLine()
                        + ":"
                        + currentToken.getCharPositionInLine()
                        + "] e ["
                        + aditionalToken.getLine()
                        + ":"
                        + aditionalToken.getCharPositionInLine()
                        + "] deve ser do tipo INTEIRO."
                );
                break;
        }
    }
}
