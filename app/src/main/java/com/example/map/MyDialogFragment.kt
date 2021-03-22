package com.example.map

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class MyDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        builder.setMessage("Пожалуйста удалите одну из геолокаций")
                .setTitle("Слишком много геолокаций")
                .setNegativeButton(MapsActivity.name1) { dialogInterface, i -> (activity as MapsActivity).firstClicked() }
                .setPositiveButton(MapsActivity.name2) { dialogInterface, i -> (activity as MapsActivity).secondClicked() }
                .setNeutralButton("Отмена") { dialogInterface, i -> (activity as MapsActivity).thirdClicked() }

        return builder.create()
    }

}