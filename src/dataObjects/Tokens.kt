package dataObjects

class Tokens(){
    fun _xVar(): String = "VAR X"
    fun _add(): String = "ADD"
    fun _times(): String = "TIMES"
    fun _sub(): String = "SUBTRACT"
    fun _true(): String = "TRUE"
    fun _false(): String = "FALSE"
    fun _equal(): String = "EQUAL"
    fun _lessThanEqual(): String = "LTEQ"
    fun _not(): String = "NOT"
    fun _and(): String = "AND"
    fun _assign(): String = "ASSIGN"
    fun _skip(): String = "SKIP"
    fun _semiColon(): String = ";"
    fun _if(): String = "IF"
    fun _then(): String = "THEN"
    fun _else(): String = "ELSE"
    fun _while(): String = "WHILE"
    fun _do(): String = "DO"
    fun _brkOpen(): String = "("
    fun _brkClose(): String = ")"
}

fun getTokens(): Array<Array<String>> {
    return arrayOf(
            arrayOf("x", Tokens()._xVar()),
            arrayOf("+", Tokens()._add()),
            arrayOf("*", Tokens()._times()),
            arrayOf("-", Tokens()._sub()),
            arrayOf("true", Tokens()._true()),
            arrayOf("false", Tokens()._false()),
            arrayOf("=", Tokens()._equal()),
            arrayOf("<=", Tokens()._lessThanEqual()),
            arrayOf("¬", Tokens()._not()),
            arrayOf("^", Tokens()._and()),
            arrayOf(":=", Tokens()._assign()),
            arrayOf("skip", Tokens()._skip()),
            arrayOf(";", Tokens()._semiColon()),
            arrayOf("if", Tokens()._if()),
            arrayOf("then", Tokens()._then()),
            arrayOf("else", Tokens()._else()),
            arrayOf("while", Tokens()._while()),
            arrayOf("do", Tokens()._do()),
            arrayOf("(", Tokens()._brkOpen()),
            arrayOf(")", Tokens()._brkClose()))
}