/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import org.antlr.v4.runtime.Token;

/**
 *
 * @author rafael
 */
public class Context {

    private final int type;
    private final boolean pointer;
    private final boolean constant;
    private final Token token;
    private Object value;

    public Context(int type, boolean pointer, boolean constant, Token token) {
        this.type = type;
        this.constant = constant;
        this.pointer = pointer;
        this.token = token;
        value = null;
    }

    public boolean isPointer() {
        return pointer;
    }

    public Token getToken() {
        return token;
    }

    public int getType() {
        return type;
    }

    public boolean isConstant() {
        return constant;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
