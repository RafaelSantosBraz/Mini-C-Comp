/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.HashMap;

/**
 *
 * @author rafael
 */
public class CSymbolTable {

    //<editor-fold defaultstate="collapsed" desc="SINGLETON">
    private static CSymbolTable instance;

    public static CSymbolTable getInstance() {
        if (instance == null) {
            instance = new CSymbolTable();
        }
        return instance;
    }
    //</editor-fold>

    private final SymbolTable globalSymbolTable;
    private final HashMap<String, SymbolTable> tables;

    public CSymbolTable() {
        globalSymbolTable = new SymbolTable();
        tables = new HashMap<>();
    }

    public SymbolTable getGlobalSymbolTable() {
        return globalSymbolTable;
    }

    public SymbolTable getTable(String functionName) {
        return tables.get(functionName);
    }
    
    public void createTable(String functionName){
        tables.put(functionName, new SymbolTable());
    }
}
