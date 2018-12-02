/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.ArrayList;
import java.util.Objects;
import org.antlr.v4.runtime.Token;
import parser.context.Context;
import parser.context.FunctionContext;
import parser.context.PointerContext;
import parser.context.PointerPointerContext;
import parser.context.PrimitiveContext;
import semantic.SemanticTable;

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

    public Context getContentContextOf(Context context) {
        Integer lowerType = Type.lowerType(context.getType());
        if (lowerType != null) {
            if (Type.isPrimitive(lowerType)) {
                return new PrimitiveContext(lowerType, context.getConstant(), context.getToken());
            }
            return new PointerContext(lowerType, context.getConstant(), context.getToken());
        }
        ArrayList<Object> args = new ArrayList<>();
        args.add(context);
        error(ErrorType.CONTENT_IS_NOT_MANIPULABLE, args);
        return null;
    }
    
    public Context promoteContextType(Context context) {
        Integer promoted = Type.promoteType(context.getType());
        if (promoted != null) {
            if (Type.getBasicType(promoted) == Type.POINTER) {
                return new PointerContext(promoted, context.getConstant(), context.getToken());
            }
            return new PointerPointerContext(promoted, context.getConstant(), context.getToken());
        }
        ArrayList<Object> args = new ArrayList<>();
        args.add(context);
        error(ErrorType.ADRESS_IS_NOT_MANIPULABLE, args);
        return null;
    }
    
    public Boolean functionCallCheck(Context context, ArrayList<Context> params) {
        FunctionContext func = isFunctionContext(context);
        if (func != null) {
            if (func.paramsCheck(params)) {
                return true;
            }
            ArrayList<Object> args = new ArrayList<>();
            args.add(context);
            error(ErrorType.FUNC_PARAMS_ARE_WRONG, args);
            return null;
        }
        ArrayList<Object> args = new ArrayList<>();
        args.add(context);
        error(ErrorType.SYMB_IS_NOT_FUNCTION, args);
        return null;
    }
    
    public FunctionContext isFunctionContext(Context func) {
        if (func instanceof FunctionContext) {
            return (FunctionContext) func;
        }
        return null;
    }
    
    public Boolean doesGlobalContextExist(Context context) {
        return SemanticTable.getInstance().getGlobalSymbolTable().isThere(context.getToken().getText());
    }
    
    public Context getContextFromTable(Context var) {
        if (SemanticTable.getInstance().getGlobalSymbolTable().isThere(var.getToken().getText())) {
            return SemanticTable.getInstance().getGlobalSymbolTable().getSymbol(var.getToken().getText());
        }
        ArrayList<Object> args = new ArrayList<>();
        args.add(var);
        error(ErrorType.SYMB_DOES_NOT_EXIT, args);
        return null;
    }
    
    public Integer toUpperType(Context type1, Context type2) {
        if (MathOpCompatibilityCheck(type1, type2)) {
            switch (type1.getType()) {
                case Type.INT: {
                    if (type2.getType() == Type.INT || type2.getType() == Type.CHAR) {
                        return Type.INT;
                    }
                    if (type2.getType() == Type.DOUBLE) {
                        return Type.DOUBLE;
                    }
                    break;
                }
                case Type.CHAR: {
                    if (type2.getType() == Type.INT || type2.getType() == Type.CHAR) {
                        return Type.CHAR;
                    }
                    if (type2.getType() == Type.DOUBLE) {
                        return Type.DOUBLE;
                    }
                    break;
                }
                case Type.DOUBLE: {
                    if (type2.getType() == Type.INT || type2.getType() == Type.CHAR || type2.getType() == Type.DOUBLE) {
                        return Type.DOUBLE;
                    }
                    break;
                }
            }
        }
        return null;
    }
    
    public Boolean MathOpCompatibilityCheck(Context type1, Context type2) {
        if (Type.isPrimitive(type1.getType()) && Type.isPrimitive(type2.getType())) {
            return true;
        }
        ArrayList<Object> args = new ArrayList<>();
        args.add(type1);
        args.add(type2);
        error(ErrorType.INCOMPATIBLE_TYPES, args);
        return false;
    }
    
    public Boolean declAtribCompatibilityCheck(Context mainType, ArrayList<Context> types) {
        for (Context t : types) {
            if (!declAtribCompatibilityCheck(mainType, t)) {
                ArrayList<Object> args = new ArrayList<>();
                args.add(mainType);
                args.add(t);
                error(ErrorType.INCOMPATIBLE_TYPES, args);
                return false;
            }
        }
        return true;
    }
    
    public Boolean declAtribCompatibilityCheck(Context type1, Context type2) {
        switch (type1.getType()) {
            case Type.DOUBLE:
            case Type.CHAR:
            case Type.INT: {
                if (Type.isPrimitive(type2.getType())) {
                    return true;
                }
                break;
            }
            case Type.POINTER_POINTER_CHAR:
            case Type.POINTER_POINTER_DOUBLE:
            case Type.POINTER_POINTER_INT:
            case Type.POINTER_CHAR:
            case Type.POINTER_DOUBLE:
            case Type.POINTER_INT: {
                if (Objects.equals(type2.getType(), type1.getType())) {
                    return true;
                }
                break;
            }
        }
        ArrayList<Object> args = new ArrayList<>();
        args.add(type1);
        args.add(type2);
        error(ErrorType.INCOMPATIBLE_TYPES, args);
        return false;
    }
    
    public Boolean constIndexCheck(Context context) {
        if (indexCheck(context)) {
            if (context.getConstant()) {
                return true;
            }
            ArrayList<Object> args = new ArrayList<>();
            args.add(context);
            error(ErrorType.INDEX_IS_NOT_CONST, args);
        }
        return false;
    }
    
    public Boolean indexCheck(Context context) {
        if (Type.getBasicType(context.getType()) == Type.INT) {
            return true;
        }
        ArrayList<Object> args = new ArrayList<>();
        args.add(context);
        error(ErrorType.INDEX_IS_NOT_INT, args);
        return false;
    }
    
    public Context createCorrectContextInstance(Context previousContext, Token newToken) {
        Integer basicType = Type.getBasicType(previousContext.getType());
        if (Type.isPrimitive(basicType)) {
            return new PrimitiveContext(previousContext.getType(), previousContext.getConstant(), newToken);
        }
        if (basicType == Type.POINTER) {
            return new PointerContext(previousContext.getType(), previousContext.getConstant(), newToken);
        }
        if (basicType == Type.POINTER_POINTER) {
            return new PointerPointerContext(previousContext.getType(), previousContext.getConstant(), newToken);
        }
        if (basicType == Type.FUNCTION_MARK) {
            return new FunctionContext(previousContext.getType(), newToken);
        }
        return null;
    }
    
    public Boolean declareMultVar(String functionName, ArrayList<Context> vars){
        for (Context t: vars){
            if (!declareVar(functionName, t)){
                return false;
            }
        }
        return true;
    }
    
    public Boolean declareFuncInTable(String functionName, Context context){
        if (!SemanticTable.getInstance().isThere(functionName)) {
            SemanticTable.getInstance().createTable(functionName);
            SemanticTable.getInstance().getGlobalSymbolTable().addSymbol(functionName, context);
        }    
        return true;
    }
    
    public Boolean declareVar(String functionName, Context context) {
        if (!SemanticTable.getInstance().isThere(functionName)) {
            SemanticTable.getInstance().createTable(functionName);
            SemanticTable.getInstance().getGlobalSymbolTable().addSymbol(functionName, context);
        }        
        SemanticTable.getInstance().getTable(functionName).addSymbol(context.getToken().getText(), context);
        return true;
    }
    
    public Boolean declareVar(Context context) {
        if (SemanticTable.getInstance().getGlobalSymbolTable().isThere(context.getToken().getText())) {
            ArrayList<Object> args = new ArrayList<>();
            args.add(context);
            args.add(SemanticTable.getInstance().getGlobalSymbolTable().getSymbol(context.getToken().getText()));
            error(ErrorType.SYMB_ALREADY_EXISTS, args);
            return false;
        }
        SemanticTable.getInstance().getGlobalSymbolTable().addSymbol(context.getToken().getText(), context);
        return true;
    }
    
    public void error(Integer errorType, ArrayList<Object> args) {
        System.err.print("Código do Erro = (" + errorType + "): ");
        switch (errorType) {
            case ErrorType.SYMB_ALREADY_EXISTS: {
                Token currentSymbol = ((Context) args.get(0)).getToken();
                Token anotherSymbol = ((Context) args.get(1)).getToken();
                System.err.println("símbolo '"
                        + currentSymbol.getText()
                        + "' em ["
                        + currentSymbol.getLine()
                        + ":"
                        + currentSymbol.getCharPositionInLine()
                        + "] declarado previamente em ["
                        + anotherSymbol.getLine()
                        + ":"
                        + anotherSymbol.getCharPositionInLine()
                        + "]."
                );
                break;
            }
            case ErrorType.INDEX_IS_NOT_INT: {
                Context expr = (Context) args.get(0);
                System.err.println("a expressão do índice deve ser do tipo 'int', mas a expressão '"
                        + expr.getToken().getText()
                        + "' em ["
                        + expr.getToken().getLine()
                        + ":"
                        + expr.getToken().getCharPositionInLine()
                        + "] retorna um '"
                        + Type.getTypeName(expr.getType(), expr.getConstant())
                        + "'"
                );
                break;
            }
            case ErrorType.INDEX_IS_NOT_CONST: {
                Context expr = (Context) args.get(0);
                System.err.println("a expressão do índice deve ser constante, mas a expressão '"
                        + expr.getToken().getText()
                        + "' em ["
                        + expr.getToken().getLine()
                        + ":"
                        + expr.getToken().getCharPositionInLine()
                        + "] retorna um '"
                        + Type.getTypeName(expr.getType(), expr.getConstant())
                        + "'"
                );
                break;
            }
            case ErrorType.INCOMPATIBLE_TYPES: {
                Context t1 = (Context) args.get(0);
                Context t2 = (Context) args.get(1);
                System.err.println("o tipo '"
                        + Type.getTypeName(t1.getType(), t1.getConstant())
                        + "' em ["
                        + t1.getToken().getLine()
                        + ":"
                        + t1.getToken().getCharPositionInLine()
                        + "] não é compatível com '"
                        + Type.getTypeName(t2.getType(), t2.getConstant())
                        + "' em ["
                        + t2.getToken().getLine()
                        + ":"
                        + t2.getToken().getCharPositionInLine()
                        + "]."
                );
                break;
            }
            case ErrorType.SYMB_DOES_NOT_EXIT: {
                Context var = (Context) args.get(0);
                System.err.println("o símbolo '"
                        + var.getToken().getText()
                        + "' em ["
                        + var.getToken().getLine()
                        + ":"
                        + var.getToken().getCharPositionInLine()
                        + "] não foi previamente declarado."
                );
                break;
            }
            case ErrorType.FUNC_PARAMS_ARE_WRONG: {
                Context func = (Context) args.get(0);
                System.err.println("a chamada da função '"
                        + func.getToken().getText()
                        + "' em ["
                        + func.getToken().getLine()
                        + ":"
                        + func.getToken().getCharPositionInLine()
                        + "] não informa os parâmetros corretos."
                );
                break;
            }
            case ErrorType.FUNC_IS_CALLED_AS_VAR: {
                Context func = (Context) args.get(0);
                Context var = (Context) args.get(1);
                System.err.println("a função '"
                        + func.getToken().getText()
                        + "' declarada em ["
                        + func.getToken().getLine()
                        + ":"
                        + func.getToken().getCharPositionInLine()
                        + "] é informada como variável em ["
                        + var.getToken().getLine()
                        + ":"
                        + var.getToken().getCharPositionInLine()
                        + "]."
                );
                break;
            }
            case ErrorType.ADRESS_IS_NOT_MANIPULABLE: {
                Context id = (Context) args.get(0);
                System.err.println("não é possível manipular diretamente o endereço de'"
                        + id.getToken().getText()
                        + "' em ["
                        + id.getToken().getLine()
                        + ":"
                        + id.getToken().getCharPositionInLine()
                        + "]."
                );
                break;
            }
            case ErrorType.CONTENT_IS_NOT_MANIPULABLE: {
                Context id = (Context) args.get(0);
                System.err.println("não é possível manipular diretamente o conteúdo de'"
                        + id.getToken().getText()
                        + "' em ["
                        + id.getToken().getLine()
                        + ":"
                        + id.getToken().getCharPositionInLine()
                        + "]."
                );
                break;
            }
            case ErrorType.FILE_DOES_NOT_EXIST: {
                String file = (String) args.get(0);
                System.err.println("arquivo '"
                        + file
                        + "' não encontrado para realizar o INCLUDE."
                );
                break;
            }
            case ErrorType.FILE_MANIPULATION_ERROR: {
                String file = (String) args.get(0);
                System.err.println("arquivo '"
                        + file
                        + "' não pode ser manipulado (escrita/leitura) no momento."
                );
                break;
            }
        }
    }
}
