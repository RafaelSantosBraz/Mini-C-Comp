/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.ArrayList;
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

    public boolean funcParamsCheck(String id, ArrayList<Context> args){
        return true;
    }
    
    public Integer upperTypeResult(Integer t1, Integer t2) {
        switch (t1) {
            case CGrammarLexer.NUMINT:
            case CGrammarLexer.CHARC:
                if (t2 == CGrammarLexer.NUMINT || t2 == CGrammarLexer.CHARC) {
                    return CGrammarLexer.NUMINT;
                } else if (t2 == CGrammarLexer.NDOUBLE) {
                    return CGrammarLexer.NDOUBLE;
                }
                break;
            case CGrammarLexer.NDOUBLE:
                if (t2 == CGrammarLexer.NUMINT || t2 == CGrammarLexer.CHARC || t2 == CGrammarLexer.NDOUBLE) {
                    return CGrammarLexer.NDOUBLE;
                }
        }
        return null;
    }

    public boolean listcompatibilityTypeTest(Integer type, ArrayList<Integer> list) {
        for (Integer t : list) {
            if (!compatibilityTypeTest(type, t)) {
                error(6, type, t);
                return false;
            }
        }
        return true;
    }

    public boolean compatibilityTypeTest(Integer t1, Integer t2) {
        switch (t1) {
            case CGrammarLexer.NUMINT:
            case CGrammarLexer.NDOUBLE:
            case CGrammarLexer.CHARC:
                return t2 != CGrammarLexer.STR;
            case CGrammarLexer.STR:
                return false;
            default:
                return false;
        }
    }

    public void error(int numError, Object c, Object a) {
        // símbolo já declarado                 0
        // expr da array deve ser inteiro       1
        // expressão e tipo incompatíveis       2
        // índice com valores não constantes    3
        // índice d valores tipos incompatíveis 4
        // argumentos de tipos imcompatíveis    5
        // valores de tipos incompatíveis       6

        switch (numError) {
            // símbolo já declarado
            case 0: {
                Token currentToken = (Token) c;
                Token aditionalToken = (Token) a;
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
            }
            // expr da array deve ser inteiro
            case 1: {
                Token currentToken = (Token) c;
                Token aditionalToken = (Token) a;
                System.err.println("Erro (1): a expressão entre ["
                        + currentToken.getLine()
                        + ":"
                        + currentToken.getCharPositionInLine()
                        + "] e ["
                        + aditionalToken.getLine()
                        + ":"
                        + aditionalToken.getCharPositionInLine()
                        + "] deve ser do tipo INT."
                );
                break;
            }
            // expressão e tipo incompatíveis
            case 2: {
                String type = getUpperTypeName((Integer) c);
                String typeExpr = getUpperTypeName((Integer) a);
                System.err.println("Erro (2): a expressão após '=' deve ser compatível com o tipo "
                        + type
                        + ", mas foi encontrado o tipo "
                        + typeExpr
                        + "."
                );
            }
            // índice com valores não constantes
            case 3: {
                Token currentToken = (Token) c;
                Token aditionalToken = (Token) a;
                System.err.println("Erro (3): a expressão entre ["
                        + currentToken.getLine()
                        + ":"
                        + currentToken.getCharPositionInLine()
                        + "] e ["
                        + aditionalToken.getLine()
                        + ":"
                        + aditionalToken.getCharPositionInLine()
                        + "], nesse caso, deve ser formada por valores constantes para o índice."
                );
            }
            // índice com valores tipos incompatíveis
            case 4: {
                Token currentToken = (Token) c;
                Token aditionalToken = (Token) a;
                System.err.println("Erro (4): a expressão entre ["
                        + currentToken.getLine()
                        + ":"
                        + currentToken.getCharPositionInLine()
                        + "] e ["
                        + aditionalToken.getLine()
                        + ":"
                        + aditionalToken.getCharPositionInLine()
                        + "] deve ser formada por valores dos tipos INT e/ou CHAR para o índice."
                );
            }
            // argumentos de tipos imcompatíveis
            case 5: {
                Token currentToken = (Token) c;
                Token aditionalToken = (Token) a;
                System.err.println("Erro (5): os valores entre ["
                        + currentToken.getLine()
                        + ":"
                        + currentToken.getCharPositionInLine()
                        + "] e ["
                        + aditionalToken.getLine()
                        + ":"
                        + aditionalToken.getCharPositionInLine()
                        + "] devem ser compatíveis com o valor declarado."
                );
            }
            // valore de tipos imcompatíveis
            case 6: {
                String type = getUpperTypeName((Integer) c);
                String typeExpr = getUpperTypeName((Integer) a);
                System.err.println("Erro (6): um valor do tipo "
                        + type
                        + " não é compatível com o tipo "
                        + typeExpr
                        + "."
                );
            }
        }
    }

    public String getUpperTypeName(int type) {
        switch (type) {
            case CGrammarLexer.INT:
            case CGrammarLexer.NUMINT:
                return "INT";
            case CGrammarLexer.DOUBLE:
            case CGrammarLexer.NDOUBLE:
                return "DOUBLE";
            case CGrammarLexer.CHAR:
            case CGrammarLexer.CHARC:
                return "CHAR";
            default:
                return null;
        }
    }
}
