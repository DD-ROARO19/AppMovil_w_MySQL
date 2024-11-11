package com.example.actividad10

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    //Objetos de componentes gráficos
    private lateinit var etNumEmp: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etCorreo: EditText
    private lateinit var btnAgregar: ImageButton
    private lateinit var btnBuscar: ImageButton
    private lateinit var btnActualizar: ImageButton
    private lateinit var btnEliminar: ImageButton
    private lateinit var btnLista: Button

    //Gestionar operaciones con la BD
    private lateinit var requestQueue: RequestQueue

    //API del servidor MySQL
    private val url = "http://192.168.100.17/practica10/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Asociar los objetos con componentes gráficos
        etNumEmp = findViewById(R.id.txtNumEmp)
        etNombre = findViewById(R.id.txtNombre)
        etApellidos = findViewById(R.id.txtApellidos)
        etTelefono = findViewById(R.id.txtTelefono)
        etCorreo = findViewById(R.id.txtCorreo)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnBuscar = findViewById(R.id.btnBuscar)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnLista = findViewById(R.id.btnLista)

        // Inicializar gestor de operaciones
        requestQueue = Volley.newRequestQueue(this)

        btnAgregar.setOnClickListener {
            agregarContacto()
        }
        btnBuscar.setOnClickListener {
            buscarContacto()
        }
        btnActualizar.setOnClickListener{
            actualizarContacto()
        }
        btnEliminar.setOnClickListener {
            eliminarContacto()
        }
        btnLista.setOnClickListener {
            val intent = Intent(this, ListadoActivity::class.java)
            startActivity(intent)
        }
    }//onCreate

    private fun ejecutarWebService(url: String, msg: String) {
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Toast.makeText(this, "$msg: $response", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                println("Error en ejecutarWebService")
                println("Error: ${error.message}")
            }
        )
        requestQueue.add(stringRequest)
//        requestQueue.start()
    }//ejecutarWebService

    private fun agregarContacto() {
        val nombre = etNombre.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val email = etCorreo.text.toString().trim()

        // Validar que los campos no estén vacíos
        if (nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val urlCompleta = url + "androidInsercionMySql.php?nombre=" +
                etNombre.text.toString() + "&apellidos=" + etApellidos.text.toString() +
                "&telefono=" + etTelefono.text.toString() + "&email=" + etCorreo.text.toString()
        println("URL: $urlCompleta")
        ejecutarWebService(urlCompleta, "Contacto registrado")

        limpiarCampos()
    }//agregarContacto

    private fun buscarContacto() {
        val idContacto = etNumEmp.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()

        // Validar que al menos un campo esté lleno
        if (idContacto.isEmpty() && nombre.isEmpty() && apellidos.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese al menos un dato para la búsqueda", Toast.LENGTH_SHORT).show()
            return
        }
        if (nombre.isNotEmpty() || apellidos.isNotEmpty()){ //Verificar que se ingrese tanto nombre como apellidos al buscar por este metodo
            if (!(nombre.isNotEmpty() && apellidos.isNotEmpty())){
                Toast.makeText(this, "Por favor ingresar nombre y apellidos", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val urlParams = mutableListOf<String>()
        val urlParamsName = mutableListOf<String>()

        if (idContacto.isNotEmpty()) urlParams.add("idContacto=$idContacto")
        if (nombre.isNotEmpty()) urlParamsName.add("nombre=$nombre")
        if (apellidos.isNotEmpty()) urlParamsName.add("apellidos=$apellidos")

//        val urlCompleta = url + "androidConsultaIndividualMySql.php?" + urlParams.joinToString("&")
        var urlCompleta: String = ""
        if (urlParams.isNotEmpty()) urlCompleta = url + "androidConsultaIndividualMySql.php?" + urlParams.joinToString("&")
        if (urlParamsName.isNotEmpty()) urlCompleta = url + "androidBusquedaMySql.php?" + urlParamsName.joinToString("&")
        println("urlCompleta: $urlCompleta ")

        // Realizar la solicitud HTTP
        val cliente = AsyncHttpClient()
        cliente.get(urlCompleta, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                if (statusCode == 200) {
                    val response = String(responseBody ?: ByteArray(0))

                    if (response != "0") {
                        try {
                            // La respuesta será un solo objeto JSON, no un arreglo
                            val contacto = JSONObject(response)

                            etNumEmp.setText(contacto.getInt("idContacto").toString())
                            etNombre.setText(contacto.getString("Nombre"))
                            etApellidos.setText(contacto.getString("Apellidos"))
                            etTelefono.setText(contacto.getString("Telefono"))
                            etCorreo.setText(contacto.getString("Email"))
                        } catch (e: JSONException) {
                            Toast.makeText(this@MainActivity, "Error al procesar la información", Toast.LENGTH_SHORT).show()
                            println(e.message)
                            println(e)
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Contacto no encontrado.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Sin resultados en búsqueda.", Toast.LENGTH_SHORT).show()
                }
            }//onSuccess

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                Toast.makeText(this@MainActivity, "Error en la búsqueda: ${error?.message ?: "Error desconocido"}", Toast.LENGTH_SHORT).show()
            }//onFailure
        })
    }//buscarContacto

    private fun actualizarContacto() {
        // Obtener los valores de los campos de texto
        val idContacto = etNumEmp.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val email = etCorreo.text.toString().trim()

        // Validar que los campos no estén vacíos
        if (idContacto.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val urlCompleta = url + "androidActualizacionMySql.php?idContacto=$idContacto&nombre=$nombre&apellidos=$apellidos&telefono=$telefono&email=$email"
        println("URL: $urlCompleta")
        // Llamar al servicio web
        ejecutarWebService(urlCompleta, "Contacto actualizado")

        limpiarCampos()
    }//actualizarContacto

    private fun eliminarContacto() {
        val idContacto = etNumEmp.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val email = etCorreo.text.toString().trim()

        // Validar que los campos no estén vacíos
        if (idContacto.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val urlCompleta = url + "androidEliminacionMySql.php?nombre=" +
                etNombre.text.toString() + "&apellidos=" + etApellidos.text.toString()
        println("URL: $urlCompleta")
        ejecutarWebService(urlCompleta, "Contacto eliminado")
        limpiarCampos()
    }//eliminarContacto

    private fun limpiarCampos() {
        etNumEmp.setText("")
        etNombre.setText("")
        etApellidos.setText("")
        etTelefono.setText("")
        etCorreo.setText("")
        etNombre.requestFocus()
    }//limpiarCampos

}//Class