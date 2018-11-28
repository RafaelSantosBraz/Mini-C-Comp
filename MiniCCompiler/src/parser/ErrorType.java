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
public class ErrorType {

    public static final int SYMB_ALREADY_EXISTS = 0;
    public static final int INDEX_IS_NOT_INT = 1;
    public static final int INDEX_IS_NOT_CONST = 2;
    public static final int INCOMPATIBLE_TYPES = 3;
    public static final int SYMB_DOES_NOT_EXIT = 4;
    public static final int SYMB_IS_NOT_FUNCTION = 5;
    public static final int FUNC_PARAMS_ARE_WRONG = 6;
    public static final int FUNC_IS_CALLED_AS_VAR = 7;
    public static final int ADRESS_IS_NOT_MANIPULABLE = 8;
    public static final int CONTENT_IS_NOT_MANIPULABLE = 9;

}
