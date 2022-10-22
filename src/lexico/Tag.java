package lexico;

public enum Tag {

    // Palavras reservadas
    START,
    EXIT,
    INT,
    FLOAT,
    STRING,
    IF,
    THEN,
    END,
    ELSE,
    DO,
    WHILE,
    SCAN,
    PRINT,

    // Operadores
    EQUAL, // ==
    NOT_EQUAL, //<>
    GREATER, // >
    GREATER_OR_EQUAL, // >=
    LESS, // <
    LESS_OR_EQUAL, // <=
    OR, // ||
    AND, // &&
    NOT, // !
    PLUS, // +
    MINUS, // -
    ASSIGN, // =
    MULT, // *
    DIV, // /

    // Pontuacao
    DOT_COMMA, // ;
    COMMA, // ,
    OPEN_PAR, // (
    CLOSE_PAR, // )
    OPEN_KEY, // {
    CLOSE_KEY, // }

    // Outros
    LITERAL,
    FLOAT_CONST,
    INTEGER_CONST,
    IDENTIFIER,
    TOKEN_INVALIDO,
    ERRO_EOF,
    EOF
}