import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.suspendCoroutine

suspend fun test2(){
}
suspend fun test1(){
    GlobalScope.launch {

    }
}
fun main(){

    val num = flow<Int> {
        for (i in 1..3){
            delay(300)
            emit(i)
        }
    }
    val strs = flow<String> {
        val strs:List<String> = listOf("One","Two","Three")
        for (i in strs){
            delay(300)
            emit(i)
        }
    }

    runBlocking {
        num.combine(strs) {
                a,b -> "$a -> $b"
        }.collect { println(it) }
    }

}