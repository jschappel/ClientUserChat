package com.example.joshuaschappel.clientuserchat
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Button
import android.widget.EditText
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.Exception

class MainActivity : AppCompatActivity() {

    lateinit var textFieldUsername: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val submit = findViewById<Button>(R.id.submit)
        textFieldUsername = findViewById(R.id.editText_username)
        submit.setOnClickListener {
            if (!textFieldUsername.text.isEmpty())
                ConnectionClass(textFieldUsername, this).execute()
        }
    }


    inner class ConnectionClass(val editText:EditText, val context: Context) : AsyncTask<Void, Void, String>() {

        lateinit var user:String
        lateinit var alert: AlertDialog.Builder

        override fun onPreExecute() {
            super.onPreExecute()
            alert = AlertDialog.Builder(context)
        }

        override fun doInBackground(vararg params: Void?): String? {
            var serverStatus: String?
            try {

                Server.socket = Socket()
                Server.socket.connect(InetSocketAddress(Server.HOST,Server.PORT),1000)

                Server.out = PrintWriter(OutputStreamWriter(Server.socket.getOutputStream()))
                Server.inn = BufferedReader(InputStreamReader(Server.socket.getInputStream()))

                Server.out.println("CHAT VERSION ${Server.VERSION}")
                Server.out.flush()

                var serverResponse = readLine()

                //  Check the version
                if (!serverResponse.equals("CHAT VERSION OK")) {
                    serverStatus = "WRONG_VERSION"
                }
                else{

                    //We are no connected so we need to see if username works
                    val status = join()
                    if (status) {
                        serverStatus = "CONNECTED"
                        Server.connectionStatus = true
                    }
                    else{
                        serverStatus ="NOT_ALLOWED"
                    }
                }
            } catch (e: Exception){
                serverStatus = "NO_SERVER"
            }
            return serverStatus
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result.equals("CONNECTED")) {

                // move on the the next activity
                val intent = Intent(context,ChatActivity::class.java)
                startActivity(intent)
            }
            else if (result.equals("NOT_ALLOWED")) {
                alert.setTitle("Warning")
                alert.setMessage("This username is not allowed. Please try another one")
                alert.setPositiveButton("Ok"){dialog: DialogInterface?, which: Int ->
                }
                alert.show()
            }
            else if (result.equals("WRONG_VERSION")){
                alert.setTitle("Warning")
                alert.setMessage("The server version requested is wrong. Please make sure the app is up to date")
                alert.setPositiveButton("Ok"){dialog: DialogInterface?, which: Int ->
                }
                alert.show()
            }
            else{
                alert.setTitle("Warning")
                alert.setMessage("Can not connect to server!")
                alert.setPositiveButton("OK"){dialog, which ->
                }
                alert.show()
            }
        }

        @Throws(IOException::class)
        private fun readLine(): String {
            return Server.inn.readLine()
        }

        fun join(): Boolean{
            user = editText.text.toString()
            Server.user = user
            Server.send("JOIN|$user")
            val serverRes = readLine()
            Log.d("Server Respose", serverRes)
            if (serverRes.equals("JOIN OK"))
                return true
            return false
        }
    }
}