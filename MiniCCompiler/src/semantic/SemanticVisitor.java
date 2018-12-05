/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semantic;

import java.util.ArrayList;
import org.antlr.v4.runtime.tree.TerminalNode;
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

    //<editor-fold defaultstate="collapsed" desc="global">
    @Override
    public Object visitGlobal(CGrammarParser.GlobalContext ctx) {
        Context c = (Context) visit(ctx.decl());
        Util.getInstance().setCurrentFuncName(null);
        if (c != null) {
            Util.getInstance().declareVar(c);
        }
        return null;
    }
    //</editor-fold>

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
    public Object visitDeclDeclatrib(CGrammarParser.DeclDeclatribContext ctx) {
        return visit(ctx.declatrib());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="declatrib">
    @Override
    public Object visitDeclatribValueSimple(CGrammarParser.DeclatribValueSimpleContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context exprContext = (Context) visit(ctx.expr());
        if (exprContext != null && Util.getInstance().declAtribCompatibilityCheck(typeContext, exprContext)) {
            return new PrimitiveContext(typeContext.getType(), false, ctx.ID().getSymbol());
        }
        return null;
    }

    @Override
    public Object visitDeclatribValuePointer(CGrammarParser.DeclatribValuePointerContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        typeContext = new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, typeContext.getToken());
        Context id = Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID(1).getSymbol()));
        if (id != null) {
            if (ctx.ADRESS() != null) {
                id = Util.getInstance().promoteContextType(id);
            }
            if (Util.getInstance().declAtribCompatibilityCheck(typeContext, id)) {
                return new PointerContext(typeContext.getType(), false, ctx.ID(0).getSymbol());
            }
        }
        return null;
    }

    @Override
    public Object visitDeclatribValueArrayList(CGrammarParser.DeclatribValueArrayListContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context exprContext;
        ArrayList<Context> funcArgsContext;
        if (ctx.expr() != null) {
            exprContext = (Context) visit(ctx.expr());
            if (exprContext != null && !Util.getInstance().constIndexCheck(exprContext)) {
                return null;
            }
        }
        funcArgsContext = (ArrayList<Context>) visit(ctx.funcargs());
        if (funcArgsContext != null && Util.getInstance().declAtribCompatibilityCheck(typeContext, funcArgsContext)) {
            return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID().getSymbol());
        }
        return null;
    }

    @Override
    public Object visitDeclatribValueArrayString(CGrammarParser.DeclatribValueArrayStringContext ctx) {
        Context exprContext;
        if (ctx.expr() != null) {
            exprContext = (Context) visit(ctx.expr());
            if (exprContext != null && !Util.getInstance().constIndexCheck(exprContext)) {
                return null;
            }
        }
        return new PointerContext(Type.POINTER_CHAR, false, ctx.ID().getSymbol());
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
        return visit(ctx.funccallact());
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

    //<editor-fold defaultstate="collapsed" desc="function">
    @Override
    public Object visitFunction(CGrammarParser.FunctionContext ctx) {
        Context returnType = (Context) visit(ctx.returntype());
        ArrayList<Context> params;
        if (ctx.param() != null) {
            params = (ArrayList<Context>) visit(ctx.param());
        } else {
            params = new ArrayList<>();
        }
        Context auxVar = new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol());
        if (Util.getInstance().doesGlobalContextExist(auxVar)) {
            ArrayList<Object> args = new ArrayList<>();
            args.add(auxVar);
            args.add(Util.getInstance().getContextFromTable(auxVar));
            Util.getInstance().error(ErrorType.SYMB_ALREADY_EXISTS, args);
            return null;
        }
        Util.getInstance().setCurrentFuncName(ctx.ID().getText());
        Util.getInstance().declareFuncInTable(ctx.ID().getText(), new FunctionContext(returnType.getType(), ctx.ID().getSymbol(), params));
        Util.getInstance().declareMultVar(params);
        if (ctx.cmd() != null) {
            Boolean checked = true, isRtnThere = false;
            for (CGrammarParser.CmdContext t : ctx.cmd()) {
                if (t.getChild(0) instanceof CGrammarParser.RetrnContext) {
                    isRtnThere = true;
                }
                if ((Context) visit(t) == null) {
                    checked = false;
                    break;
                }
            }
            if (!isRtnThere && returnType.getType() != Type.VOID) {
                ArrayList<Object> args = new ArrayList<>();
                args.add(auxVar);
                args.add(returnType);
                Util.getInstance().error(ErrorType.FUNC_WITHOUT_RETURN, args);
                return null;
            }
            if (!checked) {
                SemanticTable.getInstance().getGlobalSymbolTable().deleteSymbol(ctx.ID().getText());
                SemanticTable.getInstance().deleteTable(ctx.ID().getText());
                Util.getInstance().setCurrentFuncName(null);
                return null;
            }
        }
        return returnType;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="returntype">
    @Override
    public Object visitReturnType(CGrammarParser.ReturnTypeContext ctx) {
        return visit(ctx.type());
    }

    @Override
    public Object visitReturnVoid(CGrammarParser.ReturnVoidContext ctx) {
        return new PrimitiveContext(Type.VOID, true, ctx.VOID().getSymbol());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="param">
    @Override
    public Object visitParamCompose(CGrammarParser.ParamComposeContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        Context paramContext = (Context) visit(ctx.paramcomplx());
        if (paramContext != null) {
            args.add(paramContext);
            ArrayList<Context> params = (ArrayList<Context>) visit(ctx.param());
            if (params != null) {
                args.addAll(params);
                return args;
            }
        }
        return null;
    }

    @Override
    public Object visitParamSingle(CGrammarParser.ParamSingleContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        Context param = (Context) visit(ctx.paramcomplx());
        if (param != null) {
            args.add(param);
            return args;
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="paramcomplx">
    @Override
    public Object visitParamByValue(CGrammarParser.ParamByValueContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context auxVar = new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol());
        if (Util.getInstance().doesGlobalContextExist(auxVar)) {
            ArrayList<Object> args = new ArrayList<>();
            args.add(auxVar);
            args.add(Util.getInstance().getContextFromTable(auxVar));
            Util.getInstance().error(ErrorType.SYMB_ALREADY_EXISTS, args);
            return null;
        }
        return new PrimitiveContext(typeContext.getType(), typeContext.getConstant(), auxVar.getToken());
    }

    @Override
    public Object visitParamByPointer(CGrammarParser.ParamByPointerContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context auxVar = new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol());
        if (Util.getInstance().doesGlobalContextExist(auxVar)) {
            ArrayList<Object> args = new ArrayList<>();
            args.add(auxVar);
            args.add(Util.getInstance().getContextFromTable(auxVar));
            Util.getInstance().error(ErrorType.SYMB_ALREADY_EXISTS, args);
            return null;
        }
        return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), typeContext.getConstant(), auxVar.getToken());
    }

    @Override
    public Object visitParamArray(CGrammarParser.ParamArrayContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context auxVar = new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol());
        if (Util.getInstance().doesGlobalContextExist(auxVar)) {
            ArrayList<Object> args = new ArrayList<>();
            args.add(auxVar);
            args.add(Util.getInstance().getContextFromTable(auxVar));
            Util.getInstance().error(ErrorType.SYMB_ALREADY_EXISTS, args);
            return null;
        }
        return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), typeContext.getConstant(), auxVar.getToken());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cmd">
    @Override
    public Object visitCmdAtrib(CGrammarParser.CmdAtribContext ctx) {
        return visit(ctx.atrib());
    }

    @Override
    public Object visitCmdWrite(CGrammarParser.CmdWriteContext ctx) {
        return visit(ctx.print());
    }

    @Override
    public Object visitCmdRead(CGrammarParser.CmdReadContext ctx) {
        return visit(ctx.scan());
    }

    @Override
    public Object visitCmdDecl(CGrammarParser.CmdDeclContext ctx) {
        Context c = (Context) visit(ctx.decl());
        if (c != null) {
            if (Util.getInstance().declareVar(c)) {
                return Util.getInstance().getContextFromTable(c);
            }
        }
        return null;
    }

    @Override
    public Object visitCmdReturn(CGrammarParser.CmdReturnContext ctx) {
        return visit(ctx.retrn());
    }

    @Override
    public Object visitCmdFunccall(CGrammarParser.CmdFunccallContext ctx) {
        return visit(ctx.funccallact());
    }

    @Override
    public Object visitCmdIf(CGrammarParser.CmdIfContext ctx) {
        return visit(ctx.ifstm());
    }

    @Override
    public Object visitCmdswitch(CGrammarParser.CmdswitchContext ctx) {
        return visit(ctx.swtstm());
    }

    @Override
    public Object visitCmdWhile(CGrammarParser.CmdWhileContext ctx) {
        return visit(ctx.whilee());
    }

    @Override
    public Object visitCmdDoWhile(CGrammarParser.CmdDoWhileContext ctx) {
        return visit(ctx.dowhile());
    }

    @Override
    public Object visitCmdFor(CGrammarParser.CmdForContext ctx) {
        return visit(ctx.forr());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="forr">
    @Override
    public Object visitForr(CGrammarParser.ForrContext ctx) {
        Context initContext = (Context) visit(ctx.forinit());
        Context condContext = (Context) visit(ctx.cond());
        Context atribContext = (Context) visit(ctx.atrib());
        Context blockContext = (Context) visit(ctx.block());
        if (initContext != null && condContext != null && atribContext != null && blockContext != null) {
            return condContext;
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="forinit">
    @Override
    public Object visitForAtrib(CGrammarParser.ForAtribContext ctx) {
        return visit(ctx.atrib());
    }

    @Override
    public Object visitForDeclatrib(CGrammarParser.ForDeclatribContext ctx) {
        Context var = (Context) visit(ctx.declatrib());
        if (var != null) {
            Util.getInstance().declareVar(var);
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dowhile">
    @Override
    public Object visitDowhile(CGrammarParser.DowhileContext ctx) {
        Context condContext = (Context) visit(ctx.cond());
        if (condContext != null && visit(ctx.block()) != null) {
            return condContext;
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="whilee">
    @Override
    public Object visitWhilee(CGrammarParser.WhileeContext ctx) {
        Context condContext = (Context) visit(ctx.cond());
        if (condContext != null && visit(ctx.block()) != null) {
            return condContext;
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="swtstm">
    @Override
    public Object visitSwtstm(CGrammarParser.SwtstmContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        if (exprContext != null && Util.getInstance().indexCheck(exprContext)) {
            if (ctx.cases() == null && ctx.dfault() == null) {
                return exprContext;
            }
            if (ctx.cases() != null) {
                for (CGrammarParser.CasesContext t : ctx.cases()) {
                    if (visit(t) == null) {
                        return null;
                    }
                }
                return exprContext;
            }
            if (visit(ctx.dfault()) != null) {
                return exprContext;
            }
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cases">
    @Override
    public Object visitCaseSimple(CGrammarParser.CaseSimpleContext ctx) {
        if (ctx.cmd() != null) {
            for (CGrammarParser.CmdContext t : ctx.cmd()) {
                if (visit(t) == null) {
                    return null;
                }
            }
        }
        return new PrimitiveContext(Type.INT, true, ctx.CASE().getSymbol());
    }

    @Override
    public Object visitCaseBlock(CGrammarParser.CaseBlockContext ctx) {
        if (ctx.cmd() != null) {
            for (CGrammarParser.CmdContext t : ctx.cmd()) {
                if (visit(t) == null) {
                    return null;
                }
            }
        }
        return new PrimitiveContext(Type.INT, true, ctx.CASE().getSymbol());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dfault">
    @Override
    public Object visitDefaultSimple(CGrammarParser.DefaultSimpleContext ctx) {
        if (ctx.cmd() != null) {
            for (CGrammarParser.CmdContext t : ctx.cmd()) {
                if (visit(t) == null) {
                    return null;
                }
            }
        }
        return new PrimitiveContext(Type.INT, true, ctx.DEFAULT().getSymbol());
    }

    @Override
    public Object visitDefaultBlock(CGrammarParser.DefaultBlockContext ctx) {
        if (ctx.cmd() != null) {
            for (CGrammarParser.CmdContext t : ctx.cmd()) {
                if (visit(t) == null) {
                    return null;
                }
            }
        }
        return new PrimitiveContext(Type.INT, true, ctx.DEFAULT().getSymbol());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ifstm">
    @Override
    public Object visitIfStm(CGrammarParser.IfStmContext ctx) {
        Context condContext = (Context) visit(ctx.cond());
        if (condContext != null && visit(ctx.block()) != null) {
            // apenas para manter o padrão object/null
            return condContext;
        }
        return null;
    }

    @Override
    public Object visitIfStmElse(CGrammarParser.IfStmElseContext ctx) {
        Context condContext = (Context) visit(ctx.cond());
        if (condContext != null && visit(ctx.block(0)) != null && visit(ctx.block(1)) != null) {
            // apenas para manter o padrão object/null
            return condContext;
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="block">
    @Override
    public Object visitBlockCompose(CGrammarParser.BlockComposeContext ctx) {
        if (ctx.cmd() != null) {
            for (CGrammarParser.CmdContext t : ctx.cmd()) {
                if (visit(t) == null) {
                    return null;
                }
            }
        }
        // manter padrão object/null
        return new PointerContext(Type.POINTER_CHAR, true, ctx.OPB().getSymbol());
    }

    @Override
    public Object visitBlockSingle(CGrammarParser.BlockSingleContext ctx) {
        if (ctx.cmd() != null) {
            return visit(ctx.cmd());
        }
        // manter padrão object/null -> impossível gerar token
        return true;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="funccallact">
    @Override
    public Object visitFunccallact(CGrammarParser.FunccallactContext ctx) {
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="retrn">
    @Override
    public Object visitRetrn(CGrammarParser.RetrnContext ctx) {
        return visit(ctx.expr());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="scan">
    @Override
    public Object visitScan(CGrammarParser.ScanContext ctx) {
        ArrayList<Context> params = Util.getInstance().extractScanfParams(new PointerContext(Type.POINTER_CHAR, true, ctx.STR().getSymbol()));
        ArrayList<Context> realArgs = (ArrayList<Context>) visit(ctx.scanargs());
        if (params.isEmpty()) {
            ArrayList<Object> args = new ArrayList<>();
            args.add(new PointerContext(Type.POINTER_CHAR, true, ctx.SCANF().getSymbol()));
            Util.getInstance().error(ErrorType.SCANF_ARGS_DO_NOT_EXIST, args);
            return null;
        }
        if (params.size() != realArgs.size()) {
            ArrayList<Object> args = new ArrayList<>();
            args.add(new PointerContext(Type.POINTER_CHAR, true, ctx.SCANF().getSymbol()));
            args.add(params.size());
            args.add(realArgs.size());
            Util.getInstance().error(ErrorType.SCANF_ARGS_INSUFFICIENT, args);
            return null;
        }
        if (Util.getInstance().printfArgs(params, realArgs)) {
            return new FunctionContext(Type.FUNCTION_MARK, ctx.SCANF().getSymbol());
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="scanargs">
    @Override
    public Object visitScanargsCompose(CGrammarParser.ScanargsComposeContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        Context scanArgContext = (Context) visit(ctx.scanargstype());
        if (scanArgContext != null) {
            args.add(scanArgContext);
            ArrayList<Context> argsParams = (ArrayList<Context>) visit(ctx.scanargs());
            if (argsParams != null) {
                args.addAll(argsParams);
                return args;
            }
        }
        return null;
    }

    @Override
    public Object visitScanargsSingle(CGrammarParser.ScanargsSingleContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        Context scanArgContext = (Context) visit(ctx.scanargstype());
        if (scanArgContext != null) {
            args.add(scanArgContext);
            return args;
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="scanargstype">
    @Override
    public Object visitScanargstypeAddress(CGrammarParser.ScanargstypeAddressContext ctx) {
        Context var = Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
        if (var != null) {
            return Util.getInstance().promoteContextType(var);
        }
        return null;
    }

    @Override
    public Object visitScanargstypeId(CGrammarParser.ScanargstypeIdContext ctx) {
        return Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
    }

    @Override
    public Object visitScanargstypeAddressArray(CGrammarParser.ScanargstypeAddressArrayContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        if (exprContext != null && Util.getInstance().indexCheck(exprContext)) {
            Context var = Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
            if (var != null) {
                return Util.getInstance().createCorrectContextInstance(var, ctx.ID().getSymbol());
            }
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="print">
    @Override
    public Object visitPrintSimple(CGrammarParser.PrintSimpleContext ctx) {
        ArrayList<Context> params = Util.getInstance().extractPrintfParams(new PointerContext(Type.POINTER_CHAR, true, ctx.STR().getSymbol()));
        if (!params.isEmpty()) {
            ArrayList<Object> args = new ArrayList<>();
            args.add(new PointerContext(Type.POINTER_CHAR, true, ctx.PRINTF().getSymbol()));
            Util.getInstance().error(ErrorType.PRINTF_ARGS_UNEXPECTED, args);
            return new FunctionContext(Type.FUNCTION_MARK, ctx.PRINTF().getSymbol());
        }
        return null;
    }

    @Override
    public Object visitPrintComplex(CGrammarParser.PrintComplexContext ctx) {
        ArrayList<Context> params = Util.getInstance().extractPrintfParams(new PointerContext(Type.POINTER_CHAR, true, ctx.STR().getSymbol()));
        ArrayList<Context> realArgs = (ArrayList<Context>) visit(ctx.printargs());
        if (params != null && realArgs != null) {
            if (params.isEmpty() || !(params.size() == realArgs.size())) {
                ArrayList<Object> args = new ArrayList<>();
                args.add(new PointerContext(Type.POINTER_CHAR, true, ctx.PRINTF().getSymbol()));
                args.add(params.size());
                args.add(realArgs.size());
                Util.getInstance().error(ErrorType.PRINTF_ARGS_INSUFFICIENT, args);
                return null;
            }
            if (Util.getInstance().printfArgs(params, realArgs)) {
                return new FunctionContext(Type.FUNCTION_MARK, ctx.PRINTF().getSymbol());
            }
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="printargs">
    @Override
    public Object visitPrintargsCompose(CGrammarParser.PrintargsComposeContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        Context exprContext = (Context) visit(ctx.expr());
        if (exprContext != null) {
            args.add(exprContext);
            ArrayList<Context> argsParams = (ArrayList<Context>) visit(ctx.printargs());
            if (argsParams != null) {
                args.addAll(argsParams);
                return args;
            }
        }
        return null;
    }

    @Override
    public Object visitPrintargsSingle(CGrammarParser.PrintargsSingleContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        Context exprContext = (Context) visit(ctx.expr());
        if (exprContext != null) {
            args.add(exprContext);
            return args;
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="atrib">
    @Override

    public Object visitAtribSimple(CGrammarParser.AtribSimpleContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        Context var = Util.getInstance().getContextFromTable(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
        if (exprContext != null && var != null && Util.getInstance().declAtribCompatibilityCheck(var, exprContext)) {
            var = Util.getInstance().getContextFromTable(var);
            return Util.getInstance().createCorrectContextInstance(var, ctx.ID().getSymbol());
        }
        return null;
    }

    @Override
    public Object visitAtribPointer(CGrammarParser.AtribPointerContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        Context var = new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol());
        if (exprContext != null && Util.getInstance().declAtribCompatibilityCheck(
                Util.getInstance().getContentContextOf(Util.getInstance().getContextFromTable(var)),
                exprContext)) {
            var = Util.getInstance().getContentContextOf(Util.getInstance().getContextFromTable(var));
            return Util.getInstance().createCorrectContextInstance(var, ctx.ID().getSymbol());
        }
        return null;
    }

    @Override
    public Object visitAtribArray(CGrammarParser.AtribArrayContext ctx) {
        Context indexContext = (Context) visit(ctx.expr(0));
        Context exprContext = (Context) visit(ctx.expr(1));
        Context var = new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol());
        if (indexContext != null
                && exprContext != null
                && Util.getInstance().getContentContextOf(Util.getInstance().getContextFromTable(var)) != null) {
            var = Util.getInstance().getContentContextOf(Util.getInstance().getContextFromTable(var));
            if (Util.getInstance().indexCheck(indexContext) && Util.getInstance().declAtribCompatibilityCheck(var, exprContext)) {
                return Util.getInstance().createCorrectContextInstance(var, ctx.ID().getSymbol());
            }
        }
        return null;
    }

    @Override
    public Object visitAtribPlusPlus(CGrammarParser.AtribPlusPlusContext ctx) {
        Context var = new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol());
        if ((var = Util.getInstance().getContextFromTable(var)) != null && Util.getInstance().MathOpCompatibilityCheck(var, new PrimitiveContext(Type.INT, true, ctx.getToken(CGrammarLexer.SUM, 0).getSymbol()))) {
            return Util.getInstance().createCorrectContextInstance(var, ctx.ID().getSymbol());
        }
        return null;
    }

    @Override
    public Object visitAtribMinusMinus(CGrammarParser.AtribMinusMinusContext ctx) {
        Context var = new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol());
        if ((var = Util.getInstance().getContextFromTable(var)) != null && Util.getInstance().MathOpCompatibilityCheck(var, new PrimitiveContext(Type.INT, true, ctx.getToken(CGrammarLexer.SUM, 0).getSymbol()))) {
            return Util.getInstance().createCorrectContextInstance(var, ctx.ID().getSymbol());
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cond">
    @Override
    public Object visitCondOr(CGrammarParser.CondOrContext ctx) {
        if (visit(ctx.cond()) != null && visit(ctx.cdand()) != null) {
            return new PrimitiveContext(Type.INT, true, ctx.OR().getSymbol());
        }
        return null;
    }

    @Override
    public Object visitCondAnd(CGrammarParser.CondAndContext ctx) {
        return visit(ctx.cdand());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cdand">
    @Override
    public Object visitCdandAnd(CGrammarParser.CdandAndContext ctx) {
        if (visit(ctx.cdand()) != null && visit(ctx.cndts()) != null) {
            return new PrimitiveContext(Type.INT, true, ctx.AND().getSymbol());
        }
        return null;
    }

    @Override
    public Object visitCdandCndts(CGrammarParser.CdandCndtsContext ctx) {
        return visit(ctx.cndts());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cndts">
    @Override
    public Object visitCndtsExpr(CGrammarParser.CndtsExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Object visitCndtsRelop(CGrammarParser.CndtsRelopContext ctx) {
        Integer op = (Integer) visit(ctx.relop());
        Context expr1 = (Context) visit(ctx.expr(0));
        Context expr2 = (Context) visit(ctx.expr(1));
        if (expr1 != null && expr2 != null && Util.getInstance().declAtribCompatibilityCheck(expr1, expr2)) {
            return expr1;
        }
        return null;
    }

    @Override
    public Object visitCndtsNotExprWithout(CGrammarParser.CndtsNotExprWithoutContext ctx) {
        return visit(ctx.cndts());
    }

    @Override
    public Object visitCndtsCond(CGrammarParser.CndtsCondContext ctx) {
        return visit(ctx.cond());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="relop">
    @Override
    public Object visitRelop(CGrammarParser.RelopContext ctx) {
        if (ctx.MOR() != null) {
            return CGrammarLexer.MOR;
        }
        if (ctx.LESS() != null) {
            return CGrammarLexer.LESS;
        }
        if (ctx.MOREQ() != null) {
            return CGrammarLexer.MOREQ;
        }
        if (ctx.LESSEQ() != null) {
            return CGrammarLexer.LESSEQ;
        }
        if (ctx.EQ() != null) {
            return CGrammarLexer.EQ;
        }
        return CGrammarLexer.NEQ;
    }
    //</editor-fold>    

}
