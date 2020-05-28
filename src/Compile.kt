fun compile(inputCode: String): String{
    val tokens = lexScan(inputCode)
    println(tokens.contentToString())
    val parse = parse(tokens)
    return parse
}
