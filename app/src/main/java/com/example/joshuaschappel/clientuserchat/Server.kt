package com.example.joshuaschappel.clientuserchat

import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket

object Server {

    lateinit var socket: Socket
    val VERSION = 1.0
    val PORT = 6161
    val HOST = "10.0.2.2"
    lateinit var out: PrintWriter
    lateinit var inn: BufferedReader
    lateinit var user: String
    var connectionStatus = false

    fun send(msg: String) {
        Server.out.println(msg)
        Server.out.flush()
    }
}