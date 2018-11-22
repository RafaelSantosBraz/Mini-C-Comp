grammar CGrammar;

@header{
    package parser;
}

prog            : include* define* function+
                ;

include         : INC '<' LIB '>'                                               #includesInternal
                | INC STR                                                       #includesExternal           
                ;

define          : DEF ID NUM                                                    #defineNUM
                | DEF ID STR                                                    #defineSTR
                ;

function        : returntype ID '(' param* ')' '{' cmd* '}'
                ;

returntype      : type                                                          #returnType
                | VOID                                                          #returnVoid
                ;

type            : INT                                                           #typeInt
                | DOUBLE                                                        #typeDouble
                | CHAR                                                          #typeChar
                ;

param           : type ID ',' param                                             #paramCompose
                | type ID                                                       #paramSingle
                ;

block           : cmd?                                                          #blockSingle
                | '{' cmd* '}'                                                  #blockCompose
                ;

cmd             : atrib EOL                                                     #cmdAtrib
                | print EOL                                                     #cmdWrite
                | scan EOL                                                      #cmdRead
                | decl EOL                                                      #cmdDecl
                | retrn EOL                                                     #cmdReturn
                | ifstm                                                         #cmdIf     
                ;

retrn           : RETURN expr 
                ;

atrib           : ID '=' expr           
                ; 

print           : PRINTF '(' STR ')'                                            #printSimple
                | PRINTF '(' STR ',' printargs ')'                              #printComplex
                ;

printargs       : expr ',' printargs                                            #printargsCompose
                | expr                                                          #printargsSingle
                ;

scan            : SCANF '(' STR ',' scanargs')'      
                ;

scanargs        : '&' ID ',' scanargs                                           #scanargsCompose
                | '&' ID                                                        #scanargsSingle
                ;

expr            : expr '+' term                                                 #exprPlus
                | expr '-' term                                                 #exprMin
                | expr term                                                     #exprHided
                | term                                                          #exprTerm
                ;

term            : term '*' fact                                                 #termMult
                | term '/' fact                                                 #termDiv
                | fact                                                          #termFact
                ;

fact            : NUM                                                           #factNum
                | ID                                                            #factVar
                | STR                                                           #factStr
                | funccall                                                      #factFunc
                | '(' expr ')'                                                  #factExpr
                ;

funccall        : ID '(' funcargs ')'
                ;

funcargs        : expr ',' funcargs                                             #funcargsCompose
                | expr                                                          #funcargsSingle
                ;

decl            : type ID                                                       #declSimple
                | type ID '=' expr                                              #declValue
                ;

ifstm           : IF cond block                                                 #ifStm
                | IF cond b1=block ELSE b2=block                                #ifStmElse
                ;

cond            : '(' cond OR cdand ')'                                         #condOR
                | cdand                                                         #condAnd        
                ;

cdand           : '(' cdand AND cndts ')'                                       #cdandAnd
                | cndts                                                         #cdandCndts
                ;

cndts           : '(' expr ')'                                                  #cndtsExpr
                | '(' e1=expr relop e2=expr ')'                                 #cndtsRelop
                | '!' ('(' expr ')')                                            #cndtsNotExpr
                | '!' ('(' e1=expr relop e2=expr ')')                           #cndtsNotRelop
                | '(' cond ')'                                                  #cndtsCond
                ;

relop           : '>'
                | '<'
                | '>='
                | '<='
                | '=='
                | '!='
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
EOL     : ';';
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
INC     : '#include';
IF      : 'if';
ELSE    : 'else';
PRINTF  : 'printf';
SCANF   : 'scanf';
RETURN  : 'return';
DEF     : '#define';
INT     : 'int';
DOUBLE  : 'double';
CHAR    : 'char';
VOID    : 'void';
NUM     : '-'?[0-9]+('.'[0-9]+)?;
ID      : [_a-zA-Z][_a-zA-Z0-9]*;
LIB     : (~["\\\r\n,>< (){}#;&])+;
STR     : '"'(~["\\\r\n])*'"';
WS      : [ \t\r\n]+ -> skip;
COM     : '//'(~[\r\n])*'\r'?'\n' -> skip;