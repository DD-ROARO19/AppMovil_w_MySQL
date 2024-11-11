package com.example.actividad10

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException

class ListadoActivity : AppCompatActivity() {

    //Instancias
    private lateinit var etListado: TextView
    private lateinit var btnRegresar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listado)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Asociamos el componente gráfico
        etListado = findViewById(R.id.txtDetalle)
        btnRegresar = findViewById(R.id.btnRegresar)

        //Llamada al método para mostrar los registros de la BD
        listarContactos()
        btnRegresar.setOnClickListener { regresar() }

    }//onCreate

    private fun listarContactos() {
        // Instancia para obtener los datos del servidor
        val cliente = AsyncHttpClient()
        cliente.get("http://192.168.100.17/practica10/androidConsultaMySql.php", object :
            AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>?,
                responseBody: ByteArray?
            ) {
                // El código 200 indica que hubo registros
                if (statusCode == 200) {
                    try {
                        val response = String(responseBody ?: ByteArray(0))

                        // Si existen registros como resultado de la búsqueda
                        if (response != "0") {
                            val contactos = JSONArray(response)

                            // Usamos un StringBuilder para ir concatenando las respuestas
                            val listado = StringBuilder()

                            // Ciclo para colocar la información dentro del TextView
                            for (i in 0 until contactos.length()) {
                                val contacto = contactos.getJSONObject(i)

                                listado.append("Contacto #${i + 1}\n")
                                listado.append("Nombre: ${contacto.getString("Nombre")}\n")
                                listado.append("Apellidos: ${contacto.getString("Apellidos")}\n")
                                listado.append("Telefono: ${contacto.getString("Telefono")}\n")
                                listado.append("Correo: ${contacto.getString("Email")}\n\n")
                            }

                            // Actualizamos el TextView con la lista de contactos
                            etListado.text = listado.toString()

                        } else {
                            Toast.makeText(
                                this@ListadoActivity, "Contacto no encontrado.", Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@ListadoActivity, "Error al obtener información.", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this@ListadoActivity, "Sin resultados en búsqueda.", Toast.LENGTH_SHORT).show()
                }
            } //onSuccess

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Toast.makeText(this@ListadoActivity, "Error de conexión: ${error?.message}", Toast.LENGTH_SHORT).show()
            } //onFailure
        })
    }//listarContactos

    private fun regresar() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }//regresar

}//Class