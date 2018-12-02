/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantic;

import java.util.HashMap;
import parser.*;

/**
 *
 * @author rafael
 */
public class SemanticTable {

    //<editor-fold defaultstate="collapsed" desc="SINGLETON">
    private static SemanticTable instance;

    public static SemanticTable getInstance() {
        if (instance == null) {
            instance = new SemanticTable();
        }
        return instance;
    }
    //</editor-fold>

    private final SymbolTable globalSymbolTable;
    private final HashMap<String, SymbolTable> tables;

    private SemanticTable() {
        globalSymbolTable = new SymbolTable();
        tables = new HashMap<>();
    }

    public SymbolTable getGlobalSymbolTable() {
        return globalSymbolTable;
    }

    public SymbolTable getTable(String functionName) {
        return tables.get(functionName);
    }

    public void createTable(String functionName) {
        tables.put(functionName, new SymbolTable());
    }

    public int countTables() {
        return tables.size();
    }

    public Boolean isThere(String functionName) {
        return tables.containsKey(functionName);
    }

    public void deleteTable(String functionName) {
        tables.remove(functionName);
    }
}
