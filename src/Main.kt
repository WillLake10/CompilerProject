fun main(args: Array<String>){
    var lex = lexScan("x := 0; while (x <= 15) do (x := x + 1; skip)")
    print(lex.contentToString())
}