object HelloWorld {
  def main(args: Array[String]): Unit = {
    val greeting = "Hello"
    val name = "${name}"                          <#-- this will be evaluated/replaced by FreeMarker -->
    val msg = "${r"${greeting}"}, ${r"${name}"}!" <#-- an example of "escaping" in FreeMarker templates -->
    println(msg)
  }
}
