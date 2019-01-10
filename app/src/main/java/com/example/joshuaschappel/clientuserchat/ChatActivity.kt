package com.example.joshuaschappel.clientuserchat

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.Exception
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class ChatActivity: AppCompatActivity() {

    lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //  Hide the keyboard when activity is created
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        //  The arrayList that we will add the messages to
        var messageList: MutableList<Message> = ArrayList()

        // set up the adapter
        messageAdapter = MessageAdapter(messageList)


        // Add the toolbar
        var toolbar: Toolbar? = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //set the recyclerView to an adapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = messageAdapter

        var sendButton = findViewById<Button>(R.id.send_button)
        var userText = findViewById<EditText>(R.id.user_input)

        // Create a new thread where we listen for the server to say something
        thread {
            ClientHandler(this, messageAdapter).listenOnSocket()
        }

        // Add actionlistenter to the send button
        sendButton.setOnClickListener {
            var text2Send = userText.text
            Toast.makeText(this, Server.user, Toast.LENGTH_LONG).show()

            if (!text2Send.isEmpty()) {
                ConnectServerClass(text2Send.toString()).execute()
                userText.text = null
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        DisconnectFromServer().execute()
        //Go back to first activity
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.chat_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId) {

            R.id.userList -> {
                Log.d("ID", "We Made it")
                val intent = Intent(this,UserViewActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class ClientHandler(var activity: Activity, var adapter: MessageAdapter) {

        fun listenOnSocket(){
            val reader = Scanner(Server.socket.getInputStream())

            while (Server.connectionStatus){
                try {
                    val msg = reader.nextLine()

                    // Split the message
                    val posOfDelim = msg.indexOf("|")
                    val cmd = msg.substring(0,posOfDelim)
                    val opt = msg.substring(posOfDelim+1)
                    Log.d("CMD", opt)

                    if (cmd.equals("TEXT"))
                        addMessage(opt)
                    else if (cmd.equals("ADDU")){
                        addRemove(opt,0)
                    }
                    else if (cmd.equals("DELU"))
                        addRemove(opt,1)

                    //TODO: More cases

                } catch (e:Exception){
                    e.printStackTrace()
                    close()
                }
            }
        }

        /**
         * Adds a message to the recyclerViewAdapter
         */
        private fun addMessage(msg:String) {
            //create the message object
            val posOfDelim = msg.indexOf("|")
            val user = msg.substring(0,posOfDelim)
            val msg = msg.substring(posOfDelim+1).trim()
            val messageObj = Message(user,msg,2,isFromMe(user))
            // Must be run on the UIThread because we are updating the view
            activity.runOnUiThread {
                adapter.addMessage(messageObj)
                activity.recycler_view.smoothScrollToPosition(adapter.itemCount -1)   // Scroll the window to the bottom
            }
        }

        /**
         * Removes or add a user to the recyclerView depending on the tag type
         * Tag must be either 1 or 2
         */
        private fun addRemove(user: String, tag: Int){
            val user = user.trim()
            val message = Message(user,"",tag,false)

            // Must be run on the UIThread because we are updating the view
            activity.runOnUiThread {
                adapter.addMessage(message)
                activity.recycler_view.scrollToPosition(adapter.itemCount -1)   // Scroll the window to the bottom
            }
        }

        /**
         * Decides if the message was from the user or from the server
         */
        private fun isFromMe(user:String): Boolean{
            if (user.equals(Server.user))
                return true
            return false
        }

        /**
         * Closes the socket in case of an interrupt
         */
        private fun close(){
            Server.connectionStatus = false
            Server.socket.close()
        }
    }

    inner class DisconnectFromServer: AsyncTask<Void,Void,String>(){

        override fun doInBackground(vararg params: Void?): String {
            //  Logout of server
            Server.out.println("DELE|${Server.user}")
            Server.out.flush()
            return "done"
        }
    }

    inner class ConnectServerClass(val msg:String): AsyncTask<Void,Void,String>(){

        override fun doInBackground(vararg params: Void?): String {

            // Check for a link
            if (msg.startsWith("https://"))
                Server.send("LINK|${Server.user}|$msg")

            //  Must be normal text
            else {
                Server.send("TEXT|${Server.user}|$msg")
            }
            return ""
        }
    }
}