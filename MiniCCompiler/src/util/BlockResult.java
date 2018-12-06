/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;


/**
 *
 * @author rafael
 */
public class BlockResult {

    private boolean becauseOfReturn;
    private Object result;

    public BlockResult(boolean becauseOfReturn, Object result) {
        this.becauseOfReturn = becauseOfReturn;
        this.result = result;
    }

    public boolean isBecauseOfReturn() {
        return becauseOfReturn;
    }

    public void setBecauseOfReturn(boolean becauseOfReturn) {
        this.becauseOfReturn = becauseOfReturn;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
