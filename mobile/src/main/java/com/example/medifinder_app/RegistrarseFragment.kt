package com.example.medifinder_app

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.medifinder_app.apiservice.RetrofitClient
import com.example.medifinder_app.config.SessionManager
import com.example.medifinder_app.models.RegistroModel
import com.example.medifinder_app.models.RegistroResModel
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegistrarseFragment : Fragment() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextContrasena: EditText
    private lateinit var editTextTelefono: EditText
    private lateinit var editTextFechaNacimiento: EditText
    private lateinit var txtRegresar: TextView
    private lateinit var spinnerSexo: Spinner
    private lateinit var buttonRegistrarse: Button

    lateinit var sessionManager: SessionManager
    lateinit var contexto: Context
    private lateinit var navigationView: NavigationView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contexto = requireContext()
        sessionManager = SessionManager(contexto)

        val view = inflater.inflate(R.layout.fragment_registrarse, container, false)

        editTextNombre = view.findViewById(R.id.editTextNombre)
        editTextApellido = view.findViewById(R.id.editTextApellido)
        editTextEmail = view.findViewById(R.id.editTextEmail)
        editTextContrasena = view.findViewById(R.id.editTextContrasena)
        editTextTelefono = view.findViewById(R.id.editTextTelefono)
        txtRegresar = view.findViewById(R.id.txtRegresar)

        spinnerSexo = view.findViewById(R.id.spinnerSexo)
        buttonRegistrarse = view.findViewById(R.id.btnRegistrarse)

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

        editTextFechaNacimiento.setOnClickListener {
            datePickerDialog.show()
        }


        txtRegresar.setOnClickListener{
            cargarFragmentoIniciarSesion("")
        }

        buttonRegistrarse.setOnClickListener {
            validarYRegistrar()
        }

        return view
    }

    private fun validarYRegistrar() {
        val nombre = editTextNombre.text.toString().trim()
        val apellido = editTextApellido.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val contrasena = editTextContrasena.text.toString().trim()
        val telefono = editTextTelefono.text.toString().trim()
        val fechaNacimiento = editTextFechaNacimiento.text.toString().trim()
        val sexo = spinnerSexo.selectedItem.toString()

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || contrasena.isEmpty()
            || telefono.isEmpty() || fechaNacimiento.isEmpty() || sexo.isEmpty()) {
            Toast.makeText(requireContext(), "Faltan campos por completar", Toast.LENGTH_SHORT).show()
        } else {
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
            registrarUsuario(parametros)
        }
    }

    private fun cargarFragmentoIniciarSesion(email: String) {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        requireActivity().finish()
    }


    private fun registrarUsuario(parametros : RegistroModel) {

        Toast.makeText(requireContext(), "Datos enviados", Toast.LENGTH_SHORT).show()

        RetrofitClient.instance.registrarPaciente(parametros).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {

                        val stringJson = response.body()?.string()
                        val gson = Gson()
                        val respuesta = gson.fromJson(stringJson, RegistroResModel::class.java)
                        Toast.makeText(requireContext(), " ${respuesta.message}", Toast.LENGTH_SHORT).show()
                        cargarFragmentoIniciarSesion(parametros.email);
                    } catch (e: Exception) {
                        Log.e("RegistrarseFragment", "Exception parsing response body: ${e.message}")
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("RegistrarseFragment", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}