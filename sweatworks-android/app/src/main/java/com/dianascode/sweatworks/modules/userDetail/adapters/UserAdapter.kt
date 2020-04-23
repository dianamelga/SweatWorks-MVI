package com.dianascode.sweatworks.modules.userDetail.adapters

import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dianascode.sweatworks.R
import com.dianascode.sweatworks.models.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class UserAdapter(private val users: List<User>, private val ctx: Context, private var callback: (User) -> Unit = {}) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val windowManager = parent.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val user = users[position]
        Picasso.get()
            .load(user.picture?.thumbnail)
            .into(holder.picture)
        holder.viewItem?.setOnClickListener { callback(user) }

        if ("female" == user.gender?.toLowerCase()) {
            holder.picture?.borderColor = ContextCompat.getColor(ctx, R.color.pinkFemale)
        }else{
            holder.picture?.borderColor = ContextCompat.getColor(ctx, R.color.blueMale)
        }
    }


    class UserViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var picture: CircleImageView? = null
        var viewItem: View? = null

        init {
            picture = v.findViewById(R.id.ivUser)
            viewItem = v
        }
    }
}