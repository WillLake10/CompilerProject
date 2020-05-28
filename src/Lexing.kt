class Token(
        var token: String,
        var startPoint: Int
)

fun lexScan(inputSentance: String): Array<String> {
    val sentance: String = inputSentance.replace("\\s".toRegex(), "")
    return splitToTokens(sentance)
}

fun splitToTokens(inputLine: String): Array<String> {
    val token: Token = tokenise(inputLine)
    return if(inputLine.length != token.startPoint){
        arrayOf(token.token, *splitToTokens(inputLine.substring(token.startPoint,inputLine.length)))
    }else{
        arrayOf(token.token)
    }
}

fun tokenise(inputLine: String): Token {
    var token = "N"
    var startPoint: Int
    val firstChar = inputLine.substring(0, 1)
    val inputTokens = arrayOf("x", "+", "*", "-", "true", "false", "=", "<=", "¬", "^", ":=", "skip", ";", "if", "then", "else", "while", "do", "(", ")")
    for (element in inputTokens) {
        if (element.length == 1) {
            if (firstChar == element) {
                token = element
                break
            }
        } else {
            if (firstChar == element.substring(0, 1)) {
                if (inputLine.substring(0, element.length) == element) {
                    token = element
                    break
                }
            }
        }
    }

    startPoint = token.length
    if (token == "N") {
        startPoint = 1
        val regex = """[0-9]""".toRegex()
        if (regex.containsMatchIn(firstChar)) {
            token = "NUM $firstChar"
            for (inc in 1 until inputLine.length) {
                if (regex.containsMatchIn(inputLine.substring(inc, inc + 1))) {
                    token += inputLine.substring(inc, inc + 1)
                    startPoint += 1
                } else {
                    break
                }
            }
        }
    }
    return Token(token, startPoint)
}