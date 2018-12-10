/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter;

import java.util.ArrayList;
import java.util.Scanner;
import parser.CGrammarBaseVisitor;
import parser.CGrammarLexer;
import parser.CGrammarParser;
import parser.Type;
import parser.context.Context;
import parser.context.FunctionContext;
import util.Util;
import parser.context.PointerContext;
import parser.context.PrimitiveContext;
import parser.context.Value;
import util.BlockResult;
import util.Call;
import util.CallStack;

/**
 *
 * @author rafael
 */
public class InterpreterVisitor extends CGrammarBaseVisitor<Object> {

    //private CallStack c = CallStack.getInstance();
    //<editor-fold defaultstate="collapsed" desc="global">
    @Override
    public Object visitGlobal(CGrammarParser.GlobalContext ctx) {
        Context declContext = (Context) visit(ctx.decl());
        if (!CallStack.getInstance().isthere(declContext.getToken().getText())) {
            Util.getInstance().declareVar(declContext);
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="num">
    @Override
    public Object visitNumInt(CGrammarParser.NumIntContext ctx) {
        return new PrimitiveContext(Type.INT, true, ctx.NUMINT().getSymbol(), Util.getInstance().stringIntConvertion(ctx.NUMINT().getText()));
    }

    @Override
    public Object visitNumDouble(CGrammarParser.NumDoubleContext ctx) {
        return new PrimitiveContext(Type.DOUBLE, true, ctx.NDOUBLE().getSymbol(), Util.getInstance().stringDoubleConvertion(ctx.NDOUBLE().getText()));
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
        return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID().getSymbol());
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
        return new PrimitiveContext(typeContext.getType(), false, ctx.ID().getSymbol(), exprContext.getValue().getRealValue());
    }

    @Override
    public Object visitDeclatribValuePointer(CGrammarParser.DeclatribValuePointerContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        Context id = Util.getInstance().getVar(new PrimitiveContext(Type.INT, false, ctx.ID(1).getSymbol()));
        if (ctx.ADRESS() != null) {
            return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID(0).getSymbol(), id.getValue());
        }
        return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), false, ctx.ID(0).getSymbol(), id);
    }

    @Override
    public Object visitDeclatribValueArrayString(CGrammarParser.DeclatribValueArrayStringContext ctx) {
        PointerContext p = new PointerContext(Type.POINTER_CHAR, false, ctx.ID().getSymbol());
        p.addPointValueListFromCharArray(ctx.STR().getText().toCharArray());
        return p;
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
        args.add((Context) visit(ctx.expr()));
        args.addAll((ArrayList<Context>) visit(ctx.funcargs()));
        return args;
    }

    @Override
    public Object visitFuncargsSingle(CGrammarParser.FuncargsSingleContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        args.add((Context) visit(ctx.expr()));
        return args;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="expr">
    @Override
    public Object visitExprPlus(CGrammarParser.ExprPlusContext ctx) {
        return Util.getInstance().sumOp((Context) visit(ctx.expr()), (Context) visit(ctx.term()));
    }

    @Override
    public Object visitExprMin(CGrammarParser.ExprMinContext ctx) {
        return Util.getInstance().minusOp((Context) visit(ctx.expr()), (Context) visit(ctx.term()));
    }

    @Override
    public Object visitExprHided(CGrammarParser.ExprHidedContext ctx) {
        return Util.getInstance().sumOp((Context) visit(ctx.expr()), (Context) visit(ctx.term()));
    }

    @Override
    public Object visitExprTerm(CGrammarParser.ExprTermContext ctx) {
        return visit(ctx.term());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="term">
    @Override
    public Object visitTermMult(CGrammarParser.TermMultContext ctx) {
        return Util.getInstance().multOp((Context) visit(ctx.term()), (Context) visit(ctx.fact()));
    }

    @Override
    public Object visitTermDiv(CGrammarParser.TermDivContext ctx) {
        return Util.getInstance().divOp((Context) visit(ctx.term()), (Context) visit(ctx.fact()));
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
        PointerContext p = new PointerContext(Type.POINTER_CHAR, true, ctx.STR().getSymbol());
        p.addPointValueListFromCharArray(ctx.STR().getText().toCharArray());
        return p;
    }

    @Override
    public Object visitFactChar(CGrammarParser.FactCharContext ctx) {
        return new PrimitiveContext(Type.CHAR, true, ctx.CHARC().getSymbol(), ctx.CHARC().getText().charAt(1));
    }

    @Override
    public Object visitFactCall(CGrammarParser.FactCallContext ctx) {
        return visit(ctx.funccall());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="funccall">
    @Override
    public Object visitFunccallReal(CGrammarParser.FunccallRealContext ctx) {
        return ((BlockResult) visit(ctx.funccallact())).getResult();
    }

    @Override
    public Object visitFuncIsolate(CGrammarParser.FuncIsolateContext ctx) {
        return visit(ctx.downfact());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="downfact">
    @Override
    public Object visitDownfactId(CGrammarParser.DownfactIdContext ctx) {
        return Util.getInstance().getVar(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
    }

    @Override
    public Object visitDownfactAdress(CGrammarParser.DownfactAdressContext ctx) {
        return Util.getInstance().promoteContextType(Util.getInstance().getVar(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol())));
    }

    @Override
    public Object visitDownfactContent(CGrammarParser.DownfactContentContext ctx) {
        return ((PointerContext) Util.getInstance().getVar(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()))).getPointValue(0);
    }

    @Override
    public Object visitDownfactArray(CGrammarParser.DownfactArrayContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        return ((PointerContext) Util.getInstance().getVar(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()))).getPointValue(((Number) exprContext.getValue().getRealValue()).intValue());
    }

    @Override
    public Object visitDownfactExpr(CGrammarParser.DownfactExprContext ctx) {
        return visit(ctx.expr());
    }
    //</editor-fold>    

    //<editor-fold defaultstate="collapsed" desc="function">
    @Override
    public Object visitFunction(CGrammarParser.FunctionContext ctx) {
        return visit(ctx.block());
    }
    //</editor-fold>   

    //<editor-fold defaultstate="collapsed" desc="param">
    @Override
    public Object visitParamCompose(CGrammarParser.ParamComposeContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        args.add((Context) visit(ctx.paramcomplx()));
        args.addAll((ArrayList<Context>) visit(ctx.param()));
        return args;
    }

    @Override
    public Object visitParamSingle(CGrammarParser.ParamSingleContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        args.add((Context) visit(ctx.paramcomplx()));
        return args;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="paramcomplx">
    @Override
    public Object visitParamByValue(CGrammarParser.ParamByValueContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        return new PrimitiveContext(typeContext.getType(), typeContext.getConstant(), ctx.ID().getSymbol());
    }

    @Override
    public Object visitParamByPointer(CGrammarParser.ParamByPointerContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), typeContext.getConstant(), ctx.ID().getSymbol());
    }

    @Override
    public Object visitParamArray(CGrammarParser.ParamArrayContext ctx) {
        Context typeContext = (Context) visit(ctx.type());
        return new PointerContext(Type.getIntTypeForPointer(typeContext.getType()), typeContext.getConstant(), ctx.ID().getSymbol());
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
        Util.getInstance().declareVar(c);
        return c;
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
        CallStack.getInstance().setCall(new Call(false));
        visit(ctx.forinit());
        while ((Boolean) visit(ctx.cond())) {
            CallStack.getInstance().setCall(new Call(false));
            BlockResult result = (BlockResult) visit(ctx.block());
            if (result.isBecauseOfReturn()) {
                CallStack.getInstance().deleteCall();
                CallStack.getInstance().deleteCall();
                return result;
            }
            visit(ctx.atrib());
            CallStack.getInstance().deleteCall();
        }
        CallStack.getInstance().deleteCall();
        return new PrimitiveContext(Type.INT, true, ctx.FOR().getSymbol());
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
        Util.getInstance().declareVar(var);
        return var;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dowhile">
    @Override
    public Object visitDowhile(CGrammarParser.DowhileContext ctx) {
        do {
            CallStack.getInstance().setCall(new Call(false));
            BlockResult result = (BlockResult) visit(ctx.block());
            if (result.isBecauseOfReturn()) {
                CallStack.getInstance().deleteCall();
                return result;
            }
            CallStack.getInstance().deleteCall();
        } while ((Boolean) visit(ctx.cond()));
        return new PrimitiveContext(Type.INT, true, ctx.DO().getSymbol());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="whilee">
    @Override
    public Object visitWhilee(CGrammarParser.WhileeContext ctx) {
        while ((Boolean) visit(ctx.cond())) {
            CallStack.getInstance().setCall(new Call(false));
            BlockResult result = (BlockResult) visit(ctx.block());
            if (result.isBecauseOfReturn()) {
                CallStack.getInstance().deleteCall();
                return result;
            }
            CallStack.getInstance().deleteCall();
        }
        return new PrimitiveContext(Type.INT, true, ctx.WHILE().getSymbol());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="swtstm">
    @Override
    public Object visitSwtstm(CGrammarParser.SwtstmContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        for (CGrammarParser.CasesContext t : ctx.cases()) {
            if (Util.getInstance().stringIntConvertion(t.getChild(1).getText()) == ((Integer) exprContext.getValue().getRealValue()).intValue()) {
                BlockResult result = (BlockResult) visit(t);
                if (result.isBecauseOfReturn()) {
                    CallStack.getInstance().deleteCall();
                    return result;
                }
            }
        }
        return new PrimitiveContext(Type.INT, true, ctx.SWITCH().getSymbol());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cases">
    @Override
    public Object visitCases(CGrammarParser.CasesContext ctx) {
        CallStack.getInstance().setCall(new Call(false));
        for (CGrammarParser.CmdContext t : ctx.cmd()) {
            Object result = visit(t);
            if (result instanceof BlockResult && ((BlockResult) result).isBecauseOfReturn()) {
                CallStack.getInstance().deleteCall();
                return result;
            }
        }
        CallStack.getInstance().deleteCall();
        return new PrimitiveContext(Type.INT, true, ctx.CASE().getSymbol());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ifstm">
    @Override
    public Object visitIfStm(CGrammarParser.IfStmContext ctx) {
        if ((Boolean) visit(ctx.cond())) {
            BlockResult result = (BlockResult) visit(ctx.block());
            if (result.isBecauseOfReturn()) {
                return result;
            }
        }
        return new PrimitiveContext(Type.INT, true, ctx.IF().getSymbol());
    }

    @Override
    public Object visitIfStmElse(CGrammarParser.IfStmElseContext ctx) {
        if ((Boolean) visit(ctx.cond())) {
            Object result = visit(ctx.block(0));
            if (result instanceof BlockResult) {
                if (((BlockResult) result).isBecauseOfReturn()) {
                    return result;
                }
            }
        } else {
            Object result = visit(ctx.block(1));
            if (result instanceof BlockResult) {
                if (((BlockResult) result).isBecauseOfReturn()) {
                    return result;
                }
            }
        }
        return new PrimitiveContext(Type.INT, true, ctx.IF().getSymbol());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="block">
    @Override
    public Object visitBlock(CGrammarParser.BlockContext ctx) {
        CallStack.getInstance().setCall(new Call(false));
        for (CGrammarParser.CmdContext t : ctx.cmd()) {
            Object result = visit(t);
            if (result instanceof BlockResult && ((BlockResult) result).isBecauseOfReturn()) {
                CallStack.getInstance().deleteCall();
                return result;
            }
        }
        CallStack.getInstance().deleteCall();
        return new PrimitiveContext(Type.INT, true, ctx.getToken(CGrammarLexer.OPB, 0).getSymbol());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="funccallact">
    @Override
    public Object visitFunccallact(CGrammarParser.FunccallactContext ctx) {
        FunctionContext func = (FunctionContext) Util.getInstance().getFuncFromTable(new PrimitiveContext(Type.FUNCTION_MARK, false, ctx.ID().getSymbol()));
        ArrayList<Context> params;
        if (ctx.funcargs() != null) {
            params = (ArrayList<Context>) visit(ctx.funcargs());
        } else {
            params = new ArrayList<>();
        }
        ArrayList<Context> args = (ArrayList<Context>) func.getValue().getRealValue();
        ArrayList<Context> newArgs = new ArrayList<>();
        for (int c = 0; c < args.size(); c++) {
            Context aux = Util.getInstance().createCorrectContextInstance(args.get(c), args.get(c).getToken());
            aux.setValue(params.get(c).getValue());
            newArgs.add(aux);
        }
        Util.getInstance().putFuncInStack(true);
        Util.getInstance().declareMultVar(newArgs);
        Object result = visit(func.getTreeNode());
        CallStack.getInstance().deleteCall();
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="retrn">
    @Override
    public Object visitRetrn(CGrammarParser.RetrnContext ctx) {
        return new BlockResult(true, visit(ctx.expr()));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="scan">
    @Override
    public Object visitScan(CGrammarParser.ScanContext ctx) {
        ArrayList<Context> params = Util.getInstance().extractScanfParams(new PointerContext(Type.POINTER_CHAR, true, ctx.STR().getSymbol()));
        ArrayList<Context> realArgs = (ArrayList<Context>) visit(ctx.scanargs());
        if (params.isEmpty()) {
            return new FunctionContext(Type.FUNCTION_MARK, ctx.SCANF().getSymbol());
        }
        Scanner s = new Scanner(System.in);
        for (Context t : params) {
            Object input = null;
            switch (t.getType()) {
                case Type.POINTER_INT: {
                    //Integer n = Util.getInstance().stringIntConvertion(s.nextLine());
                    input = s.nextInt();
                    ((PointerContext) realArgs.get(params.indexOf(t))).addPointRealValue(input, 0);
                    break;
                }
                case Type.POINTER_DOUBLE: {
                    input = s.nextDouble();
                    ((PointerContext) realArgs.get(params.indexOf(t))).addPointRealValue(input, 0);
                    break;
                }
                case Type.POINTER_CHAR: {
                    input = s.nextLine();
                    ((PointerContext) realArgs.get(params.indexOf(t))).addPointValueListFromCharArray(((String) input).toCharArray());
                    break;
                }
            }
        }
        return new PrimitiveContext(Type.INT, true, ctx.SCANF().getSymbol());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="scanargs">
    @Override
    public Object visitScanargsCompose(CGrammarParser.ScanargsComposeContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        Context scanArgContext = (Context) visit(ctx.scanargstype());
        args.add(scanArgContext);
        ArrayList<Context> argsParams = (ArrayList<Context>) visit(ctx.scanargs());
        args.addAll(argsParams);
        return args;
    }

    @Override
    public Object visitScanargsSingle(CGrammarParser.ScanargsSingleContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        Context scanArgContext = (Context) visit(ctx.scanargstype());
        args.add(scanArgContext);
        return args;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="scanargstype">
    @Override
    public Object visitScanargstypeAddress(CGrammarParser.ScanargstypeAddressContext ctx) {
        return Util.getInstance().promoteContextType(Util.getInstance().getVar(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol())));
    }

    @Override
    public Object visitScanargstypeId(CGrammarParser.ScanargstypeIdContext ctx) {
        return Util.getInstance().getVar(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
    }

    @Override
    public Object visitScanargstypeAddressArray(CGrammarParser.ScanargstypeAddressArrayContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        Context var = Util.getInstance().getVar(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
        return new PointerContext(var.getType(), var.getConstant(), var.getToken(), ((PointerContext) var).getPointValue((Integer) exprContext.getValue().getRealValue()));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="print">
    @Override
    public Object visitPrintSimple(CGrammarParser.PrintSimpleContext ctx) {
        System.out.println(ctx.STR().getText());
        return new FunctionContext(Type.FUNCTION_MARK, ctx.PRINTF().getSymbol());
    }

    @Override
    public Object visitPrintComplex(CGrammarParser.PrintComplexContext ctx) {
        ArrayList<Context> realArgs = (ArrayList<Context>) visit(ctx.printargs());
        String output = ctx.STR().getText();
        for (Context t : realArgs) {
            if (t.getValue() != null) {
                if (t instanceof PrimitiveContext) {
                    output = output.replaceFirst("(%d)|(%f)|(%c)|(%s)", t.getValue().getRealValue().toString());
                } else {
                    output = output.replaceFirst("(%d)|(%f)|(%c)|(%s)", ((PointerContext) t).getPointRealValue(0).toString());
                }
            } else {
                output = output.replaceFirst("(%d)|(%f)|(%c)|(%s)", "");
            }
        }
        System.out.println(output.substring(1, output.length() - 1));
        return new FunctionContext(Type.FUNCTION_MARK, ctx.PRINTF().getSymbol());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="printargs">
    @Override
    public Object visitPrintargsCompose(CGrammarParser.PrintargsComposeContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        Context exprContext = (Context) visit(ctx.expr());
        args.add(exprContext);
        ArrayList<Context> argsParams = (ArrayList<Context>) visit(ctx.printargs());
        args.addAll(argsParams);
        return args;
    }

    @Override
    public Object visitPrintargsSingle(CGrammarParser.PrintargsSingleContext ctx) {
        ArrayList<Context> args = new ArrayList<>();
        Context exprContext = (Context) visit(ctx.expr());
        args.add(exprContext);
        return args;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="atrib">
    @Override

    public Object visitAtribSimple(CGrammarParser.AtribSimpleContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        Context var = Util.getInstance().getVar(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
        if (var instanceof PointerContext) {
            var.setValue(exprContext.getValue());
        } else {
            var.getValue().setRealValue(exprContext.getValue().getRealValue());
        }
        return var;
    }

    @Override
    public Object visitAtribPointer(CGrammarParser.AtribPointerContext ctx) {
        Context exprContext = (Context) visit(ctx.expr());
        Context var = Util.getInstance().getVar(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));        
        ((PointerContext)var).addPointRealValue(exprContext.getValue().getRealValue(), 0);
        return var;
    }

    @Override
    public Object visitAtribArray(CGrammarParser.AtribArrayContext ctx) {
        Context indexContext = (Context) visit(ctx.expr(0));
        Context exprContext = (Context) visit(ctx.expr(1));
        PointerContext var = (PointerContext) Util.getInstance().getVar(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
        var.addPointRealValue(exprContext.getValue().getRealValue(), (Integer) indexContext.getValue().getRealValue());
        return var;
    }

    @Override
    public Object visitAtribPlusPlus(CGrammarParser.AtribPlusPlusContext ctx) {
        Context var = Util.getInstance().getVar(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
        var.getValue().setRealValue(Util.getInstance().sumOp(var, new PrimitiveContext(Type.INT, true, ctx.ID().getSymbol(), 1)).getValue().getRealValue());
        return var;
    }

    @Override
    public Object visitAtribMinusMinus(CGrammarParser.AtribMinusMinusContext ctx) {
        Context var = Util.getInstance().getVar(new PrimitiveContext(Type.INT, false, ctx.ID().getSymbol()));
        var.getValue().setRealValue(Util.getInstance().minusOp(var, new PrimitiveContext(Type.INT, true, ctx.ID().getSymbol(), 1)).getValue().getRealValue());
        return var;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cond">
    @Override
    public Object visitCondOr(CGrammarParser.CondOrContext ctx) {
        return ((Boolean) visit(ctx.cond()) || (Boolean) visit(ctx.cdand()));
    }

    @Override
    public Object visitCondAnd(CGrammarParser.CondAndContext ctx) {
        return visit(ctx.cdand());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cdand">
    @Override
    public Object visitCdandAnd(CGrammarParser.CdandAndContext ctx) {
        return ((Boolean) visit(ctx.cdand()) && (Boolean) visit(ctx.cndts()));
    }

    @Override
    public Object visitCdandCndts(CGrammarParser.CdandCndtsContext ctx) {
        return visit(ctx.cndts());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cndts">
    @Override
    public Object visitCndtsRelop(CGrammarParser.CndtsRelopContext ctx) {
        Integer op = (Integer) visit(ctx.relop());
        Context expr1 = (Context) visit(ctx.expr(0));
        Context expr2 = (Context) visit(ctx.expr(1));
        if (op == CGrammarLexer.MOR) {
            return (((Number) expr1.getValue().getRealValue()).doubleValue() > ((Number) expr2.getValue().getRealValue()).doubleValue());
        }
        if (op == CGrammarLexer.LESS) {
            return (((Number) expr1.getValue().getRealValue()).doubleValue() < ((Number) expr2.getValue().getRealValue()).doubleValue());
        }
        if (op == CGrammarLexer.MOREQ) {
            return (((Number) expr1.getValue().getRealValue()).doubleValue() >= ((Number) expr2.getValue().getRealValue()).doubleValue());
        }
        if (op == CGrammarLexer.LESSEQ) {
            return (((Number) expr1.getValue().getRealValue()).doubleValue() <= ((Number) expr2.getValue().getRealValue()).doubleValue());
        }
        if (op == CGrammarLexer.EQ) {
            return (((Number) expr1.getValue().getRealValue()).doubleValue() == ((Number) expr2.getValue().getRealValue()).doubleValue());
        }
        return (((Number) expr1.getValue().getRealValue()).doubleValue() != ((Number) expr2.getValue().getRealValue()).doubleValue());
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
