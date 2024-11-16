package com.example.practica10mendozareyesangelemanuel

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Formulario : AppCompatActivity() {

    private lateinit var etId: EditText
    private lateinit var etMarca: EditText
    private lateinit var etModelo: EditText
    private lateinit var etAno: EditText
    private lateinit var etColor: EditText
    private lateinit var etPrecio: EditText
    private lateinit var btnAgregar: ImageButton
    private lateinit var btnBuscar: ImageButton
    private lateinit var btnActualizar: ImageButton
    private lateinit var btnEliminar: ImageButton
    private lateinit var btnLista: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        etId = findViewById(R.id.editTextId)
        etMarca = findViewById(R.id.editTextMarca)
        etModelo = findViewById(R.id.editTextModelo)
        etAno = findViewById(R.id.editTextAno)
        etColor = findViewById(R.id.editTextColor)
        etPrecio = findViewById(R.id.editTextPrecio)
        btnAgregar = findViewById(R.id.imageButtonAgregar)
        btnBuscar = findViewById(R.id.imageButtonBuscar)
        btnActualizar = findViewById(R.id.imageButtonActualizar)
        btnEliminar = findViewById(R.id.imageButtonEliminar)
        btnLista = findViewById(R.id.buttonLista)

        btnAgregar.setOnClickListener { registrarCarro() }
        btnBuscar.setOnClickListener { buscarCarro() }
        btnActualizar.setOnClickListener { actualizarCarro() }
        btnEliminar.setOnClickListener { eliminarCarro() }
        btnLista.setOnClickListener { listarRegistro() }
    }

    private fun listarRegistro() {
        val intent = Intent(this, Listado::class.java)
        startActivity(intent)
    }

    private fun eliminarCarro() {
        val id = etId.text.toString()
        if (id.isNotEmpty()) {
            SendDataTask().execute("delete", id)
        } else {
            Toast.makeText(this, "Ingresa la placa del carro", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarCarro() {
        val id = etId.text.toString()
        val marca = etMarca.text.toString()
        val modelo = etModelo.text.toString()
        val ano = etAno.text.toString()
        val color = etColor.text.toString()
        val precio = etPrecio.text.toString()
        if (id.isNotEmpty() && marca.isNotEmpty() && modelo.isNotEmpty() && ano.isNotEmpty() && color.isNotEmpty() && precio.isNotEmpty()) {
            SendDataTask().execute("update", id, marca, modelo, ano, color, precio)
        } else {
            Toast.makeText(this, "Debes registrar primero los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buscarCarro() {
        val id = etId.text.toString()
        if (id.isNotEmpty()) {
            SendDataTask().execute("select", id)
        } else {
            Toast.makeText(this, "Ingresa la placa del carro", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registrarCarro() {
        val id  = etId.text.toString()
        val marca = etMarca.text.toString()
        val modelo = etModelo.text.toString()
        val ano = etAno.text.toString()
        val color = etColor.text.toString()
        val precio = etPrecio.text.toString()
        if (marca.isNotEmpty() && modelo.isNotEmpty() && ano.isNotEmpty() && color.isNotEmpty() && precio.isNotEmpty()) {
            SendDataTask().execute("insert",id, marca, modelo, ano, color, precio)
        } else {
            Toast.makeText(this, "Debes registrar primero los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarCampos() {
        etId.text.clear()
        etMarca.text.clear()
        etModelo.text.clear()
        etAno.text.clear()
        etColor.text.clear()
        etPrecio.text.clear()
    }

    private inner class SendDataTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            return try {
                val action = params[0]
                val url = URL("http://192.168.1.74/auto.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                val postData = when (action) {
                    "insert" -> "action=insert&id=${params[1]}&marca=${params[2]}&modelo=${params[3]}&anio=${params[4]}&color=${params[5]}&precio=${params[6]}"
                    "select" -> "action=select&id=${params[1]}"
                    "update" -> "action=update&id=${params[1]}&marca=${params[2]}&modelo=${params[3]}&anio=${params[4]}&color=${params[5]}&precio=${params[6]}"
                    "delete" -> "action=delete&id=${params[1]}"
                    else -> ""
                }
                outputStreamWriter.write(postData)
                outputStreamWriter.flush()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    "Failed to send data: $responseCode"
                }
            } catch (e: Exception) {
                e.message ?: "Unknown error"
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            when {
                result?.contains("New record created successfully") == true -> {
                    Toast.makeText(this@Formulario, "Se agregó de manera correcta", Toast.LENGTH_LONG).show()
                    limpiarCampos()
                }
                result?.contains("Record updated successfully") == true -> {
                    Toast.makeText(this@Formulario, "Se actualizó de manera correcta", Toast.LENGTH_LONG).show()
                    limpiarCampos()
                }
                result?.contains("Record deleted successfully") == true -> {
                    Toast.makeText(this@Formulario, "Se eliminó de manera correcta", Toast.LENGTH_LONG).show()
                    limpiarCampos()
                }
                result?.contains("0 results") == true -> {
                    Toast.makeText(this@Formulario, "No se encontraron resultados", Toast.LENGTH_LONG).show()
                }
                result?.startsWith("{") == true -> {
                    val jsonObject = JSONObject(result)
                    etMarca.setText(jsonObject.getString("marca"))
                    etModelo.setText(jsonObject.getString("modelo"))
                    etAno.setText(jsonObject.getString("anio"))
                    etColor.setText(jsonObject.getString("color"))
                    etPrecio.setText(jsonObject.getString("precio"))
                    Toast.makeText(this@Formulario, "Datos cargados correctamente", Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(this@Formulario, result, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}