import dataObjects.TreeNode


fun parse(tokenArray: Array<String>): String {
    val nodes = Array(tokenArray.size + 1) { TreeNode<String>("null") }
    var nodeNum = 1
    nodes[0] = TreeNode<String>("root")
    val topSplit = parseL1(tokenArray)
    for (split in topSplit) {
        val nodeName = split.contentToString()
        nodes[nodeNum] = TreeNode<String>(nodeName.substring(1, nodeName.length - 1))
        nodes[0].addChild(nodes[nodeNum])
        val nodeLayer = nodeNum
        nodeNum += 1

        if (containsTopLayerAssign(split)) {
            val childNodes = parseL2(split)
            nodes[nodeLayer].value = childNodes[0].value
            nodes[nodeNum].value = childNodes[1].value.substring(1, childNodes[1].value.length-1)
            nodes[nodeNum+1].value = childNodes[2].value.substring(1, childNodes[2].value.length-1)
            nodes[nodeLayer].addChild(nodes[nodeNum])
            nodes[nodeLayer].addChild(nodes[nodeNum+1])
            nodeNum += 2
        }
    }

    nodes[0].prettyPrintTree()
    return nodes[0].toString()
}

private fun containsTopLayerAssign(tokenArray: Array<String>): Boolean {
    var bracketLayer = 0
    for (token in tokenArray) {
        if (token.equals("(")) bracketLayer++
        if (token.equals(")")) bracketLayer--
        if (token.equals("ASSIGN") && bracketLayer == 0) {
            return true
        }
    }
    return false
}

private fun parseL1(tokenArray: Array<String>): Array<Array<String>> {
    var returnArray: Array<Array<String>> = arrayOf(arrayOf())
    var firstTime = true
    var bracketLayer = 0
    var arrayPos = 0
    var lastSplit = 0
    for (token in tokenArray) {
        if (token.equals("(")) bracketLayer++
        if (token.equals(")")) bracketLayer--
        if (token.equals(";") && bracketLayer == 0) {
            if (firstTime) {
                returnArray = arrayOf(tokenArray.sliceArray(lastSplit..arrayPos - 1))
                firstTime = false
            } else {
                returnArray = arrayOf(*returnArray, tokenArray.sliceArray(lastSplit..arrayPos - 1))
            }
            lastSplit = arrayPos + 1
        }
        arrayPos++
    }
    returnArray = arrayOf(*returnArray, tokenArray.sliceArray(lastSplit..arrayPos - 1))
    return returnArray
}

private fun parseL2(tokenArray: Array<String>): Array<TreeNode<String>> {
    val returnNodes = Array(tokenArray.size + 1) { TreeNode<String>("null") }
    var bracketLayer = 0
    var arrayPos = 0
    for (token in tokenArray) {
        if (token.equals("(")) bracketLayer++
        if (token.equals(")")) bracketLayer--
        if (token.equals("ASSIGN") && bracketLayer == 0) {
            returnNodes[0] = TreeNode<String>("ASSIGN")
            returnNodes[1] = TreeNode<String>(tokenArray.sliceArray(0..arrayPos - 1).contentToString())
            returnNodes[2] = TreeNode<String>(tokenArray.sliceArray(arrayPos + 1..tokenArray.size - 1).contentToString())
        }
        arrayPos++
    }
    return returnNodes
}

