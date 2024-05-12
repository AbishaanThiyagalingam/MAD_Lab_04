package com.example.mad_lab_04_app

import android.content.DialogInterface


interface DialogCloseListener {
    fun handleDialogClose(dialog: DialogInterface?)
}
