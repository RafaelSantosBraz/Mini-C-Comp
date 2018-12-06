/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.HashMap;
import parser.context.FunctionContext;

/**
 *
 * @author rafael
 */
public class FuncTable {

    //<editor-fold defaultstate="collapsed" desc="SINGLETON">
    private static FuncTable instance;

    public static FuncTable getInstance() {
        if (instance == null) {
            instance = new FuncTable();
        }
        return instance;
    }
    //</editor-fold>

    private final HashMap<String, FunctionContext> marks;

    public FuncTable(){
        marks =  new HashMap<>();
    }
    
    public FunctionContext getFunc(String funcName) {
        return marks.get(funcName);
    }

    public void addFunc(String funcName, FunctionContext func) {
        marks.put(funcName, func);
    }

    public boolean isThere(String funcName) {
        return (marks.get(funcName) != null);
    }
}
