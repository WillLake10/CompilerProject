fun compile(inputCode: String): Array<String>{
    val tokens = lexScan(inputCode)
    return tokens
}