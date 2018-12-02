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
    public static final int FILE_DOES_NOT_EXIST = 10;
    public static final int FILE_MANIPULATION_ERROR = 11;
    public static final int PRINTF_ARGS_UNEXPECTED = 12;
    public static final int PRINTF_ARGS_INSUFFICIENT = 13;
    public static final int SCANF_ARGS_DO_NOT_EXIST = 14;
    public static final int SCANF_ARGS_INSUFFICIENT = 15;

}
