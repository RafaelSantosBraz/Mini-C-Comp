/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

/**
 *
 * @author rafael
 */
public class Context {

    private final int type;
    private final boolean constant;
    private Object value;

    public Context(int type, boolean constant) {
        this.type = type;
        this.constant = constant;
        value = null;
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
