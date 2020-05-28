package dataObjects

class TreeNode<T>(value:T){
    var value:T = value
    var parent:TreeNode<T>? = null

    var children:MutableList<TreeNode<T>> = mutableListOf()

    fun addChild(node:TreeNode<T>){
        children.add(node)
        node.parent = this
    }
    override fun toString(): String {
        var s = "${value}"
        if (!children.isEmpty()) {
            s += children.map { it.toString() }
        }
        return s
    }

    fun prettyPrintTree(){
        ppTree(0,this)
        println()
    }

    private fun ppTree(depth: Int, node: TreeNode<T>){
        for (x in 0 until depth-1) print("|  ")
        if(depth!=0) print("|--")
        print(node.value)

        if (!node.children.isEmpty()) {
            for(child in node.children){
                println()
                ppTree(depth+1, child)
            }
        }
    }
}