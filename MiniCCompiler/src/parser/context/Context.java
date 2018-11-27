/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.context;

import org.antlr.v4.runtime.Token;

/**
 *
 * @author rafael
 */
public class Context {

    private final Integer type;
    private final Boolean constant;
    private final Token token;
    private Object value;

    public Context(Integer type, Boolean constant, Token token) {
        this.type = type;
        this.constant = constant;
        this.token = token;
        value = null;
    }

    public Context(Integer type, Boolean constant, Token token, Object value) {
        this.type = type;
        this.constant = constant;
        this.token = token;
        this.value = value;        
    }

    public Token getToken() {
        return token;
    }

    public Integer getType() {
        return type;
    }

    public Boolean getConstant() {
        return constant;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
