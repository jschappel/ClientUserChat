package com.example.joshuaschappel.clientuserchat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_userlist.*

class UserViewActivity:AppCompatActivity() {

    lateinit var userListAdapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userlist)

        val users = listOf<String>("Joshua", "Sam", "Dana")

        userListAdapter = UserListAdapter(users)
        recyclerView_users.layoutManager = LinearLayoutManager(this)
        recyclerView_users.adapter = userListAdapter
    }
}