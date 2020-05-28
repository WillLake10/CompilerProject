fun main(args: Array<String>){
    val compiledCode = compile("x := 0; while (x <= 15) do (x := x + 1; skip)")
    println()
    print(compiledCode)
}