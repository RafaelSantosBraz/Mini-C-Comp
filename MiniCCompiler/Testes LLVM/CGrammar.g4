grammar CGrammar;

@header{
    package parser;
}

prog            : (global|function)+
                ;

                //cabeçalho
                    //; ModuleID = '/home/henrique/Documentos/code.c'
                    //source_filename = "/home/henrique/Documentos/code.c"
                    //target datalayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
                    //target triple = "x86_64-unknown-linux-gnu"

                //códigos

                //rodapé
                    //attributes #0 = { noinline nounwind optnone uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
                    //
                    //!llvm.module.flags = !{!0}
                    //!llvm.ident = !{!1}
                    //
                    //!0 = !{i32 1, !"wchar_size", i32 4}
                    //!1 = !{!"clang version 7.0.0 (tags/RELEASE_700/final)"}
                    //
                    //!0 = !{i32 1, !"wchar_size", i32 4}
                    //!1 = !{!"clang version 7.0.0 (tags/RELEASE_700/final)"}


global          : decl EOL //%**id** = alloca **tipo de dados**, align **tamanho_do_tipodedado**
                ;

                //id
                    //numero diferente para cada variavel

                //tipos de dados
                    //Variaveis
                        //int: i32
                        //double: double
                        //char: i8
                    //ponteiros
                        //int *: i32*
                        //doube *: double*
                        //char *: i8*
                //tamanho_do_tipodedado
                    //Variaveis
                        //int: 4
                        //double: 8
                        //char: 1
                    //ponteiros
                        //int *: 8
                        //doube *: 8
                        //char *: 8

num             : NUMINT                                                        #numInt
                | NDOUBLE                                                       #numDouble
                ;

function        : type ID '(' param? ')' block
                ;

                //; Function Attrs: noinline nounwind optnone uwtable
                //define dso_local **tipo_de_dado_retorno** @**nome_funcao**(**tipos_de_parametros**,**tipos_de_parametros**) #0 {
                //
                //}

                //tipo_de_dado_retorno
                    //int: i32
                    //double: double
                    //char: i8

                //tipos_de_parametros
                    //int: i32
                    //double: double
                    //char: i8 signext

type            : INT                                                           #typeInt
                | DOUBLE                                                        #typeDouble
                | CHAR                                                          #typeChar
                ;

param           : paramcomplx ',' param                                         #paramCompose
                | paramcomplx                                                   #paramSingle
                ;

paramcomplx     : type ID                                                       #paramByValue
                | type '*' ID                                                   #paramByPointer
                | type ID '[' ']'                                               #paramArray
                ;

block           : '{' cmd+ '}'
                ;

cmd             : atrib EOL                                                     #cmdAtrib
                | print EOL                                                     #cmdWrite
                | scan EOL                                                      #cmdRead
                | decl EOL                                                      #cmdDecl
                | retrn EOL                                                     #cmdReturn
                | funccallact EOL                                               #cmdFunccall
                | ifstm                                                         #cmdIf
                | swtstm                                                        #cmdswitch
                | whilee                                                        #cmdWhile
                | dowhile EOL                                                   #cmdDoWhile
                | forr                                                          #cmdFor
                ;

forr            : FOR '(' forinit ';' cond ';' atrib ')' block
                ;

forinit         : atrib                                                         #forAtrib
                | declatrib                                                     #forDeclatrib
                ;

dowhile         : DO block WHILE '(' cond ')'
                ;

whilee          : WHILE '(' cond ')' block;

swtstm          : SWITCH '(' expr ')' '{' cases+ '}'
                ;

cases           : CASE NUMINT ':' '{' cmd+ BREAK EOL '}'
                ;

retrn           : RETURN expr
                ;

atrib           : ID '=' expr                                                   #atribSimple
                | '*' ID '=' expr                                               #atribPointer
                | ID '[' expr ']' '=' expr                                      #atribArray
                | ID '+' '+'                                                    #atribPlusPlus
                | ID '-' '-'                                                    #atribMinusMinus
                ;

                //store **tipos_de_dados_valor_atribuido** **valor**, **tipos_de_dados**\* %**id**, align **tamanho_do_tipodedado**

                //tipos_de_dados_valor_atribuido
                    //Variaveis
                        //int: i32
                        //double: double
                        //char: i8
                    //ponteiros
                        //int *: i32*
                        //doube *: double*
                        //char *: i8*

                //tipos_de_dados
                    //Variaveis
                        //int: i32
                        //double: double
                        //char: i8
                    //ponteiros
                        //int *: i32*
                        //doube *: double*
                        //char *: i8*

                //tamanho_do_tipodedado
                    //Variaveis
                        //int: 4
                        //double: 8
                        //char: 1
                    //ponteiros
                        //int *: 8
                        //doube *: 8
                        //char *: 8

print           : PRINTF '(' STR ')'                                            #printSimple
                | PRINTF '(' STR ',' printargs ')'                              #printComplex
                ;

printargs       : expr ',' printargs                                            #printargsCompose
                | expr                                                          #printargsSingle
                ;

scan            : SCANF '(' STR ',' scanargs')'
                ;

scanargs        : scanargstype  ',' scanargs                                    #scanargsCompose
                | scanargstype                                                  #scanargsSingle
                ;

scanargstype    : '&' ID                                                        #scanargstypeAddress
                | ID                                                            #scanargstypeId
                | '&' ID '[' expr ']'                                           #scanargstypeAddressArray
                ;

expr            : expr '+' term                                                 #exprPlus
                | expr '-' term                                                 #exprMin
                | expr term                                                     #exprHided
                | term                                                          #exprTerm
                ;

                //Soma

                // %**id** = load **tipo da variavel a ser carregada**, **tipo ponteiro da variavel usada para linkar o valor** %**id da variavel original**, align **tamanho_do_tipodedado**
                // %**id** = load **tipo da variavel a ser carregada**, **tipo ponteiro da variavel usada para linkar o valor** %**id da variavel original**, align **tamanho_do_tipodedado**
                //%**id** = add nsw **tipo da variavel a ser carregada** %**id_var_1**, %**id_var_2**
                //store **tipo da variavel a ser carregada** %**id**, **tipo ponteiro da variavel a ser carregada o resultado** %**id da var a ser guardado resultado**, align **tamanho_do_tipodedado**

                ////tipo da variavel a ser carregada
                    //Variaveis
                        //int: i32
                        //double: double
                        //char: i8
                    //ponteiros
                        //int *: i32*
                        //doube *: double*
                        //char *: i8*
                //tamanho_do_tipodedado
                    //Variaveis
                        //int: 4
                        //double: 8
                        //char: 1
                    //ponteiros
                        //int *: 8
                        //doube *: 8
                        //char *: 8

                // SUBTRACAO

                // %**id** = load **tipo da variavel a ser carregada**, **tipo ponteiro da variavel usada para linkar o valor** %**id da variavel original**, align **tamanho_do_tipodedado**
                // %**id** = load **tipo da variavel a ser carregada**, **tipo ponteiro da variavel usada para linkar o valor** %**id da variavel original**, align **tamanho_do_tipodedado**
                //%**id** = sub nsw **tipo da variavel a ser carregada** %**id_var_1**, %**id_var_2**
                //store **tipo da variavel a ser carregada** %**id**, **tipo ponteiro da variavel a ser carregada o resultado** %**id da var a ser guardado resultado**, align **tamanho_do_tipodedado**

                ////tipo da variavel a ser carregada
                    //Variaveis
                        //int: i32
                        //double: double
                        //char: i8
                    //ponteiros
                        //int *: i32*
                        //doube *: double*
                        //char *: i8*
                //tamanho_do_tipodedado
                    //Variaveis
                        //int: 4
                        //double: 8
                        //char: 1
                    //ponteiros
                        //int *: 8
                        //doube *: 8
                        //char *: 8

                // MULTIPLICACAO

                // %**id** = load **tipo da variavel a ser carregada**, **tipo ponteiro da variavel usada para linkar o valor** %**id da variavel original**, align **tamanho_do_tipodedado**
                // %**id** = load **tipo da variavel a ser carregada**, **tipo ponteiro da variavel usada para linkar o valor** %**id da variavel original**, align **tamanho_do_tipodedado**
                //%**id** = mult nsw **tipo da variavel a ser carregada** %**id_var_1**, %**id_var_2**
                //store **tipo da variavel a ser carregada** %**id**, **tipo ponteiro da variavel a ser carregada o resultado** %**id da var a ser guardado resultado**, align **tamanho_do_tipodedado**

                ////tipo da variavel a ser carregada
                    //Variaveis
                        //int: i32
                        //double: double
                        //char: i8
                    //ponteiros
                        //int *: i32*
                        //doube *: double*
                        //char *: i8*
                //tamanho_do_tipodedado
                    //Variaveis
                        //int: 4
                        //double: 8
                        //char: 1
                    //ponteiros
                        //int *: 8
                        //doube *: 8
                        //char *: 8

                // divisao

                // %**id** = load **tipo da variavel a ser carregada**, **tipo ponteiro da variavel usada para linkar o valor** %**id da variavel original**, align **tamanho_do_tipodedado**
                // %**id** = load **tipo da variavel a ser carregada**, **tipo ponteiro da variavel usada para linkar o valor** %**id da variavel original**, align **tamanho_do_tipodedado**
                //%**id** = sdiv **tipo da variavel a ser carregada** %**id_var_1**, %**id_var_2**
                //store **tipo da variavel a ser carregada** %**id**, **tipo ponteiro da variavel a ser carregada o resultado** %**id da var a ser guardado resultado**, align **tamanho_do_tipodedado**

                ////tipo da variavel a ser carregada
                    //Variaveis
                        //int: i32
                        //double: double
                        //char: i8
                    //ponteiros
                        //int *: i32*
                        //doube *: double*
                        //char *: i8*
                //tamanho_do_tipodedado
                    //Variaveis
                        //int: 4
                        //double: 8
                        //char: 1
                    //ponteiros
                        //int *: 8
                        //doube *: 8
                        //char *: 8

term            : term '*' fact                                                 #termMult
                | term '/' fact                                                 #termDiv
                | fact                                                          #termFact
                ;

fact            : num                                                           #factNum
                | STR                                                           #factStr
                | CHARC                                                         #factChar
                | funccall                                                      #factCall
                ;

funccallact     : ID '(' funcargs? ')'
                ;

funccall        : funccallact                                                   #funccallReal
                | downfact                                                      #funcIsolate
                ;

                //funcoes
                //call **tipo da variavel a ser carregada** @**nome_funcao** (**tipo da variavel a ser carregada** %**if**, **tipo da variavel a ser carregada** %**id**)
                //tipo da variavel a ser carregada
                    //Variaveis
                        //int: i32
                        //double: double
                        //char: i8
                    //ponteiros
                        //int *: i32*
                        //doube *: double*
                        //char *: i8*

downfact        : ID                                                            #downfactId
                | '&' ID                                                        #downfactAdress
                | '*' ID                                                        #downfactContent
                | ID '[' expr ']'                                               #downfactArray
                | '(' expr ')'                                                  #downfactExpr
                ;

funcargs        : expr ',' funcargs                                             #funcargsCompose
                | expr                                                          #funcargsSingle
                ;

decl            : type ID                                                       #declSimple
                | type '*' ID                                                   #declPointer
                | type ID '[' expr ']'                                          #declArray
                | declatrib                                                     #declDeclatrib
                ;

declatrib       : type ID '=' expr                                              #declatribValueSimple
                | type '*' ID '=' ADRESS? ID                                    #declatribValuePointer
                | CHAR ID '[' ']' '=' STR                                       #declatribValueArrayString
                ;

ifstm           : IF '(' cond ')' block                                         #ifStm
                | IF '(' cond ')' block ELSE block                              #ifStmElse
                ;

cond            : cond OR cdand                                                 #condOr
                | cdand                                                         #condAnd
                ;

cdand           : cdand AND cndts                                               #cdandAnd
                | cndts                                                         #cdandCndts
                ;

cndts           : expr relop expr                                               #cndtsRelop
                | '(' cond ')'                                                  #cndtsCond
                ;

relop           : MOR
                | LESS
                | MOREQ
                | LESSEQ
                | EQ
                | NEQ
                ;

EQUALS  : '=';
MULT    : '*';
SUM     : '+';
MIN     : '-';
DIV     : '/';
OPP     : '(';
ClP     : ')';
OPB     : '{';
CLB     : '}';
OPBR    : '[';
CLBR    : ']';
EOL     : ';';
COLON   : ':';
COMMA   : ',';
HASHTAG : '#';
MOR     : '>';
LESS    : '<';
MOREQ   : '>=';
LESSEQ  : '<=';
EQ      : '==';
NEQ     : '!=';
OR      : '||';
ADRESS  : '&';
AND     : '&&';
NOT     : '!';
IF      : 'if';
ELSE    : 'else';
WHILE   : 'while';
DO      : 'do';
FOR     : 'for';
SWITCH  : 'switch';
CASE    : 'case';
BREAK   : 'break';
DEFAULT : 'default';
PRINTF  : 'printf';
SCANF   : 'scanf';
RETURN  : 'return';
INT     : 'int';
DOUBLE  : 'double';
CHAR    : 'char';
VOID    : 'void';
CHARC   : ['] (~[']) ['];
NUMINT  : '-'?[0-9]+;
NDOUBLE : NUMINT'.'[0-9]+;
ID      : [_a-zA-Z][_a-zA-Z0-9]*;
STR     : '"'(~["\r\n])*'"';
WS      : [ \t\r\n]+ -> skip;
COM     : '//'(~[\r\n])*'\r'?'\n' -> skip;
