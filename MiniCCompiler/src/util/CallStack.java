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
        for (Call c : stack) {
            if (c.isLock()) {
                return c.isThere(name);
            }
            if (c.isThere(name)) {
                return true;
            }
        }
        return stack.get(0).isThere(name);
    }

    public Call getCall() {
        return stack.get(stack.size() - 1);
    }

    public void setCall(Call call) {
        stack.add(call);
    }

    public Context getVar(String name) {
        for (Call c : stack) {
            if (c.isLock()) {
                return c.getVar(name);
            }
            if (c.isThere(name)) {
                return c.getVar(name);
            }
        }
        return stack.get(0).getVar(name);
    }

}
