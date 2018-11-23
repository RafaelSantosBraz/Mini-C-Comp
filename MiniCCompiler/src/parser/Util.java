/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;


/**
 * FACADE?
 *
 * @author wellington
 */
public class Util {

    //<editor-fold defaultstate="collapsed" desc="SINGLETON">
    private static Util instance;

    public static Util getInstance() {
        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }
    //</editor-fold>
    
   
}
