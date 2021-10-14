package io.twotle.everypic.auth

import android.graphics.Bitmap
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.twotle.everypic.data.PicDTO
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object FirebaseObj {
    var uid: String = ""
    var email: String = "" // this is display name
    var errorDesc : String = ""

    /*Local Var*/
    val storageRef = Firebase.storage.reference

    /* function */
    suspend fun postPic(bitmap: Bitmap, description: String) : Boolean{
        var result = false;
        try {
            val curTime : String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            val pUid : String = this.uid + "/" + curTime
            val picRef = storageRef.child(pUid)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = picRef.putBytes(data)
            uploadTask.addOnFailureListener {
                errorDesc = it.message.toString()
                result = false
            }.addOnSuccessListener { taskSnapshot ->
                result = true
            }.await()

            Firebase.firestore.collection(uid).document().set(hashMapOf(
                "pictureUUID" to pUid,
                "description" to description
            )).addOnSuccessListener {
                result = true
            }.addOnFailureListener {
                errorDesc = it.message.toString()
                result = false
            }.await()

            return result
        } catch(e: Exception) {
            errorDesc = e.message.toString()
            return false
        }



    }

    suspend fun editPic(postUid : String, editDesc: String) : Boolean {
        var result = false
        try {
            Firebase.firestore.collection(uid).document(postUid).set(
                hashMapOf(
                    "description" to editDesc
                ), SetOptions.merge()
            ).addOnSuccessListener {
                result = true
            }.addOnFailureListener {
                errorDesc = it.message.toString()
                result = false
            }.await()

            return result
        } catch (e: Exception) {
            errorDesc = e.message.toString()
            return false
        }



    }

    suspend fun getPicList() : ArrayList<PicDTO>? {
        var data: ArrayList<PicDTO> = ArrayList()
        try {
            Firebase.firestore.collection(uid).get().addOnSuccessListener {
                for(dt in it) {
                    data.add(PicDTO(
                        dt.data["pictureUUID"].toString(),
                        dt.data["description"].toString(),
                        dt.id
                    ))
                }
            }.addOnFailureListener {
                errorDesc = it.message.toString()
            }.await()

            return data
        } catch(e: Exception) {
            errorDesc = e.message.toString()
            return null
        }
    }

    suspend fun removePic(docUid: String, picUid: String): Boolean { //성공시 true 실패시 false
        var result = false
        return try {
            Firebase.firestore.collection(uid).document(docUid).delete().addOnSuccessListener {
                //returnD = true
            }.addOnFailureListener {
                errorDesc = it.message.toString()
                result = false
            }.await()
            if(!result) false

            Firebase.storage.reference.child(picUid).delete().addOnSuccessListener {
                result = true
            }.addOnFailureListener {
                errorDesc = it.message.toString()
                result = false
            }.await()
            result
        } catch (e: Exception) {
            errorDesc = e.message.toString()
            false
        }
    }
}
