grammar Statements;

@header {package com.evo.internal.grammar.antlr;}

statements              : statementOrComment* EOF
                        ;
statementOrComment      : comment
                        | statement
                        ;
statement               : (WORD|PHRASE|COMMENT_MULTILINE)+ SEMICOLON?
                        ;
comment                 : COMMENT_MULTILINE SEMICOLON?
                        | COMMENT_SINGLE_LINE SEMICOLON?
                        ;

WHITE_SPACE             : [ \t\r\n]+ -> skip; // skip spaces, tabs, newlines
COMMENT_MULTILINE       : '/*' .*? '*/'
                        ;
COMMENT_SINGLE_LINE     : ('--' [ \t] | '#') ~[\r\n]* ('\r'? '\n' | EOF)
                        | '--' ('\r'? '\n' | EOF)
                        ;

COLON                   : [:];
SEMICOLON               : [;];
SINGLE_QUOTE            : ['];
SINGLE_QUOTE_ESC        : '\\' SINGLE_QUOTE;
DOUBLE_QUOTE            : ["];
DOUBLE_QUOTE_ESC        : '\\' DOUBLE_QUOTE;
BACK_TICK               : [`];
BACK_TICK_ESC           : '\\' BACK_TICK;
PHRASE                  : SINGLE_QUOTE ~[']* SINGLE_QUOTE { setText(getText().substring(1, getText().length()-1)); }
                        | DOUBLE_QUOTE ~["]* DOUBLE_QUOTE { setText(getText().substring(1, getText().length()-1)); }
                        | BACK_TICK ~[`]* BACK_TICK { setText(getText().substring(1, getText().length()-1)); }
                        ;
NEW_LINE                : '\r'? '\n' | '\r';
WORD                    : ~[ \t\r\n;:]+;

fragment A:[aA];
fragment B:[bB];
fragment C:[cC];
fragment D:[dD];
fragment E:[eE];
fragment F:[fF];
fragment G:[gG];
fragment H:[hH];
fragment I:[iI];
fragment J:[jJ];
fragment K:[kK];
fragment L:[lL];
fragment M:[mM];
fragment N:[nN];
fragment O:[oO];
fragment P:[pP];
fragment Q:[qQ];
fragment R:[rR];
fragment S:[sS];
fragment T:[tT];
fragment U:[uU];
fragment V:[vV];
fragment W:[wW];
fragment X:[xX];
fragment Y:[yY];
fragment Z:[zZ];