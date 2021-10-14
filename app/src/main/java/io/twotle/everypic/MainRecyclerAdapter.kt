package io.twotle.everypic

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.twotle.everypic.data.PicDTO
import io.twotle.everypic.databinding.ItemPicBinding
import io.twotle.everypic.upload.EditPicActivity

class MainRecyclerAdapter(val data: ArrayList<PicDTO>): RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ItemPicBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pic, parent, false)
        return ViewHolder(ItemPicBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.binding.ipImg
        //Glide.with(holder.itemView).load(Firebase.storage.reference.child(data[position].pictureUUID)).into(holder.binding.ipImg)
        val stor = Firebase.storage.reference

        stor.child(data[position].pictureUUID).downloadUrl.addOnSuccessListener {
            Glide.with(holder.itemView).load(it).into(holder.binding.ipImg)
        }
        holder.binding.ipImg.setOnClickListener {
            val intent = Intent(holder.binding.root.context, PicViewActivity::class.java)
            intent.putExtra("desc",data[position].description)
            intent.putExtra("uid",data[position].uid)
            intent.putExtra("photoUid",data[position].pictureUUID)
            holder.binding.root.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = data.size
}