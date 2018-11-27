/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.ArrayList;
import org.antlr.v4.runtime.Token;
import parser.context.Context;
import semantic.SemanticTable;

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
   
//    public Boolean declareVar(String functionName, Context context) {
//        SemanticTable.getInstance().getTable(functionName).addSymbol(context.getToken().getText(), context);
//    }
    
    public Boolean declareVar(Context context) {
        if (SemanticTable.getInstance().getGlobalSymbolTable().isThere(context.getToken().getText())) {
            ArrayList<Object> args = new ArrayList<>();
            args.add(context);
            args.add(SemanticTable.getInstance().getGlobalSymbolTable().getSymbol(context.getToken().getText()));
            error(ErrorType.SYMB_ALREADY_EXISTS, args);
            return false;
        }
        SemanticTable.getInstance().getGlobalSymbolTable().addSymbol(context.getToken().getText(), context);
        return true;
    }
    
    public void error(Integer errorType, ArrayList<Object> args) {
        System.err.print("Código do Erro = (" + errorType + "): ");
        switch (errorType) {
            case ErrorType.SYMB_ALREADY_EXISTS: {
                Token currentSymbol = ((Context) args.get(0)).getToken();
                Token anotherSymbol = ((Context) args.get(1)).getToken();
                System.err.println("símbolo '"
                        + currentSymbol.getText()
                        + "' em ["
                        + currentSymbol.getLine()
                        + ":"
                        + currentSymbol.getCharPositionInLine()
                        + "] declarado previamente em ["
                        + anotherSymbol.getLine()
                        + ":"
                        + anotherSymbol.getCharPositionInLine()
                        + "]."
                );
                break;
            }
        }
    }
}
