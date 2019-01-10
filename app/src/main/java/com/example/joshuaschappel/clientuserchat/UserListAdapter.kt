package com.example.joshuaschappel.clientuserchat

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView

class UserListAdapter(val userList: List<String>): RecyclerView.Adapter<UserListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_user, parent, false)
        val hold = ViewHolder(view)
        hold.bind()
        return hold
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserListAdapter.ViewHolder, position: Int) {
        holder.userName.text = userList[position]
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        lateinit var userName: TextView
        lateinit var profilePic: CircleImageView

        fun bind(){
            userName = itemView.findViewById(R.id.user_username)
            profilePic = itemView.findViewById(R.id.profile_image)
        }
    }
}