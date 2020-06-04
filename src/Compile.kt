/**
 * A function that runs the compile part and returns a String
 *
 * @param [inputCode] the code to be compiled
 * @return A string that represents the parse tree
 */
fun compile(inputCode: String): String{
    val tokens = lexScan(inputCode)
    println(tokens.contentToString())
    val parse = parse(tokens)
    return parse
}