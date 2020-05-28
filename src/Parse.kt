import dataObjects.TreeNode

fun parse(tokenArray: Array<String>): String {
    val rootNode = TreeNode<String>("root")
    val n1 = TreeNode<String>("n1")
    val n2 = TreeNode<String>("n2")
    val n3 = TreeNode<String>("n3")
    rootNode.value="Test"
    rootNode.addChild(n1)
    rootNode.addChild(n2)
    n1.addChild(n3)
    rootNode.prettyPrintTree()
    return rootNode.toString()
}
/*
private fun splitToSegments(tokenArray: Array<String>): Array<Array<String>> {

}*/