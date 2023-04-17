package com.example.notes

import android.app.Activity.RESULT_OK
import android.app.job.JobInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.notes.database.NotesDatabase
import com.example.notes.databinding.FragmentCreateNoteBinding
import com.example.notes.entities.Notes
import com.example.notes.util.NoteBottomSheetFragment
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.android.synthetic.main.fragment_create_note.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_notes_bottom_sheet.*
import kotlinx.android.synthetic.main.item_rv_notes.view.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest
import java.util.regex.Pattern


class CreateNoteFragment : BaseFragment() , EasyPermissions.PermissionCallbacks , EasyPermissions.RationaleCallbacks  {

    var selectedColor = "#171C26"
    var currentData : String? = null
    private var READ_STORAGE_PERMISSION = 123
    private var REQUEST_CODE_IMAGE = 456
    private var selectedImagePath = ""
    private var webLink = ""
    private var noteId = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteId = requireArguments().getInt("noteId", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_create_note, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateNoteFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if(noteId != -1){

            launch {
                context?.let {
                    var notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                    etNoteTitle.setText(notes.title)
                    etNoteSubTitle.setText(notes.subTitle)
                    etNoteDesc.setText(notes.noteText)
                    if(notes.imgPath != ""){
                        imgNote.setImageBitmap(BitmapFactory.decodeFile(notes.imgPath))
                        layoutImage.visibility = View.VISIBLE
                        imgNote.visibility = View.VISIBLE
                        imgDelete.visibility = View.VISIBLE
                    }else{
                        layoutImage.visibility = View.GONE
                        imgNote.visibility = View.GONE
                        imgDelete.visibility = View.GONE
                    }

                    if(notes.webLink != ""){
                        tvWebLink.text = notes.webLink
                        layoutWebUrl.visibility = View.VISIBLE
                        etWebLink.setText(notes.webLink)
                    }else{
                        layoutWebUrl.visibility = View.GONE
                    }
                }
            }

        }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            broadcastReceiver, IntentFilter("bottom_sheet_action")
        )

        val simpleDateFormat = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        currentData = simpleDateFormat.format(Date())
        colorView.setBackgroundColor(Color.parseColor(selectedColor))


        tvDataTime.text = currentData

        imgDone.setOnClickListener {
            saveNote()
        }

        imgBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        imgMore.setOnClickListener {
            var noteBottomSheetFragment = NoteBottomSheetFragment.newInstance()
            noteBottomSheetFragment.show(requireActivity().supportFragmentManager,  "Note Bottom Sheet Fragment")
        }



        btnOk.setOnClickListener {
            if(etWebLink.text.toString().trim().isNotEmpty()){
                checkWebUrl()
            }else{
                Toast.makeText(requireContext(), "Url is Required", Toast.LENGTH_SHORT).show()
            }
        }
        btnCancel.setOnClickListener {
            layoutWebUrl.visibility = View.GONE
        }
        tvWebLink.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW , Uri.parse(etWebLink.text.toString()))
            startActivity(intent)
        }


    }
    private fun saveNote(){

        if(etNoteTitle.text.isNullOrEmpty()){
            Toast.makeText(context, "Note Title is Required" , Toast.LENGTH_SHORT).show()
        }

        else if(etNoteSubTitle.text.isNullOrEmpty()){
            Toast.makeText(context, "Note Sub Title is Required" , Toast.LENGTH_SHORT).show()
        }

        else if(etNoteDesc.text.isNullOrEmpty()){
            Toast.makeText(context, "Note description is Required" , Toast.LENGTH_SHORT).show()
        }
        else{

        launch {
            var notes = Notes()
            notes.title = etNoteTitle.text.toString()
            notes.subTitle = etNoteSubTitle.text.toString()
            notes.noteText = etNoteDesc.text.toString()
            notes.dataTime = currentData
            notes.color = selectedColor
            notes.imgPath = selectedImagePath
            notes.webLink = webLink

            context?.let {
                NotesDatabase.getDatabase(it).noteDao().insertNote(notes)
                etNoteTitle.setText("")
                etNoteSubTitle.setText("")
                etNoteDesc.setText("")
                layoutImage.visibility = View.GONE
                imgNote.visibility = View.GONE
                tvWebLink.visibility = View.GONE
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        }
    }

    private fun checkWebUrl(){
        if(Patterns.WEB_URL.matcher(etWebLink.text.toString()).matches()){
            layoutWebUrl.visibility = View.GONE
            etWebLink.isEnabled = false
            webLink = etWebLink.text.toString()
            tvWebLink.visibility = View.VISIBLE
            tvWebLink.text = etWebLink.text.toString()
        }else{
            Toast.makeText(requireContext(), "Url is not valid", Toast.LENGTH_SHORT).show()

        }
    }

    private val broadcastReceiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {

            var actionColor = p1!!.getStringExtra("action")

            when(actionColor!!){
                "Blue" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Yellow" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Purple" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Green" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Orange" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Black" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
                "Image" -> {
                    readStorageTask()
                    layoutWebUrl.visibility = View.GONE
                }
                "WebUrl" -> {
                    layoutWebUrl.visibility = View.VISIBLE
                }
                else -> {
                    layoutImage.visibility = View.GONE
                    imgNote.visibility = View.GONE
                    layoutWebUrl.visibility = View.GONE
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }
    private fun hasReadStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(requireContext() , android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    private fun hasWriteStoragePermission():Boolean{
        return EasyPermissions.hasPermissions(requireContext() , android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun readStorageTask(){
        if (hasReadStoragePermission()){

            pickImageFromGallery()

        }else{
            EasyPermissions.requestPermissions(
                this ,
                getString(R.string.storage_permission) ,
                READ_STORAGE_PERMISSION,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }
    private fun pickImageFromGallery(){
        var intent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if(intent.resolveActivity(requireActivity().packageManager) != null){
            startActivityForResult(intent  , REQUEST_CODE_IMAGE)
        }
    }

    private fun getPathFromUri(contentUri: Uri):String?{
        var filePath:String? = null
        var cursor = requireActivity().contentResolver.query(contentUri , null , null , null , null)
        if(cursor == null){
            filePath = contentUri.path
        }else{
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
           if(requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK){
               if (data != null){
                   var selectedImageURL = data.data
                   if (selectedImageURL != null){
                       try{
                           var inputStream = requireActivity().contentResolver.openInputStream(selectedImageURL)
                           var bitmap = BitmapFactory.decodeStream(inputStream)
                           imgNote.setImageBitmap(bitmap)
                           imgNote.visibility = View.VISIBLE
                           layoutImage.visibility = View.VISIBLE

                           selectedImagePath = getPathFromUri(selectedImageURL)!!
                       }catch (e: Exception){
                           Toast.makeText(requireContext() , e.message , Toast.LENGTH_SHORT).show()

                       }
                   }
               }
           }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode , permissions , grantResults , requireActivity())
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(requireActivity(),perms)){
            SettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        TODO("Not yet implemented")
    }

    override fun onRationaleAccepted(requestCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onRationaleDenied(requestCode: Int) {
        TODO("Not yet implemented")
    }
}