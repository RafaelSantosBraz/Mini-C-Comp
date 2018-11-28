/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantic;

import java.util.ArrayList;
import parser.*;
import parser.context.Context;
import parser.context.FunctionContext;
import parser.context.PointerContext;
import parser.context.PrimitiveContext;

/**
 *
 * @author rafael
 */
public class SemanticVisitor extends CGrammarBaseVisitor<Object> {

    //<editor-fold defaultstate="collapsed" desc="define">
    @Override
    public Object visitDefineNUM(CGrammarParser.DefineNUMContext ctx) {
        Context numContext = (Context) visit(ctx.num());
        Util.getInstance().declareVar(new PrimitiveContext(numContext.getType(), numContext.getConstant(), ctx.ID().getSymbol()));
        return null;
    }

    @Override
    public Object visitDefineChar(CGrammarParser.DefineCharContext ctx) {
        Util.getInstance().declareVar(new PrimitiveContext(Type.CHAR, true, ctx.ID().getSymbol()));
        return null;
    }

    @Override
    public Object visitDefineSTR(CGrammarParser.DefineSTRContext ctx) {
        Util.getInstance().declareVar(new PointerContext(Type.POINTER_CHAR, true, ctx.ID().getSymbol()));
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="num">
    @Override
    public Object visitNumInt(CGrammarParser.NumIntContext ctx) {
        return new PrimitiveContext(Type.INT, true, ctx.NUMINT().getSymbol());
    }

    @Override
    public Object visitNumDouble(CGrammarParser.NumDoubleContext ctx) {
        return new PrimitiveContext(Type.DOUBLE, true, ctx.NDOUBLE().getSymbol());
    }
    //</editor-fold>

    @Override
    public Object visitGlobal(CGrammarParser.GlobalContext ctx) {
        Context c = (Context) visit(ctx.decl());
        if (c != null) {
            Util.getInstance().declareVar(c);
        }
        return null;
    }

    //<editor-fold defaultstate="collapsed" desc="decl">
    @Override
    public Object visitDeclSimple(CGrammarParser.DeclSimpleContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        return new PrimitiveContext(typeContext.getType(), false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclPointer(CGrammarParser.DeclPointerContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID().getSymbol());
    }

    @Override
    public Object visitDeclArray(CGrammarParser.DeclArrayContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context exprContext = (Context) visit(ctx.expr());
        if (exprContext != null && Util.getInstance().indexCheck(exprContext)) {
            return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID().getSymbol());
        }
        return null;
    }

    @Override
    public Object visitDeclValueSimple(CGrammarParser.DeclValueSimpleContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context exprContext = (Context) visit(ctx.expr());
        if (exprContext != null && Util.getInstance().declAtribCompatibilityCheck(typeContext, exprContext)) {
            return new PrimitiveContext(typeContext.getType(), false, ctx.ID().getSymbol());
        }
        return null;
    }

    @Override
    public Object visitDeclValuePointer(CGrammarParser.DeclValuePointerContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        typeContext = new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, typeContext.getToken());
        Context exprContext = (Context) visit(ctx.expr());
        if (exprContext != null && Util.getInstance().declAtribCompatibilityCheck(typeContext, exprContext)) {
            return new PointerContext(typeContext.getType(), false, ctx.ID().getSymbol());
        }
        return null;
    }

    @Override
    public Object visitDeclValueArrayList(CGrammarParser.DeclValueArrayListContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context exprContext;
        ArrayList<Context> funcArgsContext;
        if (ctx.expr() != null) {
            exprContext = (Context) visit(ctx.expr());
            if (Util.getInstance().constIndexCheck(exprContext)) {
                if (ctx.funcargs() != null) {
                    funcArgsContext = (ArrayList<Context>) visit(ctx.funcargs());
                    if (funcArgsContext != null && Util.getInstance().declAtribCompatibilityCheck(typeContext, funcArgsContext)) {
                        return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID().getSymbol());
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Object visitDeclValueArrayString(CGrammarParser.DeclValueArrayStringContext ctx) {
        Context exprContext;
        if (ctx.expr() != null) {
            exprContext = (Context) visit(ctx.expr());
            if (exprContext != null && Util.getInstance().constIndexCheck(exprContext)) {
                return new PointerContext(Type.POINTER_CHAR, false, ctx.ID().getSymbol());
            }
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="type">
    @Override
    public Object visitTypeInt(CGrammarParser.TypeIntContext ctx) {
        return new PrimitiveContext(Type.INT, true, ctx.INT().getSymbol());
    }

    @Override
    public Object visitTypeChar(CGrammarParser.TypeCharContext ctx) {
        return new PrimitiveContext(Type.CHAR, true, ctx.CHAR().getSymbol());
    }

    @Override
    public Object visitTypeDouble(CGrammarParser.TypeDoubleContext ctx) {
        return new PrimitiveContext(Type.DOUBLE, true, ctx.DOUBLE().getSymbol());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="funcargs">
    @Override
    public Object visitFuncargsCompose(CGrammarParser.FuncargsComposeContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        Context exprContext = (Context) visit(ctx.expr());
        if (exprContext != null) {
            args.add(exprContext);
            ArrayList<Context> funccargs = (ArrayList<Context>) visit(ctx.funcargs());
            if (funccargs != null) {
                args.addAll(funccargs);
                return args;
            }
        }
        return null;
    }

    @Override
    public Object visitFuncargsSingle(CGrammarParser.FuncargsSingleContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        Context exprContext = (Context) visit(ctx.expr());
        if (exprContext != null) {
            args.add(exprContext);
            return args;
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="expr">
    @Override
    public Object visitExprPlus(CGrammarParser.ExprPlusContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        Context termContext = (Context) visit(ctx.term());
        if (exprContext != null && termContext != null) {
            Boolean constant = (exprContext.getConstant() && termContext.getConstant());
            Integer newType = Util.getInstance().toUpperType(exprContext, termContext);
            if (newType != null) {
                return new PrimitiveContext(newType,
                        constant,
                        ctx.getToken(CGrammarLexer.SUM, 0).getSymbol()
                );
            }
        }
        return null;
    }

    @Override
    public Object visitExprMin(CGrammarParser.ExprMinContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        Context termContext = (Context) visit(ctx.term());
        if (exprContext != null && termContext != null) {
            Boolean constant = (exprContext.getConstant() && termContext.getConstant());
            Integer newType = Util.getInstance().toUpperType(exprContext, termContext);
            if (newType != null) {
                return new PrimitiveContext(newType,
                        constant,
                        ctx.getToken(CGrammarLexer.MIN, 0).getSymbol()
                );
            }
        }
        return null;
    }

    @Override
    public Object visitExprHided(CGrammarParser.ExprHidedContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        Context termContext = (Context) visit(ctx.term());
        if (exprContext != null && termContext != null) {
            Boolean constant = (exprContext.getConstant() && termContext.getConstant());
            Integer newType = Util.getInstance().toUpperType(exprContext, termContext);
            if (newType != null) {
                return new PrimitiveContext(newType,
                        constant,
                        exprContext.getToken()
                );
            }
        }
        return null;
    }

    @Override
    public Object visitExprTerm(CGrammarParser.ExprTermContext ctx) {
        return (Context) visit(ctx.term());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="term">
    @Override
    public Object visitTermMult(CGrammarParser.TermMultContext ctx) {
        Context termContext = (Context) visit(ctx.term());
        Context factContext = (Context) visit(ctx.fact());
        if (termContext != null && factContext != null) {
            Boolean constant = (termContext.getConstant() && factContext.getConstant());
            Integer newType = Util.getInstance().toUpperType(termContext, factContext);
            if (newType != null) {
                return new PrimitiveContext(newType,
                        constant,
                        ctx.getToken(CGrammarLexer.MULT, 0).getSymbol()
                );
            }
        }
        return null;
    }

    @Override
    public Object visitTermDiv(CGrammarParser.TermDivContext ctx) {
        Context termContext = (Context) visit(ctx.term());
        Context factContext = (Context) visit(ctx.fact());
        if (termContext != null && factContext != null) {
            Boolean constant = (termContext.getConstant() && factContext.getConstant());
            Integer newType = Util.getInstance().toUpperType(termContext, factContext);
            if (newType != null) {
                return new PrimitiveContext(newType,
                        constant,
                        ctx.getToken(CGrammarLexer.DIV, 0).getSymbol()
                );
            }
        }
        return null;
    }

    @Override
    public Object visitTermFact(CGrammarParser.TermFactContext ctx) {
        return visit(ctx.fact());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fact">
    @Override
    public Object visitFactNum(CGrammarParser.FactNumContext ctx) {
        return visit(ctx.num());
    }

    @Override
    public Object visitFactStr(CGrammarParser.FactStrContext ctx) {
        return new PointerContext(Type.POINTER_CHAR, true, ctx.STR().getSymbol());
    }

    @Override
    public Object visitFactChar(CGrammarParser.FactCharContext ctx) {
        return new PrimitiveContext(Type.CHAR, true, ctx.CHARC().getSymbol());
    }

    @Override
    public Object visitFactCall(CGrammarParser.FactCallContext ctx) {
        return visit(ctx.funccall());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="funccall">
    @Override
    public Object visitFunccallReal(CGrammarParser.FunccallRealContext ctx) {
        Context func = Util.getInstance().getContextFromTable(new PrimitiveContext(Type.FUNCTION_MARK, false, ctx.ID().getSymbol()));
        if (func != null) {
            ArrayList<Context> args;
            if (ctx.funcargs() != null) {
                args = (ArrayList<Context>) visit(ctx.funcargs());
            } else {
                args = new ArrayList<>();
            }
            if (args != null && Util.getInstance().functionCallCheck(func, args)) {
                return new FunctionContext(func.getType(), ctx.ID().getSymbol(), args);
            }
        }
        return null;
    }

    @Override
    public Object visitFuncIsolate(CGrammarParser.FuncIsolateContext ctx) {
        return visit(ctx.downfact());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="downfact">
    @Override
    public Object visitDownfactId(CGrammarParser.DownfactIdContext ctx) {
        Context var = Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
        if (var != null) {
            if (!(var instanceof FunctionContext)) {
                return Util.getInstance().createCorrectContextInstance(var, ctx.ID().getSymbol());
            }
            ArrayList<Object> args = new ArrayList<>();
            args.add(var);
            args.add(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
            Util.getInstance().error(ErrorType.FUNC_IS_CALLED_AS_VAR, args);
        }
        return null;
    }

    @Override
    public Object visitDownfactAdress(CGrammarParser.DownfactAdressContext ctx) {
        Context var = Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
        if (var != null) {
            if (!(var instanceof FunctionContext)) {
                return Util.getInstance().promoteContextType(var);
            }
            ArrayList<Object> args = new ArrayList<>();
            args.add(var);
            args.add(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
            Util.getInstance().error(ErrorType.FUNC_IS_CALLED_AS_VAR, args);
        }
        return null;
    }

    @Override
    public Object visitDownfactContent(CGrammarParser.DownfactContentContext ctx) {
        Context var = Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
        if (var != null) {
            if (!(var instanceof FunctionContext)) {
                return Util.getInstance().getContentContextOf(var);
            }
            ArrayList<Object> args = new ArrayList<>();
            args.add(var);
            args.add(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
            Util.getInstance().error(ErrorType.FUNC_IS_CALLED_AS_VAR, args);
        }
        return null;
    }

    @Override
    public Object visitDownfactArray(CGrammarParser.DownfactArrayContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        if (exprContext != null && Util.getInstance().indexCheck(exprContext)) {
            Context var = Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
            if (var != null) {
                if (!(var instanceof FunctionContext)) {
                    Context content = Util.getInstance().getContentContextOf(var);
                    if (content instanceof PointerContext) {
                        return Util.getInstance().getContentContextOf(content);
                    }
                    return content;
                }
                ArrayList<Object> args = new ArrayList<>();
                args.add(var);
                args.add(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
                Util.getInstance().error(ErrorType.FUNC_IS_CALLED_AS_VAR, args);
            }
        }
        return null;
    }

    @Override
    public Object visitDownfactExpr(CGrammarParser.DownfactExprContext ctx) {
        return visit(ctx.expr());
    }
    //</editor-fold>

}
