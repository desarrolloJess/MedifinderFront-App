package com.example.medifinder_app

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.medifinder_app.apiservice.RetrofitClient
import com.example.medifinder_app.models.DatosPaciente
import com.example.medifinder_app.models.RegistroModel
import com.example.medifinder_app.models.RegistroResModel
import com.example.medifinder_app.models.UsuarioModel
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val USUARIO_ID = "usuarioId"

class PerfilFragment : Fragment() {
    private var usuarioId: String? = null

    private lateinit var editTextNombre : EditText
    private lateinit var editTextApellido : EditText
    private lateinit var editTextEmail : EditText
    private lateinit var editTextContrasena : EditText
    private lateinit var editTextTelefono : EditText
    private lateinit var editTextFechaNacimiento : EditText
    private lateinit var spinnerSexo : Spinner
    private lateinit var btnActualizarDatos : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuarioId = it.getString(USUARIO_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        editTextNombre = view.findViewById<EditText>(R.id.editTextNombre)
        editTextApellido = view.findViewById<EditText>(R.id.editTextApellido)
        editTextEmail = view.findViewById<EditText>(R.id.editTextEmail)
        editTextContrasena = view.findViewById<EditText>(R.id.editTextContrasena)
        editTextTelefono = view.findViewById<EditText>(R.id.editTextTelefono)
        spinnerSexo = view.findViewById<Spinner>(R.id.spinnerSexo)
        btnActualizarDatos = view.findViewById<Button>(R.id.btnActualizarDatos)


        editTextFechaNacimiento = view.findViewById<EditText>(R.id.editTextFechaNacimiento)

        // Configurar el DatePickerDialog
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Aquí se establece la fecha seleccionada en el EditText
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = sdf.format(selectedDate.time)
                editTextFechaNacimiento.setText(formattedDate)
            },
            year,
            month,
            day
        )

        // Mostrar el DatePickerDialog cuando se hace clic en el EditText de fecha
        editTextFechaNacimiento.setOnClickListener {
            datePickerDialog.show()
        }

        usuarioId?.let { id ->
            cargarDatosUsuario(id)
        }

        btnActualizarDatos.setOnClickListener {
            var nombre = editTextNombre.text.toString()
            var apellido = editTextApellido.text.toString()
            var email = editTextEmail.text.toString().trim()
            var contrasena = editTextContrasena.text.toString().trim()
            var telefono = editTextTelefono.text.toString().trim()
            var fechaNacimiento = editTextFechaNacimiento.text.toString().trim()
            var sexo = spinnerSexo.selectedItem.toString()


            if (validateFields(nombre,apellido,email,contrasena,telefono,fechaNacimiento,sexo)) {
                val parametros = RegistroModel(
                    nombre = nombre,
                    apellido = apellido,
                    email = email,
                    contrasena = contrasena,
                    telefono = telefono,
                    fechaNacimiento = fechaNacimiento,
                    sexo = sexo,
                    estatus = "1"
                )

                usuarioId?.let { id ->
                    actualizarDatosUsuarios(id, parametros)
                }

            } else {
                Toast.makeText(requireContext(), "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show()
            }

        }

        return view
    }

    private fun actualizarDatosUsuarios(id: String,parametros :RegistroModel){
        RetrofitClient.instance.modificarDatosPaciente(id, parametros).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {

                        val stringJson = response.body()?.string()
                        val gson = Gson()
                        val respuesta = gson.fromJson(stringJson, RegistroResModel::class.java)
                        Toast.makeText(requireContext(), " ${respuesta.message}", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e("PerfilFragment", "Exception parsing response body: ${e.message}")
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("PerfilFragment", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarDatosUsuario(id: String){
        RetrofitClient.instance.obtenerDatosPaciente(id).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val stringJson = response.body()?.string()
                        val gson = Gson()
                        val respuesta = gson.fromJson(stringJson, DatosPaciente::class.java)
                        asignarDatosCajas(respuesta)
                    } catch (e: Exception) {
                        Log.e("PerfilFragment", "Exception parsing response body: ${e.message}")
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("PerfilFragment", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun asignarDatosCajas(datos : DatosPaciente){
        editTextNombre.setText(datos.nombre)
        editTextApellido.setText(datos.apellido)
        editTextEmail.setText(datos.email)
        editTextContrasena.setText(datos.contrasena)
        editTextTelefono.setText(datos.telefono)
        editTextFechaNacimiento.setText(datos.fechaNacimiento)

        val adapter = spinnerSexo.adapter as ArrayAdapter<*>
        val sexoOptions = resources.getStringArray(R.array.sexo_options)
        val sexoIndex = sexoOptions.indexOfFirst { it.equals(datos.sexo, ignoreCase = true) }
        spinnerSexo.setSelection(sexoIndex)

    }

    private fun validateFields(nombre:String,apellido:String,email:String,contrasena:String,telefono:String,fechaNacimiento:String,sexo:String): Boolean {
        spinnerSexo = requireView().findViewById<Spinner>(R.id.spinnerSexo)

        return nombre.isNotBlank() &&
                apellido.isNotBlank() &&
                email.isNotBlank() &&
                contrasena.isNotBlank() &&
                telefono.isNotBlank() &&
                fechaNacimiento.isNotBlank() &&
                sexo.isNotBlank() &&
                spinnerSexo.selectedItemPosition != Spinner.INVALID_POSITION
    }


    companion object {
        @JvmStatic
        fun newInstance(usuarioId: String) =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putString(USUARIO_ID, usuarioId)
                }
            }
    }
}