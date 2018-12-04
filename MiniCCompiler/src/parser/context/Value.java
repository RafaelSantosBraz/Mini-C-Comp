/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.context;

/**
 *
 * @author rafael
 */
public class Value {

    private Object realValue;

    public Value(Object realvalue) {
        this.realValue = realvalue;
    }

    public Value() {
        this.realValue = null;
    }

    public Object getRealValue() {
        return realValue;
    }

    public void setRealValue(Object realValue) {
        this.realValue = realValue;
    }

}
