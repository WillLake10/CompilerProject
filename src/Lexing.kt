class Token(
        var token: String,
        var startPoint: Int
)

fun lexScan(inputSentance: String): Array<String> {
    val sentance: String = inputSentance.replace("\\s".toRegex(), "")
    val sentanceArray: Array<String> = splitToTokens(sentance);
    return sentanceArray
}

fun splitToTokens(inputLine: String): Array<String> {
    var token: Token = tokenise(inputLine)
    var splitFull: Array<String>
    if(inputLine.length != 1){
        splitFull = arrayOf(token.token, *splitToTokens(inputLine.substring(token.startPoint,inputLine.length)))
    }else{
        splitFull = arrayOf(token.token)
    }

    return splitFull
}

fun tokenise(inputLine: String): Token {
    var token = "N"
    var startPoint: Int
    var firstChar = inputLine.substring(0, 1)
    var inputTokens = arrayOf("x", "+", "*", "-", "true", "false", "=", "<=", "¬", "^", ":=", "skip", ";", "if", "then", "else", "while", "do", "(", ")")
    for (element in inputTokens) {
        if (element.length == 1) {
            if (firstChar == element) {
                token = element
                break
            }
        } else {
            if (firstChar == element.substring(0, 1)) {
                if (inputLine.substring(0, element.length).equals(element)) {
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