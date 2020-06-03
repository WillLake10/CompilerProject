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
    if (containsTopLayer(pds, ";")) {
        pds = parseSemiColons(pds)
    } else if (containsTopLayer(pds, "IF")) {
        pds = parseIfTE(pds)
    } else if (containsTopLayer(pds, "WHILE")) {
        pds = parseIfTE(pds)
    } else {
        pds.nodes[pds.nodeNum] = TreeNode(tokenArr.contentToString())
        pds.nodeNum++
    }
    pds.tokenArray = tArray
    return pds
}

private fun parseA(dataSet: ParseDataSet, tokenArr: Array<String>): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    pds.nodes[pds.nodeNum] = TreeNode(tokenArr.contentToString())
    pds.nodeNum++
    return pds
}

private fun parseB(dataSet: ParseDataSet, tokenArr: Array<String>): ParseDataSet {
    var pds = dataSet //pds = parseDataSet
    pds.nodes[pds.nodeNum] = TreeNode(tokenArr.contentToString())
    pds.nodeNum++
    return pds
}

private fun containsTopLayer(dataSet: ParseDataSet, tokenToCheck: String): Boolean {
    var parseDataSet = dataSet
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
        if (token.equals("(")) bracketLayer++
        if (token.equals(")")) bracketLayer--
        if (token.equals(";") && bracketLayer == 0) {
            val parentNum = pds.nodeNum
            pds.nodes[pds.nodeNum] = TreeNode(";")
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
        if (token.equals("(")) bracketLayer++
        if (token.equals(")")) bracketLayer--
        if (token.equals("IF") && bracketLayer == 0) {
            ifLoc = arrayPos
        }
        if (token.equals("THEN") && bracketLayer == 0){
            thenLoc = arrayPos
        }
        if (token.equals("ELSE") && bracketLayer == 0){
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