package com.example.joshuaschappel.clientuserchat

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

class MessageAdapter(val list:MutableList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("Type",viewType.toString())
         when(viewType) {

             // 0: Add the user message to view
             0-> {
                 val view: View = LayoutInflater.from(parent.context).inflate(R.layout.message_status, parent, false)
                 val hold = ViewHolder2(view)
                 hold.bind()
                 return hold
             }

             // 1: Add user left message to view
             1-> {
                 val view: View = LayoutInflater.from(parent.context).inflate(R.layout.message_status, parent, false)
                 val hold = ViewHolder2(view)
                 hold.bind()
                 return hold
             }

             // 2: Add message sent from android to view
             2-> {
                 val view: View = LayoutInflater.from(parent.context).inflate(R.layout.message_layout, parent, false)
                 val hold = ViewHolder1(view)
                 hold.bind()
                 return hold
             }

             // else: Add message received from server to view
             else-> {
                 val view: View = LayoutInflater.from(parent.context).inflate(R.layout.message_from_row, parent, false)
                 val hold = ViewHolder1(view)
                 hold.bind()
                 return hold
            }
        }
    }

    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {
        val message: Message = list[position]

        when(holder){
            is ViewHolder1 ->{
                decideColorOfText(message, holder)
                holder.messageField.text = message.msg
                holder.senderField.text = message.user
            }
            is ViewHolder2 -> {

                if(message.msgType == 1) {
                    holder.messageField.text = "${message.user} has left the chat"
                }
                else {
                    holder.messageField.text = "${message.user} has joined the chat"
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = list[position]
        val origin = list[position].origin

        if (origin){
            Log.d("Here","Here")
            return 2
        }

        else
            // Message is from the server so lets see what type it is
            when(message.msgType){

                0-> return 0    // User added case
                1-> return 1    // User removed case
                else -> return 3    // Text from then server case
            }
    }

    fun addMessage(msg:Message){
        list.add(msg)
        notifyDataSetChanged()
    }

    fun decideColorOfText(aMessage: Message,holder: MessageAdapter.ViewHolder1){

        if (aMessage.origin){//  Is from the user
            holder.relLayout.apply {
                holder.messageField.setBackgroundResource(R.drawable.rounded_textview_user)
            }
        }
        //  Must be from the server
        else{
            holder.relLayout.apply {
                holder.messageField.setBackgroundResource(R.drawable.rounded_textview_server)
            }
        }
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind()
    }

    class ViewHolder1(itemView: View): ViewHolder(itemView){

        lateinit var messageField: TextView
        lateinit var senderField: TextView
        lateinit var relLayout: RelativeLayout

        override fun bind() {
            messageField = itemView.findViewById(R.id.message_field)
            senderField = itemView.findViewById(R.id.username_field)
            relLayout = itemView.findViewById(R.id.message_root)
        }

    }

    class ViewHolder2(itemView: View): ViewHolder(itemView){

        lateinit var messageField: TextView

        override fun bind() {
            messageField = itemView.findViewById(R.id.message_field)
        }

    }
}