package com.example.campustransaction.ui.posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.campustransaction.R
import com.example.campustransaction.api.RequestPost
import com.example.campustransaction.databinding.FragmentEditPostBinding
import com.example.campustransaction.databinding.FragmentPostsBinding
import com.example.campustransaction.ui.UIViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream







class EditPostFragment  : Fragment() {
    private val viewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentEditPostBinding

    private var lastPhoto = 0  // 记录上次选择的图片框

    // 删除符号的计时器
    private var timer1: TimerUnit? = null
    private var timer2: TimerUnit? = null
    private var timer3: TimerUnit? = null
    private var timer4: TimerUnit? = null

    // post的labels
    private val labelsArray = arrayOf(
        "Personal Computer",
        "Phone",
        "Digital",
        "Food&Drink",
        "Books",
        "Medicine",
        "Bags",
        "Sports",
        "Fruits",
        "Luxury",
        "Sports",
        "Auction",
        "Lost&Found",
        "Jewelry",
        "Watches"
    )
    private val checkedLabelsArray = booleanArrayOf(
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false
    )
    private lateinit var pid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pid = it.getString("pid") ?: throw IllegalStateException("POST_ID is required")
        }
        Log.d("EditPostFragment", "onCreate: received pid: $pid")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPostBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        if (viewModel.myNewPost == null) {
        viewModel.sdkOldPosts(pid)
        viewModel.responseOldPosts.observe(viewLifecycleOwner){
            binding.textTitle.setText(viewModel.responseOldPosts.value?.Title)
            binding.textDescription.setText(viewModel.responseOldPosts.value?.Text)
            binding.textPrice.setText(viewModel.responseOldPosts.value?.Price.toString())

            if (!viewModel.responseOldPosts.value?.Images?.getOrNull(0).isNullOrEmpty()){
                val photo1Base64 = viewModel.responseOldPosts.value?.Images?.getOrNull(0)

                val photo1 = photo1Base64?.let { convertBase64ToUri(it, requireContext()) }


                binding.buttonPhoto1.setImageURI(photo1);binding.buttonPhoto1.tag =
                "Selected"
                viewModel.postPhotoUri1=photo1
                Log.d("photo1","$photo1")}
            if(!viewModel.responseOldPosts.value?.Images?.getOrNull(1).isNullOrEmpty()){
                val photo2Base64 = viewModel.responseOldPosts.value?.Images?.getOrNull(1)
                val photo2 = photo2Base64?.let { convertBase64ToUri(it, requireContext()) }

                binding.buttonPhoto2.setImageURI(photo2);binding.buttonPhoto2.tag =
                    "Selected"
                viewModel.postPhotoUri2=photo2

            }

            if(!viewModel.responseOldPosts.value?.Images?.getOrNull(2).isNullOrEmpty()){
                val photo3Base64 = viewModel.responseOldPosts.value?.Images?.getOrNull(2)
                val photo3 = photo3Base64?.let { convertBase64ToUri(it, requireContext()) }

                binding.buttonPhoto3.setImageURI(photo3);binding.buttonPhoto3.tag =
                    "Selected"
                viewModel.postPhotoUri3=photo3
            }
            if (!viewModel.responseOldPosts.value?.Images?.getOrNull(3).isNullOrEmpty()){
                val photo4Base64 = viewModel.responseOldPosts.value?.Images?.getOrNull(3)
                val photo4 = photo4Base64?.let { convertBase64ToUri(it, requireContext()) }


                binding.buttonPhoto4.setImageURI(photo4);binding.buttonPhoto4.tag =
                    "Selected"
                viewModel.postPhotoUri4=photo4
            }

            //Log.d("photo1","$photo1")




        }}

        // 重现上次保存的内容, 文字和图片
        if (viewModel.myNewPost != null) {
            binding.textTitle.setText(viewModel.myNewPost!!.Title)
            binding.textDescription.setText(viewModel.myNewPost!!.Text)
            if (viewModel.myNewPost!!.Price != null) {
                binding.textPrice.setText(viewModel.myNewPost!!.Price.toString())
            }
        } else {
            binding.textTitle.setText("")
            binding.textDescription.setText("")
            binding.textPrice.setText("")
        }
        if (viewModel.postPhotoUri1 != null) {
            binding.buttonPhoto1.setImageURI(viewModel.postPhotoUri1);binding.buttonPhoto1.tag =
                "Selected"
        }
        if (viewModel.postPhotoUri2 != null) {
            binding.buttonPhoto2.setImageURI(viewModel.postPhotoUri2);binding.buttonPhoto2.tag =
                "Selected"
        }
        if (viewModel.postPhotoUri3 != null) {
            binding.buttonPhoto3.setImageURI(viewModel.postPhotoUri3);binding.buttonPhoto3.tag =
                "Selected"
        }
        if (viewModel.postPhotoUri4 != null) {
            binding.buttonPhoto4.setImageURI(viewModel.postPhotoUri4);binding.buttonPhoto4.tag =
                "Selected"
        }



        // 申请相机和图库读写权限
        context?.let { viewModel.onRequestPermissions(it) }

        // 图片点击
        binding.buttonPhoto1.setOnClickListener {
            lastPhoto = 1
            savePost()
            view?.findNavController()?.navigate(R.id.action_editPostFragment_to_photoFragment)
        }
        binding.buttonPhoto2.setOnClickListener {
            lastPhoto = 2
            savePost()
            view?.findNavController()?.navigate(R.id.action_editPostFragment_to_photoFragment)
        }
        binding.buttonPhoto3.setOnClickListener {
            lastPhoto = 3
            savePost()
            view?.findNavController()?.navigate(R.id.action_editPostFragment_to_photoFragment)
        }
        binding.buttonPhoto4.setOnClickListener {
            lastPhoto = 4
            savePost()
            view?.findNavController()?.navigate(R.id.action_editPostFragment_to_photoFragment)
        }

        // 长按显示删除图片的按钮
        binding.buttonPhoto1.setOnLongClickListener {
            showDeleteButton()
            return@setOnLongClickListener true
        }
        binding.buttonPhoto2.setOnLongClickListener {
            showDeleteButton()
            return@setOnLongClickListener true
        }
        binding.buttonPhoto3.setOnLongClickListener {
            showDeleteButton()
            return@setOnLongClickListener true
        }
        binding.buttonPhoto4.setOnLongClickListener {
            showDeleteButton()
            return@setOnLongClickListener true
        }

        // 点击删除当前图片
        binding.buttonDelete1.setOnClickListener {
            binding.buttonPhoto1.setImageResource(R.drawable.choose_photo)
            binding.buttonPhoto1.tag = "unSelected"
            viewModel.postPhotoUri1 = null
            timer1!!.endTime()
            timer1 = null
        }
        binding.buttonDelete2.setOnClickListener {
            binding.buttonPhoto2.setImageResource(R.drawable.choose_photo)
            binding.buttonPhoto2.tag = "unSelected"
            viewModel.postPhotoUri2 = null
            timer2!!.endTime()
            timer2 = null
        }
        binding.buttonDelete3.setOnClickListener {
            binding.buttonPhoto3.setImageResource(R.drawable.choose_photo)
            binding.buttonPhoto3.tag = "unSelected"
            viewModel.postPhotoUri3 = null
            timer3!!.endTime()
            timer3 = null
        }
        binding.buttonDelete4.setOnClickListener {
            binding.buttonPhoto4.setImageResource(R.drawable.choose_photo)
            binding.buttonPhoto4.tag = "unSelected"
            viewModel.postPhotoUri4 = null
            timer4!!.endTime()
            timer4 = null
        }

        // 选择帖子的label
        binding.buttonLabel.setOnClickListener {
            val builder = context?.let { it1 -> AlertDialog.Builder(it1) }

            builder?.setTitle("Select labels")
            builder?.setMultiChoiceItems(labelsArray, checkedLabelsArray) { _, which, isChecked ->
                checkedLabelsArray[which] = isChecked
                //Toast.makeText(context, "$labelsList[which] $isChecked", Toast.LENGTH_SHORT).show()
            }

            builder?.setPositiveButton("OK") { _, _ -> }
            builder?.setNeutralButton("Cancel") { _, _ -> }
            val dialog = builder?.create()
            dialog?.show()
        }


        // 如果viewModel里的图片的Uri被改变，则记录新的头像的Uri并重新加载图片
        viewModel.photoUri.observe(viewLifecycleOwner) {
            if (viewModel.photoUri.value != null) {
                when (lastPhoto) {
                    1 -> {
                        viewModel.postPhotoUri1 = viewModel.photoUri.value
                        binding.buttonPhoto1.setImageURI(viewModel.postPhotoUri1)
                        binding.buttonPhoto1.tag = "Selected"
                        lastPhoto = 0
                    }

                    2 -> {
                        viewModel.postPhotoUri2 = viewModel.photoUri.value
                        binding.buttonPhoto2.setImageURI(viewModel.postPhotoUri2)
                        binding.buttonPhoto2.tag = "Selected"
                        lastPhoto = 0
                    }

                    3 -> {
                        viewModel.postPhotoUri3 = viewModel.photoUri.value
                        binding.buttonPhoto3.setImageURI(viewModel.postPhotoUri3)
                        binding.buttonPhoto3.tag = "Selected"
                        lastPhoto = 0
                    }

                    4 -> {
                        viewModel.postPhotoUri4 = viewModel.photoUri.value
                        binding.buttonPhoto4.setImageURI(viewModel.postPhotoUri4)
                        binding.buttonPhoto4.tag = "Selected"
                        lastPhoto = 0
                    }
                }
            }
        }

        // Upload按钮监听
        binding.buttonPost.setOnClickListener {
            if (binding.textTitle.text == null || binding.textTitle.text.toString() == "") {
                Toast.makeText(context, "You must have a Title", Toast.LENGTH_SHORT).show()
            } else if (binding.textDescription.text == null || binding.textDescription.text.toString() == "") {
                Toast.makeText(context, "You must have a Text", Toast.LENGTH_SHORT).show()
            } else if (binding.textPrice.text == null || binding.textPrice.text.toString() == "") {
                Toast.makeText(context, "You must have a Price", Toast.LENGTH_SHORT).show()
            } else {
                // 保存帖子




                    savePost()
                    // 保存图片
                    val base64FromPostPhoto1 = context?.let {
                        viewModel.postPhotoUri1?.let { it1 ->
                            viewModel.getBase64ForUri(
                                it,
                                it1
                            )
                        }
                    }
                    val base64FromPostPhoto2 = context?.let {
                        viewModel.postPhotoUri2?.let { it1 ->
                            viewModel.getBase64ForUri(
                                it,
                                it1
                            )
                        }
                    }
                    val base64FromPostPhoto3 = context?.let {
                        viewModel.postPhotoUri3?.let { it1 ->
                            viewModel.getBase64ForUri(
                                it,
                                it1
                            )
                        }
                    }
                    val base64FromPostPhoto4 = context?.let {
                        viewModel.postPhotoUri4?.let { it1 ->
                            viewModel.getBase64ForUri(
                                it,
                                it1
                            )
                        }
                    }
                    val photoList: MutableList<String> = mutableListOf()
                    if (base64FromPostPhoto1 != "") {
                        base64FromPostPhoto1?.let { it1 -> photoList.add(it1) }
                    }
                    if (base64FromPostPhoto2 != "") {
                        base64FromPostPhoto2?.let { it1 -> photoList.add(it1) }
                    }
                    if (base64FromPostPhoto3 != "") {
                        base64FromPostPhoto3?.let { it1 -> photoList.add(it1) }
                    }
                    if (base64FromPostPhoto4 != "") {
                        base64FromPostPhoto4?.let { it1 -> photoList.add(it1) }
                    }
                    viewModel.myNewPost?.Images = photoList
                    Log.d("sdkNewPost1", photoList.toString())

                    // 上传新帖子




                viewModel.viewModelScope.launch {
                    //viewModel.parandedit(pid) {
                        // 当 sdkParsingPID 完成时，这个回调将被调用
                        //viewModel.sdkFinalPost()


                    //}
                    Log.d("firstsdkTEditPost", viewModel.myNewPost.toString())
                    viewModel.sdkTEditPost(pid)
                }






                // binding.buttonPost.isClickable = false
                Toast.makeText(context, "New post uploaded", Toast.LENGTH_SHORT).show()
                clearPost()
            }
        }
        /*
        binding.buttonPost.setOnClickListener{
            viewModel.sdkFinalPost()


        }

         */



//        // 创建帖子的返回监听
//        viewModel.responseNewPost.observe(viewLifecycleOwner){
//            if (viewModel.responseNewPost.value?.Success == true){
//                Toast.makeText(context, "New post uploaded", Toast.LENGTH_SHORT).show()
//                clearPost()
//            }else{
//                Toast.makeText(context, "New post upload failed. Error:"+viewModel.responseNewPost.value?.Error, Toast.LENGTH_SHORT).show()
//            }
//            binding.buttonPost.isClickable = true
//        }


        return binding.root
    }


    // 离开时询问是否保存帖子
    override fun onDestroy() {
        super.onDestroy()
        // 判断有无草稿存在
        if (binding.textTitle.text.toString() != ""
            || binding.textDescription.text.toString() != ""
            || binding.textPrice.text.toString() != ""
            || binding.buttonPhoto1.tag.equals("Selected")
            || binding.buttonPhoto2.tag.equals("Selected")
            || binding.buttonPhoto3.tag.equals("Selected")
            || binding.buttonPhoto4.tag.equals("Selected")
        ) {
            // 询问保存框
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Save post draft?")
                .setMessage("Do you want to save this post draft?")
                .setCancelable(false)
                .setNegativeButton("DON'T SAVE") { _, _ -> unSavePost() }
                .setPositiveButton("SAVE") { _, _ -> savePost() }
                .show()
        }
    }

    // 保存帖子
    private fun savePost() {
        viewModel.myNewPost = RequestPost(
            PostOwner = viewModel.myUserInfo.EmailAddress,
            Title = binding.textTitle.text.toString(),
            Text = binding.textDescription.text.toString(),
            Price = binding.textPrice.text.toString().toDoubleOrNull()
        )
        when (binding.spinnerType.selectedItem.toString()) {
            "Auction" -> viewModel.myNewPost!!.Auction = true
            "Lost&Found" -> viewModel.myNewPost!!.LostFound = true
        }

        for (i in checkedLabelsArray.indices) {
            if (checkedLabelsArray[i]) {
                if (viewModel.myNewPost!!.Fields == null) {
                    viewModel.myNewPost!!.Fields = mutableListOf()
                }
                viewModel.myNewPost!!.Fields?.add(labelsArray[i])
            }
        }

        Log.d("PostsFragment", "savePost")
    }

    // 不保存帖子
    private fun unSavePost() {
        viewModel.postPhotoUri1 = null
        viewModel.postPhotoUri2 = null
        viewModel.postPhotoUri3 = null
        viewModel.postPhotoUri4 = null
        viewModel.myNewPost = null
        Log.d("PostsFragment", "unSavePost")
    }

    // 不保存帖子
    private fun clearPost() {
        unSavePost()
        binding.textTitle.text = null
        binding.textDescription.text = null
        binding.textPrice.text = null
        binding.buttonPhoto1.setImageResource(R.drawable.choose_photo)
        binding.buttonPhoto2.setImageResource(R.drawable.choose_photo)
        binding.buttonPhoto3.setImageResource(R.drawable.choose_photo)
        binding.buttonPhoto4.setImageResource(R.drawable.choose_photo)
        binding.buttonPhoto1.tag = "unSelected"
        binding.buttonPhoto2.tag = "unSelected"
        binding.buttonPhoto3.tag = "unSelected"
        binding.buttonPhoto4.tag = "unSelected"
        viewModel.postPhotoUri1 = null
        viewModel.postPhotoUri2 = null
        viewModel.postPhotoUri3 = null
        viewModel.postPhotoUri4 = null
        lastPhoto = 0
    }


    // 长按显示删除按钮
    private fun showDeleteButton() {
        // 给删除按钮绑定timer
        Log.d(
            "showDeleteButton",
            binding.buttonPhoto1.tag.toString() + " " + binding.buttonPhoto2.tag.toString() + " " + binding.buttonPhoto3.tag.toString() + " " + binding.buttonPhoto4.tag.toString()
        )
        if (binding.buttonPhoto1.tag.equals("Selected")) {
            timer1 = TimerUnit(binding.buttonDelete1)
            timer1?.startTime()
        }
        if (binding.buttonPhoto2.tag.equals("Selected")) {
            timer2 = TimerUnit(binding.buttonDelete2)
            timer2?.startTime()
        }
        if (binding.buttonPhoto3.tag.equals("Selected")) {
            timer3 = TimerUnit(binding.buttonDelete3)
            timer3?.startTime()
        }
        if (binding.buttonPhoto4.tag.equals("Selected")) {
            timer4 = TimerUnit(binding.buttonDelete4)
            timer4?.startTime()
        }

    }
    fun convertBase64ToBitmap(base64Str: String): Bitmap? {
        val decodedBytes: ByteArray = Base64.decode(base64Str.substring(base64Str.indexOf(",") + 1), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun convertBase64ToUri(base64Str: String, context: Context): Uri? {
        // 解码Base64
        val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        // 创建文件来保存图片
        val imagesFolder = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(imagesFolder, "temp_image_${System.currentTimeMillis()}.jpg")

        // 将Bitmap保存为文件
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }

        // 返回文件的Uri
        return Uri.fromFile(file)
    }



}
