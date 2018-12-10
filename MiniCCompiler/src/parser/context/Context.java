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
    private Value value;

    public Context(Integer type, Boolean constant, Token token) {
        this.type = type;
        this.constant = constant;
        this.token = token;
        value = new Value();
    }

    public Context(Integer type, Boolean constant, Token token, Value value) {
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

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

}
