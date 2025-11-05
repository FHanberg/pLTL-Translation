// $antlr-format alignTrailingComments true, columnLimit 150, minEmptyLines 1, maxEmptyLinesToKeep 1, reflowComments false, useTab false
// $antlr-format allowShortRulesOnASingleLine false, allowShortBlocksOnASingleLine true, alignSemicolons hanging, alignColons hanging

grammar pltlGrammar;

file_
    : formula EOF
    ;

formula
    : 'true'
    | 'false'
    | ATOMIC
    | '(' formula ')'
    | formula (AND | OR | IMPLICATION) formula
    | NOT formula
    | (GLOBALLY | FUTURE | NEXT) formula
    | (HISTORICALLY | ONCE | YESTERDAY | WYESTERDAY) formula
    | formula (UNTIL | WEAK | RELEASE | MIGHTY) formula
    | formula ( BEFORE | WBEFORE | SINCE | WSINCE) formula
    ;

ATOMIC
    : [a-z]+
    ;

UNTIL
    : 'U'
    ;

GLOBALLY
    : 'G'
    ;

FUTURE
    : 'F'
    ;

NEXT
    : 'X'
    ;

HISTORICALLY
    : 'H'
    ;

ONCE
    : 'O'
    ;

YESTERDAY
    : 'Y'
    ;

WYESTERDAY
    : '~Y'
    ;

WEAK
    : 'W'
    ;

RELEASE
    : 'R'
    ;

MIGHTY
    : 'M'
    ;

BEFORE
    : 'B'
    ;

WBEFORE
    : '~B'
    ;

SINCE
    : 'S'
    ;

WSINCE
    : '~S'
    ;

IMPLICATION
    : '->'
    ;

AND
    : '&'
    ;

OR
    : '|'
    ;

NOT
    : '!'
    ;

WS
    : [ \r\n\t]+ -> skip
    ;