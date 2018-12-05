/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import parser.context.Context;
import java.util.HashMap;

/**
 *
 * @author rafael
 */
public class SymbolTable implements Cloneable {

    private final HashMap<String, Context> memory;

    public SymbolTable() {
        memory = new HashMap<>();
    }

    public Boolean isThere(String Symbol) {
        return memory.containsKey(Symbol);
    }

    public void addSymbol(String varName, Context value) {
        memory.put(varName, value);
    }

    public Context getSymbol(String varName) {
        return memory.get(varName);
    }

    public void deleteSymbol(String varName) {
        memory.remove(varName);
    }

    @Override
    public SymbolTable clone() {
        try {
            return (SymbolTable) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
}
