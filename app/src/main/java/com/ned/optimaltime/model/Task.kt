package com.ned.optimaltime.model

public class Task(var name : String)
{
    var done = 0
    var skipped = 0


    public fun incrementDone(){
        done++
    }

    public fun incrementSkip(){
        skipped++
    }

    public fun decrementDone(){
        done--;
    }

    public fun decrementSkipped(){
        skipped--;
    }

//    private lateinit var target : Int
//    private lateinit var priority : String


}