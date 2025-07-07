package com.rizqi.wideloc.presentation.ui.dialogs

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.rizqi.wideloc.R
import com.rizqi.wideloc.databinding.DialogAddMapBinding

class AddMapDialog(
    private val context: Context,
    private val onPickImage: () -> Unit,
    private val onSave: (name: String, imageUri: Uri?) -> Unit
) {
    private val binding: DialogAddMapBinding = DialogAddMapBinding.inflate(
        android.view.LayoutInflater.from(context),
        null,
        false
    )
    private var dialog: AlertDialog? = null
    private var selectedImageUri: Uri? = null

    fun show() {
        dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(true)
            .create()

        // Show dialog

        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        dialog?.show()

        binding.addImageLayoutDialogAddMap.setOnClickListener {
            onPickImage()
        }

        binding.editImageButtonDialogAddMap.setOnClickListener {
            onPickImage()
        }

        binding.deleteImageButtonDialogAddMap.setOnClickListener {
            selectedImageUri = null
            binding.mapImageImageViewDialogAddMap.setImageDrawable(null)
            binding.mapImageCardViewLayoutDialogAddMap.visibility = View.GONE
            binding.addImageLayoutDialogAddMap.visibility = View.VISIBLE
        }

        binding.saveButtonDialogAddMap.setOnClickListener {
            binding.mapNameInputLayoutDialogAddMap.error = null
            val name = binding.mapNameInputEditTextDialogAddMap.text.toString().trim()

            if (name.isEmpty()){
                binding.mapNameInputLayoutDialogAddMap.error = context.getString(R.string.name_cant_be_empty)
                return@setOnClickListener
            }

            if (selectedImageUri == null) {
                Toast.makeText(context, context.getString(R.string.select_an_image_of_your_map), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            onSave(name, selectedImageUri)
            dialog?.dismiss()
        }
    }

    fun updateImage(uri: Uri) {
        selectedImageUri = uri
        binding.mapImageImageViewDialogAddMap.setImageURI(uri)
        binding.mapImageCardViewLayoutDialogAddMap.visibility = View.VISIBLE
        binding.addImageLayoutDialogAddMap.visibility = View.GONE
    }
}
