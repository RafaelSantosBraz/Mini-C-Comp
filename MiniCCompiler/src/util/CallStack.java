/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Stack;
import parser.context.Context;

/**
 *
 * @author rafael
 */
public class CallStack {

    //<editor-fold defaultstate="collapsed" desc="SINGLETON">
    private static CallStack instance;

    public static CallStack getInstance() {
        if (instance == null) {
            instance = new CallStack();
        }
        return instance;
    }
    //</editor-fold>

    private final Stack<Call> stack;

    public CallStack() {
        stack = new Stack<>();
    }

    public boolean isthere(String name) {
       for (int c = stack.size() - 1; c >= 0; c--){
            if (stack.get(c).isLock()) {
                if (stack.get(c).isThere(name)){
                    return true;
                }
                break;
            }
            if (stack.get(c).isThere(name)) {
                return true;
            }
        }
        return stack.get(0).isThere(name);
    }

    public Call getCall() {
        return stack.peek();
    }

    public void setCall(Call call) {
        stack.add(call);
    }

    public Context getVar(String name) {
        for (int c = stack.size() - 1; c >= 0; c--){
            if (stack.get(c).isLock()) {
                Context var = stack.get(c).getVar(name);
                if (var != null){
                    return var;
                }
                break;
            }
            Context var = stack.get(c).getVar(name);
            if (var != null) {
                return var;
            }
        }
        return stack.get(0).getVar(name);
    }
    
    public Call deleteCall(){
        return stack.pop();
    }
    
    public void reset(){
        Call global = stack.get(0);
        stack.clear();
        stack.push(global);
    }
}
