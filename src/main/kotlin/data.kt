import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table


//object timeLines:IntIdTable(){
//    val time = varchar("time",30).uniqueIndex()
//}
//
//class timeLine(id:EntityID<Int>):IntEntity(id){
//    companion object: IntEntityClass<timeLine>(timeLines)
//    var time by timeLines.time
//}

object tasks:IntIdTable() {
    val task = varchar("task", 200)
    val closed = bool("closed")
    val startTime = varchar("start_time", 30)
    val finishedTime = varchar("finished_time", 30)
    val date = varchar("time", 20)
}

class task(id:EntityID<Int>):IntEntity(id){
    companion object: IntEntityClass<task>(tasks)
    var task by tasks.task
    var closed by tasks.closed
    var startTime by tasks.startTime
    var finishedTime by tasks.finishedTime
    var date by tasks.date
}

object members:IntIdTable(){
    val member = varchar("member",20)
}

class member(id:EntityID<Int>):IntEntity(id){
    companion object: IntEntityClass<member>(members)
    var member by members.member
}

object taskMembers: IntIdTable(){
    val task = reference("tasks",tasks)
    val member = reference("members",members)
    val finished = bool("finished")
    val finishedTime = varchar("finished_time",30)
}

class taskMember(id:EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<taskMember>(taskMembers)
    var task by taskMembers.task
    var member by taskMembers.member
    var finished by taskMembers.finished
    var finishedTime by taskMembers.finishedTime
}