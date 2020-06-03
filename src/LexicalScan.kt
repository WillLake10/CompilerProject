import dataObjects.getTokens

private class Token(
        var token: String,
        var startPoint: Int
)

fun lexScan(inputSentence: String): Array<String> {
    return splitToTokens(inputSentence.replace("\\s".toRegex(), ""))
}

private fun splitToTokens(inputLine: String): Array<String> {
    val token: Token = tokenise(inputLine)
    return if (inputLine.length != token.startPoint) {
        arrayOf(token.token, *splitToTokens(inputLine.substring(token.startPoint, inputLine.length)))
    } else {
        arrayOf(token.token)
    }
}

private fun tokenise(inputLine: String): Token {
    var token = "N"
    var startPoint = 1
    val firstChar = inputLine.substring(0, 1)
    val inputTokens = getTokens()
    for (element in inputTokens) {
        if (element[0].length == 1) {
            if (firstChar == element[0]) {
                token = element[1]
                startPoint = element[0].length
                break
            }
        } else {
            if (firstChar == element[0].substring(0, 1)) {
                if (inputLine.substring(0, element[0].length) == element[0]) {
                    token = element[1]
                    startPoint = element[0].length
                    break
                }
            }
        }
    }

    if (token == "N") {
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

