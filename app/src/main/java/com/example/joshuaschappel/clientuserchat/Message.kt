package com.example.joshuaschappel.clientuserchat

/**
 * For Message type:
 *      0 = JOIN
 *      1 = DELETE
 *      2 = everything else
 */
class Message(val user: String, val msg: String, val msgType: Int, val origin: Boolean)