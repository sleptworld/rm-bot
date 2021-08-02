import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjuster

fun createMember(vararg ms:String){
    transaction {
        addLogger(StdOutSqlLogger)
        for (m in ms){
            try {
                member.new {
                    member = m
                }
            } catch (e:SQLException){
                println(e.toString())
            }
        }
    }
}

fun removeMember(vararg ms:String){
    transaction {
        for(m in ms){
            member.find { members.member eq m }.firstOrNull()?.delete()
        }
    }
}

fun createTask(taskName:String,startT:String=LocalDateTime.now().toString(),
               finishT:String=LocalDateTime.now().plusDays(1).withHour(0).
               withMinute(0).withSecond(0).toString()){
    transaction {
        addLogger(StdOutSqlLogger)
            val da:String = LocalDate.now().toString()
            if (task.find { tasks.date eq da }.empty()){
                task.new {
                    task = taskName
                    closed = false
                    startTime = startT
                    finishedTime = finishT
                    date = da
                }
            }else{
                task.new {
                    closed = false
                    startTime = startT
                    finishedTime = finishT
                    task = taskName
                    date = da
                }
            }
    }
}

fun listTask(date:String="",limit:Int = 5,offset:Long=0):List<task>{
    var result = emptyList<task>()
    transaction {
        if(date == ""){
            result = task.find { tasks.date eq LocalDate.now().toString() }.limit(limit,offset).toList()
        } else if (date == "all"){
            result = task.all().limit(limit,offset).toList()
        } else{
            result = task.find { tasks.date eq date }.limit(limit,offset).toList()
        }
    }
    return result
}

fun removeTask(taskId:Int):String?{
    var message:String? = null
    transaction {
        val task = task.findById(taskId)
        if(task == null){
            message = "Task:$taskId is not exist."
            return@transaction
        }
        task?.let {
            it.delete()
            val tms = taskMember.find { taskMembers.task eq taskId }
            if (!tms.empty()) {
                for (tm in tms) {
                    tm.delete()
                }
            }
        }
    }
    return message
}

fun addUser2Task(taskId:Int,memberName: String):String?{
    var message:String? = null
    transaction {
        val m = member.find { members.member eq memberName }.firstOrNull()
        if (m == null){
            message = "Message:$memberName is not exist."
            return@transaction
        }
        m?.let {m:member->
            val t = task.findById(taskId)
            if (t == null){
                message = "Task:${taskId} is not exist."
            }
            t?.let {
                taskMember.new {
                    task = it.id
                    member = m.id
                    finishedTime = ""
                    finished = false
                }
            }
        }
    }
    return message
}

fun removeUserFromTask(memberName: String,taskId: Int):String?{
    var message:String? = null
    transaction {
        val m = member.find{ members.member eq memberName }.firstOrNull()
        if(m == null){
            message = "Member:$memberName is not exist."
            return@transaction
        }
        m?.let {
            val tm = taskMember.find { taskMembers.task eq taskId and (taskMembers.member eq it.id) }.firstOrNull()
            if (tm == null){
                message = "Task:$taskId is not exist."
            }
            tm?.let {
                it.delete()
            }
        }
    }
    return message
}

fun finishTask(memberName: String,taskId: Int):String?{
    var message:String? = null
    transaction {
        val m = member.find{ members.member eq memberName }.firstOrNull()
        if(m == null){
            message = "Member:$memberName is not exist."
            return@transaction
        }
        m?.let {
            val tm = taskMember.find { taskMembers.task eq taskId and (taskMembers.member eq it.id)  }.firstOrNull()
            if (tm == null){
                message = "Task:$taskId is not exist."
            }
            tm?.let {
                it.finished = true
                it.finishedTime = LocalDateTime.now().toString()
            }
        }
        commit()
    }
    return message
}

fun myTasks(memberName:String,finished:Boolean = false):MutableList<task>{
    val result = mutableListOf<task>()
    transaction {
        val m = member.find { members.member eq memberName }.firstOrNull()
        m?.let {
            val ts = taskMember.find { taskMembers.member eq it.id and (taskMembers.finished eq finished) }

            if(!ts.empty()){
                for (t in ts){
                    task.findById(t.task)?.let {
                        result.add(it)
                    }
                }
            }
        }
    }
    return result
}