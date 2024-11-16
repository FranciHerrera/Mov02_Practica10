package com.example.practica10mendozareyesangelemanuel

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var user: EditText
    private lateinit var password: EditText
    private lateinit var Datasave: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user = findViewById(R.id.edtUsuario)
        password = findViewById(R.id.edtContrasenia)
        Datasave = findViewById(R.id.rbGuardarDatos)
    }

    fun onClick(view: View?) {
        when(view?.id) {
            R.id.btnIngresar -> ingresar()
            R.id.btnSalir -> salir()
            R.id.btnLimpiar -> limpiarCampos()
        }
    }

    private fun guardarPreferencias(user: User) {
        val preferences: SharedPreferences = getSharedPreferences("preferenciasUsuario", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("email", user.correo)
        editor.putString("password", user.contrasena)
        editor.putBoolean("guardado", user.guardado)
        editor.apply()
    }

    private fun salir() {
        finish()
    }

    private fun ingresar() {
        if (user.text.isNotEmpty() && password.text.isNotEmpty() && user.text.isNotBlank() && password.text.isNotBlank()) {
            val preferences: SharedPreferences = getSharedPreferences("preferenciasUsuario", MODE_PRIVATE)
            val savedEmail = preferences.getString("email", null)
            val savedPassword = preferences.getString("password", null)

            if (Datasave.isChecked) {
                val usr = User(user.text.toString(), password.text.toString(), true)
                guardarPreferencias(usr)
                Toast.makeText(this, "Se guardo en SharedPreferences", Toast.LENGTH_LONG).show()
                return
            }

            if (user.text.toString() == savedEmail && password.text.toString() == savedPassword) {
                val intent = Intent(applicationContext, Formulario::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No se encontró ese usuario", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Debes de llenar todos los campos", Toast.LENGTH_LONG).show()
        }
    }

    private fun limpiarCampos() {
        user.text.clear()
        password.text.clear()
        Datasave.isChecked = false
    }
}