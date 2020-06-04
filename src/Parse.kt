import dataObjects.Tokens
import dataObjects.TreeNode

/**
 * A data transfer objects to return the nodes Arrray, tokenArray and the node position
 *
 * @param [tokenArray] the token array that is to be compiled
 */
class ParseDataSet(var tokenArray: Array<String>) {
    /** nodes is an array of TreeNode's of length of the token arrey + 1 for the root */
    var nodes = Array(tokenArray.size + 1) { TreeNode("null") }
    var nodeNum = 1
}

/**
 * Called externially to parse a token array into a String representing a tree
 *
 * @param [tokenArray] the token array that is to be compiled
 * @return A String that represents a parse tree
 */
fun parse(tokenArray: Array<String>): String {
    var parseDataSet = ParseDataSet(tokenArray)
    /** The root node that all further nodes branch from */
    parseDataSet.nodes[0] = TreeNode("root")
    /** Start the parse from State S */
    parseDataSet = parseS(parseDataSet, parseDataSet.tokenArray)
    /** The first node is the root and adds the next noda as a child */
    parseDataSet.nodes[0].addChild(parseDataSet.nodes[1])
    /** Prints out the tree in a way thats easily viewable */
    parseDataSet.nodes[0].prettyPrintTree()
    return parseDataSet.nodes[0].toString()
}

/**
 * A function to parse the S state
 *
 * @param [dataSet] a ParseDataSet containing the nodes and whole token array
 * @param [tokenArr] the token arrey to be parsed in that section
 * @return a ParseDataSet containing all the nodes already parsed
 */
private fun parseS(dataSet: ParseDataSet, tokenArr: Array<String>): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    val tArray = dataSet.tokenArray // Stores a constant with the whole token array
    /** sets the pds's tokenArray to the token array to be parsed in this parse */
    pds.tokenArray = tokenArr
    /** Removes the brackets from token array if they are around the whole array */
    pds = removeFullLineBracket(pds)
    when {
        containsTopLayer(pds, Tokens()._semiColon()) -> { //If contains a ; then parse for ;
            pds = parseSemiColons(pds)
        }
        containsTopLayer(pds, Tokens()._if()) -> { //If contains an if statement then parse for if
            pds = parseIfTE(pds)
        }
        containsTopLayer(pds, Tokens()._while()) -> { //If contains a while statement then parse for while
            pds = parseWhileDo(pds)
        }
        containsTopLayer(pds, Tokens()._assign()) -> { //If contains an assign then parse for assign
            pds = parseAssign(pds)
        }
        containsTopLayer(pds, Tokens()._skip()) -> { //If contians skip set the node to skip as has no further parse
            pds.nodes[pds.nodeNum] = TreeNode(Tokens()._skip())
            pds.nodeNum++
        }
        else -> { //Else it doesnt contain a valid token so set node to Syntax Error and the tokens with error in
            pds.nodes[pds.nodeNum] = TreeNode("Syntax Error in" + pds.tokenArray.contentToString())
            pds.nodeNum++
        }
    }
    /** sets the pds.tokenArray back to original tokenArray */
    pds.tokenArray = tArray
    return pds
}

/**
 * A function to parse the a state
 *
 * @param [dataSet] a ParseDataSet containing the nodes and whole token array
 * @param [tokenArr] the token arrey to be parsed in that section
 * @return a ParseDataSet containing all the nodes already parsed
 */
private fun parseA(dataSet: ParseDataSet, tokenArr: Array<String>): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    val tArray = dataSet.tokenArray // Stores a constant with the whole token array
    /** sets the pds's tokenArray to the token array to be parsed in this parse */
    pds.tokenArray = tokenArr
    /** Removes the brackets from token array if they are around the whole array */
    pds = removeFullLineBracket(pds)
    val addTimesSub = arrayOf(Tokens()._add(), Tokens()._times(), Tokens()._sub())
    /** Checks if each of the add, times and sub are containd and if so it parses for that one */
    for (ats in addTimesSub) {
        if (containsTopLayer(pds, ats)) {
            pds = parseAddTimesSubEqualsLtEq(pds, ats)
        }
    }
    /** If it does not contain add, times or sub then it is a variable or number so set node to it and no further parse */
    if (!containsTopLayer(pds, Tokens()._add()) && !containsTopLayer(pds, Tokens()._times()) && !containsTopLayer(pds, Tokens()._sub())) {
        pds.nodes[pds.nodeNum] = TreeNode(tokenArr[0])
        pds.nodeNum++
    }
    /** sets the pds.tokenArray back to original tokenArray */
    pds.tokenArray = tArray
    return pds
}

/**
 * A function to parse the b state
 *
 * @param [dataSet] a ParseDataSet containing the nodes and whole token array
 * @param [tokenArr] the token arrey to be parsed in that section
 * @return a ParseDataSet containing all the nodes already parsed
 */
private fun parseB(dataSet: ParseDataSet, tokenArr: Array<String>): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    val tArray = dataSet.tokenArray // Stores a constant with the whole token array
    /** sets the pds's tokenArray to the token array to be parsed in this parse */
    pds.tokenArray = tokenArr
    /** Removes the brackets from token array if they are around the whole array */
    pds = removeFullLineBracket(pds)
    when {
        containsTopLayer(pds, Tokens()._equal()) -> { //If contains an equal then parse for equal
            pds = parseAddTimesSubEqualsLtEq(pds, Tokens()._equal())
        }
        containsTopLayer(pds, Tokens()._lessThanEqual()) -> { //If contains less than equal then parse for less than equal
            pds = parseAddTimesSubEqualsLtEq(pds, Tokens()._lessThanEqual())
        }
        containsTopLayer(pds, Tokens()._and()) -> {//If contains an and then parse for and
            pds = parseAnd(pds, Tokens()._and())
        }
        containsTopLayer(pds, Tokens()._true()) -> {//If contains true then set node to true
            pds.nodes[pds.nodeNum] = TreeNode(Tokens()._true())
            pds.nodeNum++
        }
        containsTopLayer(pds, Tokens()._false()) -> {//If contains false then set node to false
            pds.nodes[pds.nodeNum] = TreeNode(Tokens()._false())
            pds.nodeNum++
        }
        else -> { //Else it doesnt contain a valid token so set node to Syntax Error and the tokens with error in
            pds.nodes[pds.nodeNum] = TreeNode("Syntax Error in" + pds.tokenArray.contentToString())
            pds.nodeNum++
        }
    }
    /** sets the pds.tokenArray back to original tokenArray */
    pds.tokenArray = tArray
    return pds
}

/**
 * A function that removes the outer brackets if they enclose the whole token array
 *
 * @param [pds] a ParseDataSet containing the token array to check brackets for
 * @return a ParseDataSet containing a token array that has no brackets that surround the whole array
 */
private fun removeFullLineBracket(pds: ParseDataSet): ParseDataSet {
    if (pds.tokenArray[0] == Tokens()._brkOpen() && pds.tokenArray[pds.tokenArray.size - 1] == Tokens()._brkClose()) {
        var bracketLayer = 1
        var brkLayerTo0 = false
        for(token in pds.tokenArray.sliceArray(1..pds.tokenArray.size - 2)){
            if (bracketLayer == 0) brkLayerTo0 = true //If bracket layer goes to 0 then the brackets don't surround whole array
            if (token == Tokens()._brkOpen()) bracketLayer++ //If there is an open bracket the bracket layer is incremented
            if (token == Tokens()._brkClose()) bracketLayer-- //If there is a close bracket the bracket layer is incremented
        }
        if (!brkLayerTo0) pds.tokenArray = pds.tokenArray.sliceArray(1..pds.tokenArray.size - 2)
    }
    return pds
}

/**
 * A function that checks if a given token is in the token arrey but not in brackets
 *
 * @param [parseDataSet] contains the token array to check
 * @param [tokenToCheck] the token to check for in the token array
 * @return a boolean that returns true if token array contains the token outside of brackets
 */
private fun containsTopLayer(parseDataSet: ParseDataSet, tokenToCheck: String): Boolean {
    var bracketLayer = 0 // Keeps track of if the token is in bracket
    for (token in parseDataSet.tokenArray) {
        /** If there is an open bracket the bracket layer is incremented */
        if (token == Tokens()._brkOpen()) bracketLayer++
        /** If there is a close bracket the bracket layer is incremented */
        if (token == Tokens()._brkClose()) bracketLayer--
        /** If the token being looked for matches the token and not in bracket */
        if (token == tokenToCheck && bracketLayer == 0) return true
    }
    return false
}

/**
 * A function that parses ;
 *
 * @param [dataSet] a ParseDataSet containing the token array and tree nodes
 * @return ParseDataSet that contains the tree nodes for the token array
 */
private fun parseSemiColons(dataSet: ParseDataSet): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    var bracketLayer = 0
    var arrayPos = 0
    for (token in pds.tokenArray) {
        /** If there is an open bracket the bracket layer is incremented */
        if (token == Tokens()._brkOpen()) bracketLayer++
        /** If there is a close bracket the bracket layer is incremented */
        if (token == Tokens()._brkClose()) bracketLayer--
        /** If ; matches the token and not in bracket */
        if (token == Tokens()._semiColon() && bracketLayer == 0) {
            val parentNum = pds.nodeNum //keep track of where the parent node is in the TreeNode array
            pds.nodes[pds.nodeNum] = TreeNode(Tokens()._semiColon()) //set current node to semi colon
            pds.nodeNum++
            /** parse left side of ; */
            var childNum = pds.nodeNum
            pds = parseS(pds, pds.tokenArray.sliceArray(0 until arrayPos))
            pds.nodes[parentNum].addChild(pds.nodes[childNum])
            /** parse right side of ; */
            childNum = pds.nodeNum
            pds = parseS(pds, pds.tokenArray.sliceArray(arrayPos + 1 until pds.tokenArray.size))
            pds.nodes[parentNum].addChild(pds.nodes[childNum])
            break
        }
        arrayPos++
    }
    return pds
}

/**
 * A function that parses If, Then, else statement
 *
 * @param [dataSet] a ParseDataSet containing the token array and tree nodes
 * @return ParseDataSet that contains the tree nodes for the token array
 */
private fun parseIfTE(dataSet: ParseDataSet): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    var bracketLayer = 0
    var ifLoc = 0
    var thenLoc = 0
    var elseLoc = 0
    for ((arrayPos, token) in pds.tokenArray.withIndex()) {
        /** If there is an open bracket the bracket layer is incremented */
        if (token == Tokens()._brkOpen()) bracketLayer++
        /** If there is a close bracket the bracket layer is incremented */
        if (token == Tokens()._brkClose()) bracketLayer--
        if (token == Tokens()._if() && bracketLayer == 0) {
            ifLoc = arrayPos //saves location of the if token
        }
        if (token == Tokens()._then() && bracketLayer == 0) {
            thenLoc = arrayPos //saves location of the then token
        }
        if (token == Tokens()._else() && bracketLayer == 0) {
            elseLoc = arrayPos //saves location of the else token
        }
    }

    val parentNum = pds.nodeNum //keep track of where the parent node is in the TreeNode array
    pds.nodes[pds.nodeNum] = TreeNode("IFTE") //Sets current node to If then else (IFTE)
    pds.nodeNum++
    /** parse if statements condition */
    var childNum = pds.nodeNum
    pds = parseB(pds, pds.tokenArray.sliceArray(ifLoc + 1 until thenLoc))
    pds.nodes[parentNum].addChild(pds.nodes[childNum])
    /** parse if statements body */
    childNum = pds.nodeNum
    pds = parseS(pds, pds.tokenArray.sliceArray(thenLoc + 1 until elseLoc))
    pds.nodes[parentNum].addChild(pds.nodes[childNum])
    /** parse else statements body */
    childNum = pds.nodeNum
    pds = parseS(pds, pds.tokenArray.sliceArray(elseLoc - 1 until pds.tokenArray.size))
    pds.nodes[parentNum].addChild(pds.nodes[childNum])
    return pds
}

/**
 * A function that parses while statement
 *
 * @param [dataSet] a ParseDataSet containing the token array and tree nodes
 * @return ParseDataSet that contains the tree nodes for the token array
 */
private fun parseWhileDo(dataSet: ParseDataSet): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    var bracketLayer = 0
    var whileLoc = 0
    var doLoc = 0
    for ((arrayPos, token) in pds.tokenArray.withIndex()) {
        /** If there is an open bracket the bracket layer is incremented */
        if (token == Tokens()._brkOpen()) bracketLayer++
        /** If there is a close bracket the bracket layer is incremented */
        if (token == Tokens()._brkClose()) bracketLayer--
        if (token == Tokens()._while() && bracketLayer == 0) {
            whileLoc = arrayPos //saves location of while token
        }
        if (token == Tokens()._do() && bracketLayer == 0) {
            doLoc = arrayPos //saves location of do token
        }
    }

    val parentNum = pds.nodeNum //keep track of where the parent node is in the TreeNode array
    pds.nodes[pds.nodeNum] = TreeNode(Tokens()._while()) //Sets current node to while
    pds.nodeNum++
    /** parse while statements condition */
    var childNum = pds.nodeNum
    pds = parseB(pds, pds.tokenArray.sliceArray(whileLoc + 1 until doLoc))
    pds.nodes[parentNum].addChild(pds.nodes[childNum])
    /** parse while statements body */
    childNum = pds.nodeNum
    pds = parseS(pds, pds.tokenArray.sliceArray(doLoc + 1 until pds.tokenArray.size))
    pds.nodes[parentNum].addChild(pds.nodes[childNum])
    return pds
}

/**
 * A function that parses assign statement
 *
 * @param [dataSet] a ParseDataSet containing the token array and tree nodes
 * @return ParseDataSet that contains the tree nodes for the token array
 */
private fun parseAssign(dataSet: ParseDataSet): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    var bracketLayer = 0
    var assignLoc = 0
    for ((arrayPos, token) in pds.tokenArray.withIndex()) {
        /** If there is an open bracket the bracket layer is incremented */
        if (token == Tokens()._brkOpen()) bracketLayer++
        /** If there is a close bracket the bracket layer is incremented */
        if (token == Tokens()._brkClose()) bracketLayer--
        if (token == Tokens()._assign() && bracketLayer == 0) {
            assignLoc = arrayPos //saves location of assign token
        }
    }

    val parentNum = pds.nodeNum //keep track of where the parent node is in the TreeNode array
    pds.nodes[pds.nodeNum] = TreeNode(Tokens()._assign()) //Sets current node to assign
    pds.nodeNum++
    /** sets the node to the variable */
    pds.nodes[pds.nodeNum] = TreeNode(Tokens()._xVar())
    pds.nodes[parentNum].addChild(pds.nodes[pds.nodeNum])
    /** parse the assign for going to state a */
    val childNum = pds.nodeNum
    pds = parseA(pds, pds.tokenArray.sliceArray(assignLoc + 1 until pds.tokenArray.size))
    pds.nodes[parentNum].addChild(pds.nodes[childNum])
    return pds
}

/**
 * A function that parses Add, times, subtract, equals and less than equal
 *
 * @param [dataSet] a ParseDataSet containing the token array and tree nodes
 * @param [symbol] the token being parsed
 * @return ParseDataSet that contains the tree nodes for the token array
 */
private fun parseAddTimesSubEqualsLtEq(dataSet: ParseDataSet, symbol: String): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    var bracketLayer = 0
    var arrayPos = 0
    for (token in pds.tokenArray) {
        /** If there is an open bracket the bracket layer is incremented */
        if (token == Tokens()._brkOpen()) bracketLayer++
        /** If there is a close bracket the bracket layer is incremented */
        if (token == Tokens()._brkClose()) bracketLayer--
        if (token == symbol && bracketLayer == 0) {
            val parentNum = pds.nodeNum //keep track of where the parent node is in the TreeNode array
            pds.nodes[pds.nodeNum] = TreeNode(symbol) //Sets current node to symbol
            pds.nodeNum++
            /** parse left side of token */
            var childNum = pds.nodeNum
            pds = parseA(pds, pds.tokenArray.sliceArray(0 until arrayPos))
            pds.nodes[parentNum].addChild(pds.nodes[childNum])
            /** parse right side of token */
            childNum = pds.nodeNum
            pds = parseA(pds, pds.tokenArray.sliceArray(arrayPos + 1 until pds.tokenArray.size))
            pds.nodes[parentNum].addChild(pds.nodes[childNum])
            break
        }
        arrayPos++
    }
    return pds
}

/**
 * A function that parses and statement
 *
 * @param [dataSet] a ParseDataSet containing the token array and tree nodes
 * @return ParseDataSet that contains the tree nodes for the token array
 */
private fun parseAnd(dataSet: ParseDataSet, symbol: String): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    var bracketLayer = 0
    var arrayPos = 0
    for (token in pds.tokenArray) {
        /** If there is an open bracket the bracket layer is incremented */
        if (token.equals(Tokens()._brkOpen())) bracketLayer++
        /** If there is a close bracket the bracket layer is incremented */
        if (token.equals(Tokens()._brkClose())) bracketLayer--
        if (token.equals(Tokens()._and()) && bracketLayer == 0) {
            val parentNum = pds.nodeNum //keep track of where the parent node is in the TreeNode array
            pds.nodes[pds.nodeNum] = TreeNode(Tokens()._and()) //Sets current node to symbol
            pds.nodeNum++
            /** parse left side of and */
            var childNum = pds.nodeNum
            pds = parseB(pds, pds.tokenArray.sliceArray(0..arrayPos - 1))
            pds.nodes[parentNum].addChild(pds.nodes[childNum])
            /** parse right side of and */
            childNum = pds.nodeNum
            pds = parseB(pds, pds.tokenArray.sliceArray(arrayPos + 1..pds.tokenArray.size - 1))
            pds.nodes[parentNum].addChild(pds.nodes[childNum])
            break
        }
        arrayPos++
    }
    return pds
}