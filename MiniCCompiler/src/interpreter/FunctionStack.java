/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter;

import java.util.Stack;
import parser.SymbolTable;

/**
 *
 * @author rafael
 */
public class FunctionStack {

    //<editor-fold defaultstate="collapsed" desc="SINGLETON">
    private static FunctionStack instance;

    public static FunctionStack getInstance() {
        if (instance == null) {
            instance = new FunctionStack();
        }
        return instance;
    }
    //</editor-fold> 

    private Stack<SymbolTable> stack;

    public SymbolTable getTable() {
        return stack.get(stack.size() - 1);
    }

    public void addTable(SymbolTable table) {
        stack.push(table);
    }

    public void clear() {
        stack.clear();
    }

}
