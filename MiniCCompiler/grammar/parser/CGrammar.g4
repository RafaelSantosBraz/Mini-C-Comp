grammar CGrammar;

@header{
    package parser;
}

prog            : (global|function)+
                ;

global          : decl EOL
                ;

num             : NUMINT                                                        #numInt
                | NDOUBLE                                                       #numDouble
                ;

function        : returntype ID '(' param? ')' '{' cmd* '}'
                ;

returntype      : type                                                          #returnType
                | VOID                                                          #returnVoid
                ;

type            : INT                                                           #typeInt
                | DOUBLE                                                        #typeDouble
                | CHAR                                                          #typeChar
                ;

param           : paramcomplx  ',' param                                        #paramCompose
                | paramcomplx                                                   #paramSingle
                ;

paramcomplx     : type ID                                                       #paramByValue
                | type '*' ID                                                   #paramByPointer
                | type ID '[' ']'                                               #paramArray
                ;

block           : cmd?                                                          #blockSingle
                | OPB cmd* CLB                                                  #blockCompose
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

swtstm          : SWITCH '(' expr ')' '{' cases* dfault?'}'  
                ;

cases           : CASE NUMINT ':' cmd* BREAK?                                   #caseSimple
                | CASE NUMINT ':' '{' cmd* BREAK? '}'                           #caseBlock
                ;

dfault          : DEFAULT ':' cmd* BREAK?                                       #defaultSimple
                | DEFAULT ':' '{' cmd* BREAK? '}'                               #defaultBlock
                ;

retrn           : RETURN expr 
                ;

atrib           : ID '=' expr                                                   #atribSimple
                | '*' ID '=' expr                                               #atribPointer
                | ID '[' expr ']' '=' expr                                      #atribArray
                | ID '+' '+'                                                    #atribPlusPlus
                | ID '-' '-'                                                    #atribMinusMinus
                ; 

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
                | type ID '[' expr? ']' '=' '{' funcargs? '}'                   #declatribValueArrayList
                | CHAR ID '[' expr? ']' '=' STR                                 #declatribValueArrayString
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

cndts           : expr                                                          #cndtsExpr
                | expr relop expr                                               #cndtsRelop                
                | '!' cndts                                                     #cndtsNotExprWithout                
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