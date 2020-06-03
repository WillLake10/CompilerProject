import dataObjects.Tokens
import dataObjects.TreeNode

class ParseDataSet(treeSize: Int, var tokenArray: Array<String>) {
    var nodes = Array(treeSize + 1) { TreeNode("null") }
    var nodeNum = 1

}

fun parse(tokenArray: Array<String>): String {
    var parseDataSet = ParseDataSet(tokenArray.size, tokenArray)
    parseDataSet.nodes[0] = TreeNode("root")
    parseDataSet = parseS(parseDataSet, tokenArray)
    parseDataSet.nodes[0].addChild(parseDataSet.nodes[1])
    parseDataSet.nodes[0].prettyPrintTree()
    return parseDataSet.nodes[0].toString()
}

private fun parseS(dataSet: ParseDataSet, tokenArr: Array<String>): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    val tArray = dataSet.tokenArray
    pds.tokenArray = tokenArr
    pds = removeFullLineBracket(pds)
    if (containsTopLayer(pds, Tokens()._semiColon())) {
        pds = parseSemiColons(pds)
    } else if (containsTopLayer(pds, Tokens()._if())) {
        pds = parseIfTE(pds)
    } else if (containsTopLayer(pds, Tokens()._while())) {
        pds = parseWhileDo(pds)
    } else if (containsTopLayer(pds, Tokens()._assign())) {
        pds = parseAssign(pds)
    } else if (containsTopLayer(pds, Tokens()._skip())) {
        pds.nodes[pds.nodeNum] = TreeNode(Tokens()._skip())
        pds.nodeNum++
    } else {
        pds.nodes[pds.nodeNum] = TreeNode(tokenArr.contentToString())
        pds.nodeNum++
    }
    pds.tokenArray = tArray
    return pds
}

private fun parseA(dataSet: ParseDataSet, tokenArr: Array<String>): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    val tArray = dataSet.tokenArray
    pds.tokenArray = tokenArr
    pds = removeFullLineBracket(pds)

    if (containsTopLayer(pds, Tokens()._add())) {
        pds = parseAddTimesSubEqualsLtEq(pds, Tokens()._add())
    } else if (containsTopLayer(pds, Tokens()._times())) {
        pds = parseAddTimesSubEqualsLtEq(pds, Tokens()._times())
    } else if (containsTopLayer(pds, Tokens()._sub())) {
        pds = parseAddTimesSubEqualsLtEq(pds, Tokens()._sub())
    } else {
        pds.nodes[pds.nodeNum] = TreeNode(tokenArr[0])
        pds.nodeNum++
    }

    pds.tokenArray = tArray
    return pds
}

private fun parseB(dataSet: ParseDataSet, tokenArr: Array<String>): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    val tArray = dataSet.tokenArray
    pds.tokenArray = tokenArr
    pds = removeFullLineBracket(pds)

    if (containsTopLayer(pds, Tokens()._equal())) {
        pds = parseAddTimesSubEqualsLtEq(pds, Tokens()._equal())
    } else if (containsTopLayer(pds, Tokens()._lessThanEqual())) {
        pds = parseAddTimesSubEqualsLtEq(pds, Tokens()._lessThanEqual())
    } else if (containsTopLayer(pds, Tokens()._and())) {
        pds = parseAnd(pds, Tokens()._and())
    } else {
        pds.nodes[pds.nodeNum] = TreeNode(tokenArr[0])
        pds.nodeNum++
    }

    pds.tokenArray = tArray
    return pds
}

private fun removeFullLineBracket(dataSet: ParseDataSet): ParseDataSet{
    val pds = dataSet //pds = parseDataSet
    if(pds.tokenArray[0] == Tokens()._brkOpen() && pds.tokenArray[pds.tokenArray.size-1] == Tokens()._brkClose()){
        pds.tokenArray = pds.tokenArray.sliceArray(1..pds.tokenArray.size-2)
    }
    return pds
}

private fun containsTopLayer(parseDataSet: ParseDataSet, tokenToCheck: String): Boolean {
    var bracketLayer = 0
    for (token in parseDataSet.tokenArray) {
        if (token.equals("(")) bracketLayer++
        if (token.equals(")")) bracketLayer--
        if (token.equals(tokenToCheck) && bracketLayer == 0) {
            return true
        }
    }
    return false
}

private fun parseSemiColons(dataSet: ParseDataSet): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    var bracketLayer = 0
    var arrayPos = 0
    for (token in pds.tokenArray) {
        if (token.equals(Tokens()._brkOpen())) bracketLayer++
        if (token.equals(Tokens()._brkClose())) bracketLayer--
        if (token.equals(Tokens()._semiColon()) && bracketLayer == 0) {
            val parentNum = pds.nodeNum
            pds.nodes[pds.nodeNum] = TreeNode(Tokens()._semiColon())
            pds.nodeNum++
            var childNum = pds.nodeNum
            pds = parseS(pds, pds.tokenArray.sliceArray(0..arrayPos - 1))
            pds.nodes[parentNum].addChild(pds.nodes[childNum])
            childNum = pds.nodeNum
            pds = parseS(pds, pds.tokenArray.sliceArray(arrayPos + 1..pds.tokenArray.size - 1))
            pds.nodes[parentNum].addChild(pds.nodes[childNum])
            break
        }
        arrayPos++
    }
    return pds
}

private fun parseIfTE(dataSet: ParseDataSet): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    var bracketLayer = 0
    var arrayPos = 0
    var ifLoc = 0
    var thenLoc = 0
    var elseLoc = 0
    for (token in pds.tokenArray) {
        if (token.equals(Tokens()._brkOpen())) bracketLayer++
        if (token.equals(Tokens()._brkClose())) bracketLayer--
        if (token.equals(Tokens()._if()) && bracketLayer == 0) {
            ifLoc = arrayPos
        }
        if (token.equals(Tokens()._then()) && bracketLayer == 0){
            thenLoc = arrayPos
        }
        if (token.equals(Tokens()._else()) && bracketLayer == 0){
            elseLoc = arrayPos
        }
        arrayPos++
    }

    val parentNum = pds.nodeNum
    pds.nodes[pds.nodeNum] = TreeNode("IFTE") //If then else
    pds.nodeNum++
    var childNum = pds.nodeNum
    pds = parseB(pds, pds.tokenArray.sliceArray(ifLoc+1..thenLoc-1))
    pds.nodes[parentNum].addChild(pds.nodes[childNum])
    childNum = pds.nodeNum
    pds = parseS(pds, pds.tokenArray.sliceArray(thenLoc+1..elseLoc-1))
    pds.nodes[parentNum].addChild(pds.nodes[childNum])
    childNum = pds.nodeNum
    pds = parseS(pds, pds.tokenArray.sliceArray(elseLoc-1..pds.tokenArray.size - 1))
    pds.nodes[parentNum].addChild(pds.nodes[childNum])
    return pds
}

private fun parseWhileDo(dataSet: ParseDataSet): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    var bracketLayer = 0
    var arrayPos = 0
    var whileLoc = 0
    var doLoc = 0
    for (token in pds.tokenArray) {
        if (token.equals(Tokens()._brkOpen())) bracketLayer++
        if (token.equals(Tokens()._brkClose())) bracketLayer--
        if (token.equals(Tokens()._while()) && bracketLayer == 0) {
            whileLoc = arrayPos
        }
        if (token.equals(Tokens()._do()) && bracketLayer == 0){
            doLoc = arrayPos
        }
        arrayPos++
    }

    val parentNum = pds.nodeNum
    pds.nodes[pds.nodeNum] = TreeNode(Tokens()._while()) //If then else
    pds.nodeNum++
    var childNum = pds.nodeNum
    pds = parseB(pds, pds.tokenArray.sliceArray(whileLoc+1..doLoc-1))
    pds.nodes[parentNum].addChild(pds.nodes[childNum])
    childNum = pds.nodeNum
    pds = parseS(pds, pds.tokenArray.sliceArray(doLoc+1..pds.tokenArray.size - 1))
    pds.nodes[parentNum].addChild(pds.nodes[childNum])
    return pds
}

private fun parseAssign(dataSet: ParseDataSet): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    var bracketLayer = 0
    var arrayPos = 0
    var assignLoc = 0
    for (token in pds.tokenArray) {
        if (token.equals(Tokens()._brkOpen())) bracketLayer++
        if (token.equals(Tokens()._brkClose())) bracketLayer--
        if (token.equals(Tokens()._assign()) && bracketLayer == 0) {
            assignLoc = arrayPos
        }
        arrayPos++
    }

    val parentNum = pds.nodeNum
    pds.nodes[pds.nodeNum] = TreeNode(Tokens()._assign())
    pds.nodeNum++
    pds.nodes[pds.nodeNum] = TreeNode(Tokens()._xVar())
    pds.nodes[parentNum].addChild(pds.nodes[pds.nodeNum])
    val childNum = pds.nodeNum
    pds = parseA(pds, pds.tokenArray.sliceArray(assignLoc+1..pds.tokenArray.size - 1))
    pds.nodes[parentNum].addChild(pds.nodes[childNum])
    return pds
}

private fun parseAddTimesSubEqualsLtEq(dataSet: ParseDataSet, symbol: String): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    var bracketLayer = 0
    var arrayPos = 0
    for (token in pds.tokenArray) {
        if (token.equals(Tokens()._brkOpen())) bracketLayer++
        if (token.equals(Tokens()._brkClose())) bracketLayer--
        if (token.equals(symbol) && bracketLayer == 0) {
            val parentNum = pds.nodeNum
            pds.nodes[pds.nodeNum] = TreeNode(symbol)
            pds.nodeNum++
            var childNum = pds.nodeNum
            pds = parseA(pds, pds.tokenArray.sliceArray(0..arrayPos - 1))
            pds.nodes[parentNum].addChild(pds.nodes[childNum])
            childNum = pds.nodeNum
            pds = parseA(pds, pds.tokenArray.sliceArray(arrayPos + 1..pds.tokenArray.size - 1))
            pds.nodes[parentNum].addChild(pds.nodes[childNum])
            break
        }
        arrayPos++
    }
    return pds
}

private fun parseAnd(dataSet: ParseDataSet, symbol: String): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    var bracketLayer = 0
    var arrayPos = 0
    for (token in pds.tokenArray) {
        if (token.equals(Tokens()._brkOpen())) bracketLayer++
        if (token.equals(Tokens()._brkClose())) bracketLayer--
        if (token.equals(Tokens()._and()) && bracketLayer == 0) {
            val parentNum = pds.nodeNum
            pds.nodes[pds.nodeNum] = TreeNode(Tokens()._and())
            pds.nodeNum++
            var childNum = pds.nodeNum
            pds = parseB(pds, pds.tokenArray.sliceArray(0..arrayPos - 1))
            pds.nodes[parentNum].addChild(pds.nodes[childNum])
            childNum = pds.nodeNum
            pds = parseB(pds, pds.tokenArray.sliceArray(arrayPos + 1..pds.tokenArray.size - 1))
            pds.nodes[parentNum].addChild(pds.nodes[childNum])
            break
        }
        arrayPos++
    }
    return pds
}