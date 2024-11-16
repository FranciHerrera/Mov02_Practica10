package com.example.practica10mendozareyesangelemanuel

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class Listado : AppCompatActivity() {

    private lateinit var editDetalle: EditText
    private lateinit var btnRegresar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado)

        editDetalle = findViewById(R.id.editDetalle)
        btnRegresar = findViewById(R.id.btnRegresar)

        btnRegresar.setOnClickListener { finish() }

        FetchDataTask().execute()
    }

    private inner class FetchDataTask : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            return try {
                val url = URL("http://192.168.1.74/auto.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val postData = "action=select_all"
                connection.outputStream.write(postData.toByteArray())
                connection.outputStream.flush()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    "Failed to fetch data: $responseCode"
                }
            } catch (e: Exception) {
                e.message ?: "Unknown error"
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null && result.startsWith("[")) {
                val jsonArray = JSONArray(result)
                val stringBuilder = StringBuilder()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    stringBuilder.append("Placas: ${jsonObject.getInt("id")}\n")
                    stringBuilder.append("Marca: ${jsonObject.getString("marca")}\n")
                    stringBuilder.append("Modelo: ${jsonObject.getString("modelo")}\n")
                    stringBuilder.append("Año: ${jsonObject.getInt("anio")}\n")
                    stringBuilder.append("Color: ${jsonObject.getString("color")}\n")
                    stringBuilder.append("Precio: ${jsonObject.getDouble("precio")}\n")
                    stringBuilder.append("\n")
                }
                editDetalle.setText(stringBuilder.toString())
            } else {
                Toast.makeText(this@Listado, "No se pudieron obtener los datos", Toast.LENGTH_LONG).show()
            }
        }
    }
}