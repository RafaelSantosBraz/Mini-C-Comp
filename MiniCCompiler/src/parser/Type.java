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
public class Type {

    public static final int INT = 1000;
    public static final int DOUBLE = 2000;
    public static final int CHAR = 3000;

    public static final int POINTER = 4000;
    public static final int POINTER_INT = 4001;
    public static final int POINTER_DOUBLE = 4002;
    public static final int POINTER_CHAR = 4003;

    public static final int POINTER_POINTER = 5000;
    public static final int POINTER_POINTER_INT = 5001;
    public static final int POINTER_POINTER_DOUBLE = 5002;
    public static final int POINTER_POINTER_CHAR = 5003;

    public static final int FUNCTION_MARK = 6000;
    
    public static final int VOID = 7000;
    
    public static Integer getBasicType(Integer complexType) {
        if (complexType == INT || complexType == DOUBLE || complexType == CHAR) {
            return complexType;
        }
        if (complexType == POINTER || complexType == POINTER_INT
                || complexType == POINTER_DOUBLE || complexType == POINTER_CHAR) {
            return POINTER;
        }
        if (complexType == POINTER_POINTER || complexType == POINTER_POINTER_INT
                || complexType == POINTER_POINTER_DOUBLE || complexType == POINTER_POINTER_CHAR) {
            return POINTER_POINTER;
        }
        if (complexType == FUNCTION_MARK) {
            return FUNCTION_MARK;
        }
        return null;
    }

    public static Integer getIntTypeForPointer(Integer primitive) {
        switch (primitive) {
            case INT:
                return POINTER_INT;
            case DOUBLE:
                return POINTER_DOUBLE;
            case CHAR:
                return POINTER_CHAR;
        }
        return null;
    }

    public static Integer getIntTypeForPointerPointer(Integer primitive) {
        switch (primitive) {
            case INT:
                return POINTER_POINTER_INT;
            case DOUBLE:
                return POINTER_POINTER_DOUBLE;
            case CHAR:
                return POINTER_POINTER_CHAR;
        }
        return null;
    }

    public static String getTypeName(Integer typeInt, Boolean constant) {
        if (constant) {
            return "const " + getLiteralTypeName(typeInt);
        }
        return getLiteralTypeName(typeInt);
    }

    public static String getLiteralTypeName(Integer typeInt) {
        switch (typeInt) {
            case INT:
                return "int";
            case DOUBLE:
                return "double";
            case CHAR:
                return "char";
            case POINTER_INT:
                return "int*";
            case POINTER_DOUBLE:
                return "double*";
            case POINTER_CHAR:
                return "char*";
            case POINTER_POINTER_INT:
                return "int[][]";
            case POINTER_POINTER_DOUBLE:
                return "double[][]";
            case POINTER_POINTER_CHAR:
                return "char[][]";
            default:
                return null;
        }
    }

    public static Boolean isPrimitive(Integer type) {
        return (type == INT || type == DOUBLE || type == CHAR);
    }

    public static Integer promoteType(Integer type) {
        if (isPrimitive(type)) {
            if (type == INT) {
                return POINTER_INT;
            }
            if (type == DOUBLE) {
                return POINTER_DOUBLE;
            }
            if (type == CHAR) {
                return POINTER_CHAR;
            }
        }
        if (getBasicType(type) == POINTER) {
            if (type == POINTER_INT) {
                return POINTER_POINTER_INT;
            }
            if (type == POINTER_DOUBLE) {
                return POINTER_POINTER_DOUBLE;
            }
            if (type == POINTER_CHAR) {
                return POINTER_POINTER_CHAR;
            }
        }
        return null;
    }

    public static Integer lowerType(Integer type) {
        if (getBasicType(type) == POINTER) {
            if (type == POINTER_INT) {
                return INT;
            }
            if (type == POINTER_DOUBLE) {
                return DOUBLE;
            }
            if (type == POINTER_CHAR) {
                return CHAR;
            }
        }
        if (getBasicType(type) == POINTER_POINTER) {
            if (type == POINTER_POINTER_INT) {
                return POINTER_INT;
            }
            if (type == POINTER_POINTER_DOUBLE) {
                return POINTER_DOUBLE;
            }
            if (type == POINTER_POINTER_CHAR) {
                return POINTER_CHAR;
            }
        }
        return null;
    }
}
