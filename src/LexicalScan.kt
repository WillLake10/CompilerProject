import dataObjects.getTokens

/**
 * A data transfer object to return token information from functions
 *
 * @param [token] A string that corresponds to a lexical token
 * @param [startPoint] An integer that corresponds to where to start the next search for a token from in the string
 */
private class Token(
        var token: String,
        var startPoint: Int
)

/**
 * Called externally to run a lexical scan on an input String and returns an array of tokens, removes the white space
 * and calls recursive function to split to tokens
 *
 * @param [inputLine] a String that contains the code to be scanned and tokenized
 * @return An array of Strings that correspond to the lexical tokens
 */
fun lexScan(inputLine: String): Array<String> {
    return splitToTokens(inputLine.replace("\\s".toRegex(), ""))
}

/**
 * A recursive function to split a String to tokens and create an array of Strings
 *
 * @param [inputLine] a String that contains the code to be scanned and tokenized
 * @return An array of Strings that correspond to the lexical tokens
 */
private fun splitToTokens(inputLine: String): Array<String> {
    /** [tokenize] is called on [inputLine] to find the next token*/
    val token: Token = tokenize(inputLine)
    /** If the length of the [inputLine] is not equal to the [Token.startPoint] recursivly call [splitToTokens] */
    return if (inputLine.length != token.startPoint) {
        arrayOf(token.token, *splitToTokens(inputLine.substring(token.startPoint, inputLine.length)))
    }
    /** Recursive base case when the [inputLine] is equal to the [Token.startPoint] then the end of [inputLine] is
     * reached */
    else {
        arrayOf(token.token)
    }
}

/**
 * A function that searches from the start of string to find a matching token from the Tokens class then return that
 * token along with the position of the end of it
 *
 * @param[inputLine] the remaining part of the original string still to be tokenized
 * @return A Token data type containing the found token and the position to start the next search of the string
 */
private fun tokenize(inputLine: String): Token {
    var token = "N" // set to "N" for ease as not a token and easy to check in the finding numbers section
    var startPoint = 1 // Length of the next token initially assumed to be 1 and then changed if not
    val firstChar = inputLine.substring(0, 1) // A constant for ease of access later
    val inputTokens = getTokens() // All the tokens and there corresponding code strings loaded
    /** for each element in inputTokens check if it matches the corresponding number of characters from the start of
     * the string */
    for (element in inputTokens) {
        /** If the length of the element[0] which represents the token is 1 then check if element[1] which is the
         * token in the coke matches the firstChar then record the token and start point */
        if (element[0].length == 1) {
            if (firstChar == element[0]) {
                token = element[1]
                startPoint = element[0].length
                break
            }
        }
        /** Runs if the token is longer than 1 */
        else {
            /** If the firstChar is equal to the first character of the element then check the whole length of eliment
             * against an equivalent length string from the inputLine and if match then record the token and start
             * point */
            if (firstChar == element[0].substring(0, 1)) {
                if (inputLine.substring(0, element[0].length) == element[0]) {
                    token = element[1]
                    startPoint = element[0].length
                    break
                }
            }
        }
    }

    /** If the token is still equal to "N" then it must be a number so assign token NUM and the actual number */
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

