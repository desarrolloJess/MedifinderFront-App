package com.example.medifinder_app

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.medifinder_app.apiservice.RetrofitClient
import com.example.medifinder_app.models.CancelarCitaPacienteModel
import com.example.medifinder_app.models.CitasPacienteModel
import com.example.medifinder_app.models.DoctoresModel
import com.example.medifinder_app.models.RegistroCalificacionMedico
import com.example.medifinder_app.models.RegistroModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

private const val USUARIO_ID = "usuarioId"

class CitasListaFragment : Fragment() {
    private var usuarioId: String? = null;
    private lateinit var citasContainer: LinearLayout;
    private lateinit var spinnerEstatus: Spinner
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
        val view = inflater.inflate(R.layout.fragment_short, container, false)
        citasContainer = view.findViewById(R.id.citasContainer)
        spinnerEstatus = view.findViewById(R.id.spinnerEstatus)

        setupSpinner()
        usuarioId?.let { id ->
            obtenerCitasPaciente(id.toInt());
        }

        return view
    }

    private fun obtenerCitasPaciente(idPaciente: Int) {
        RetrofitClient.instance.obtenerCitasPacientes(idPaciente).enqueue(object : Callback<List<CitasPacienteModel>> {
            override fun onResponse(call: Call<List<CitasPacienteModel>>, response: Response<List<CitasPacienteModel>>) {
                if (response.isSuccessful) {
                    try {
                        val citasPacienteList = response.body() ?: emptyList();
                        mostrarCitas(citasPacienteList);
                    } catch (e: Exception) {
                        Log.e("CitasListaFragment", "Exception parsing response body: ${e.message}")
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CitasPacienteModel>>, t: Throwable) {
                Log.e("CitasListaFragment", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun obtenerCitasPacientePorEstatus(idPaciente: Int, estatusCita: Int) {
        if (estatusCita == -1){
            obtenerCitasPaciente(idPaciente);
        }else{
            RetrofitClient.instance.obtenerCitasPorPacientesYEstatus(idPaciente,estatusCita).enqueue(object : Callback<List<CitasPacienteModel>> {
                override fun onResponse(call: Call<List<CitasPacienteModel>>, response: Response<List<CitasPacienteModel>>) {
                    if (response.isSuccessful) {
                        try {
                            val citasPacienteList = response.body() ?: emptyList();
                            citasContainer.removeAllViews()
                            if (citasPacienteList.isEmpty()){
                                showToast("El paciente no cuenta con registros de citas.")
                            }else{
                                mostrarCitas(citasPacienteList);
                            }

                        } catch (e: Exception) {
                            Log.e("CitasListaFragment", "Exception parsing response body: ${e.message}")
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<CitasPacienteModel>>, t: Throwable) {
                    Log.e("CitasListaFragment", "Failure: ${t.message}")
                    Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun actualizarCitas(){
        usuarioId?.let { id ->
            obtenerCitasPaciente(id.toInt());
        }
    }
    private fun mostrarCitas(citasPacienteList: List<CitasPacienteModel>) {
        citasContainer.removeAllViews()

        for (citasPaciente in citasPacienteList) {
            // Iterar sobre cada cita del paciente
            for (cita in citasPaciente.citas) {
                val citaView = layoutInflater.inflate(R.layout.item_cita, null)


                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val dateOutputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val timeOutputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                val fechaInicio = inputFormat.parse(cita.fechaInicio)
                val fechaFin = inputFormat.parse(cita.fechaFin)
                val fechaInicioFormateada = dateOutputFormat.format(fechaInicio)
                val horaInicioFormateada = timeOutputFormat.format(fechaInicio)
                val horaFinFormateada = timeOutputFormat.format(fechaFin)

                val estatus = when (cita.estatus) {
                    "0" -> "Pendiente por confirmar"
                    "1" -> "Confirmada por médico"
                    "2" -> "Confirmada por médico y paciente"
                    "3" -> "Cancelada por médico"
                    "4" -> "Cancelada por paciente"
                    "5" -> "Cita Terminada"
                    else -> "Desconocido"
                }

                val colorEstatus = when (cita.estatus) {
                    "0" -> ContextCompat.getColor(requireContext(), R.color.grisS)      //Pendiente por confirmar
                    "1" -> ContextCompat.getColor(requireContext(), R.color.azulS)      //Confirmada por médico
                    "2" -> ContextCompat.getColor(requireContext(), R.color.verdeS)     //Confirmada por médico y paciente
                    "3" -> ContextCompat.getColor(requireContext(), R.color.rojoS)      //Cancelada por médico
                    "4" -> ContextCompat.getColor(requireContext(), R.color.naranjaS)   //Cancelada por paciente
                    "5" -> ContextCompat.getColor(requireContext(), R.color.amarilloS)  //Cita Terminada
                    else -> ContextCompat.getColor(requireContext(), R.color.black)
                }
                citaView.findViewById<View>(R.id.colorEstatus).backgroundTintList = ColorStateList.valueOf(colorEstatus)



                citaView.findViewById<TextView>(R.id.textViewFecha).text = fechaInicioFormateada ?: ""
                citaView.findViewById<TextView>(R.id.textViewHora).text = "${horaInicioFormateada} - ${horaFinFormateada}" ?: ""
                citaView.findViewById<TextView>(R.id.textViewNombre).text = "Dr./Dra. ${cita.nombreMedico} ${cita.apellidoMedico}"
                citaView.findViewById<TextView>(R.id.textViewDireccion).text = "Dirección. ${cita.direccionMedico}"
                citaView.findViewById<TextView>(R.id.textViewEstatus).text = "Estatus: ${estatus}" ?: "Estatus: "

                val starContainer = citaView.findViewById<LinearLayout>(R.id.starContainer)
                val stars = arrayOf(
                    starContainer.findViewById<ImageView>(R.id.star1),
                    starContainer.findViewById<ImageView>(R.id.star2),
                    starContainer.findViewById<ImageView>(R.id.star3),
                    starContainer.findViewById<ImageView>(R.id.star4),
                    starContainer.findViewById<ImageView>(R.id.star5)
                )

                val txtAreaCalificacion = citaView.findViewById<TextView>(R.id.txtAreaCalificacion)
                if (cita.estatus == "5") {
                    starContainer.visibility  = View.VISIBLE
                    txtAreaCalificacion.visibility = View.VISIBLE
                    obtenerCalificacionPorCita(cita.id.toInt()) { calificacion ->
                        if (calificacion > 0) {
                            txtAreaCalificacion.text = "Calificación: "
                            setStarRating(stars, calificacion)
                        } else {
                            txtAreaCalificacion.text = "Calificar Cita: "
                            clearStarRating(stars)
                        }
                    }
                } else {
                    starContainer.visibility  = View.GONE
                    txtAreaCalificacion.visibility = View.GONE
                }

                citaView.setOnClickListener {
                    validarRespuestaCita(cita.estatus.toInt(),cita.id.toInt());
                }

                // Agregar la vista de cita al contenedor
                citasContainer.addView(citaView)
            }
        }
    }

    private fun validarRespuestaCita(estatusCita:Int,idCita: Int){
        when (estatusCita) {
            //Pendiente por confirmar
            0 -> { showToast("La cita esta pendiente de aprobación por parte del médico") }
            //Confirmada por médico
            1 -> { verDialogoConfirmarCitaPaciente(idCita) }
            //Confirmada por médico y paciente
            2 -> { validaQuiereCancelar(idCita) }
            //Cancelada por médico
            3 -> { showToast("La cita fue cancelada por el médico.") }
            //Cancelada por paciente
            4 -> { showToast("Cancelaste la cita.") }
            //Cita Terminada
            5 -> { existeCalificacionParaCita(idCita) }
            else -> ContextCompat.getColor(requireContext(), R.color.black)
        }
    }

    // Funciones para confiramar cita -------------------------------------------------------------------------------------------------------------------------
    private fun verDialogoConfirmarCitaPaciente(idCita :Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmación")
        builder.setMessage("La cita ha sido confirmada por el médico. Le solicitamos que confirme su asistencia presionando el botón 'Confirmar'.")
        builder.setPositiveButton("Confirmar") { dialog, _ ->
            confirmarCitaPaciente(idCita);
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun confirmarCitaPaciente(idCita:Int) {
        RetrofitClient.instance.confirmarCitaPaciente(idCita).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val stringJson = response.body()?.string();
                        actualizarCitas();
                        showToast("Cita confirmada con éxito.");

                    } catch (e: Exception) {
                        Log.e("CitasListaFragment", "Exception parsing response body: ${e.message}")
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("CitasListaFragment", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Funciones para cancelar cita -------------------------------------------------------------------------------------------------------------------------
    private fun validaQuiereCancelar(idCita: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cita Programada")
        builder.setMessage("La cita ha sido confirmada por ambos, el médico y el paciente. Ahora, solo queda asistir en la fecha programada.")

        builder.setPositiveButton("Cancelar Cita") { dialog, _ ->
            mostrarDialogoMotivoCancelacion(idCita);
            dialog.dismiss()
        }
        builder.setNegativeButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun mostrarDialogoMotivoCancelacion(idCita: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cancelar Cita")
        builder.setMessage("Por favor, ingrese el motivo de la cancelación.")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        input.hint = "Motivo de la cancelación"
        builder.setView(input)

        builder.setPositiveButton("Cancelar Cita") { dialog, _ ->
            val motivoCancelacion = CancelarCitaPacienteModel(
                motivoCancelacion = input.text.toString()
            )
            cancelarCitaPaciente(idCita, motivoCancelacion);
            dialog.dismiss()
        }
        builder.setNegativeButton("Salir") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun cancelarCitaPaciente(idCita:Int,motivoCancelacion:CancelarCitaPacienteModel) {
        RetrofitClient.instance.cancelarCitaPaciente(idCita,motivoCancelacion).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val stringJson = response.body()?.string()
                        actualizarCitas();
                        showToast("Cita cancelada con éxito.");
                    } catch (e: Exception) {
                        Log.e("CitasListaFragment", "Exception parsing response body: ${e.message}")
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("CitasListaFragment", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Funciones para calificar cita -------------------------------------------------------------------------------------------------------------------------
    private fun mostrarDialogoCalificacion(idCita: Int) {

        val dialogView = layoutInflater.inflate(R.layout.calificar_medico, null)
        val comentarioEditText = dialogView.findViewById<EditText>(R.id.comentariosEditText)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Califica al Médico")
            .setView(dialogView)
            .setPositiveButton("Enviar") { _, _ ->
                val calificacion = obtenerCalificacion(dialogView)
                val comentarioText = comentarioEditText.text.toString()

                val parametros = RegistroCalificacionMedico(
                    idCita = idCita,
                    puntuacion = calificacion,
                    comentarios = comentarioText
                );

                enviarCalificacionCitaMedico(parametros);
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        val estrellas = arrayOf(
            dialogView.findViewById<ImageView>(R.id.star1),
            dialogView.findViewById<ImageView>(R.id.star2),
            dialogView.findViewById<ImageView>(R.id.star3),
            dialogView.findViewById<ImageView>(R.id.star4),
            dialogView.findViewById<ImageView>(R.id.star5)
        )

        for (i in estrellas.indices) {
            estrellas[i].setOnClickListener {
                toggleStar(estrellas, i)
            }
        }
    }

    private fun toggleStar(estrellas: Array<ImageView>, index: Int) {
        val isActivated = estrellas[index].tag == "active"
        for (i in estrellas.indices) {
            estrellas[i].setImageResource(if (i <= index || (i < index && estrellas[i].tag == "active")) R.drawable.estrella_activa else R.drawable.estrella_inactiva)
            estrellas[i].tag = if (i <= index) "active" else "inactive"
        }
    }

    private fun obtenerCalificacion(view: View): Int {
        val estrellas = arrayOf(
            view.findViewById<ImageView>(R.id.star1),
            view.findViewById<ImageView>(R.id.star2),
            view.findViewById<ImageView>(R.id.star3),
            view.findViewById<ImageView>(R.id.star4),
            view.findViewById<ImageView>(R.id.star5)
        )

        var calificacion = 0
        for (i in estrellas.indices) {
            if (estrellas[i].tag == "active") {
                calificacion = i + 1
            }
        }
        return calificacion
    }

    private fun enviarCalificacionCitaMedico(parametros : RegistroCalificacionMedico) {
        RetrofitClient.instance.registrarCalificacionMedico(parametros).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val stringJson = response.body()?.string()
                        actualizarCitas();
                        showToast("Calificación registrada con éxito.");
                    } catch (e: Exception) {
                        Log.e("CitasListaFragment", "Exception parsing response body: ${e.message}")
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("CitasListaFragment", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun existeCalificacionParaCita(idCita: Int) {
        RetrofitClient.instance.existeCalificacionParaCita(idCita).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val stringJson = response.body()?.string()
                        val calificacionExistente = stringJson?.toIntOrNull() ?: 0

                        if (calificacionExistente == 1) {
                            Toast.makeText(requireContext(), "Ya existe una calificación para esta cita.", Toast.LENGTH_SHORT).show()
                        } else {
                            mostrarDialogoCalificacion(idCita);
                        }
                    } catch (e: Exception) {
                        Log.e("CitasListaFragment", "Exception parsing response body: ${e.message}")
                        Toast.makeText(requireContext(), "Error al procesar la respuesta.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("CitasListaFragment", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun obtenerCalificacionPorCita(idCita: Int, callback: (Int) -> Unit) {
        RetrofitClient.instance.obtenerCalificacionPorCita(idCita).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val stringJson = response.body()?.string()
                        val calificacionExistente = stringJson?.toIntOrNull() ?: 0
                        callback(calificacionExistente.toInt())
                    } catch (e: Exception) {
                        Log.e("CitasListaFragment", "Exception parsing response body: ${e.message}")
                        Toast.makeText(requireContext(), "Error al procesar la respuesta.", Toast.LENGTH_SHORT).show()
                        callback(0)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    callback(0)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("CitasListaFragment", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
                callback(0)
            }
        })
    }

    private fun setStarRating(stars: Array<ImageView>, rating: Int) {
        for (i in stars.indices) {
            stars[i].setImageResource(if (i < rating) R.drawable.estrella_activa else R.drawable.estrella_inactiva)
        }
    }

    private fun clearStarRating(stars: Array<ImageView>) {
        for (star in stars) {
            star.setImageResource(R.drawable.estrella_inactiva)
        }
    }

    // Funciones para buscar por estatus cita -------------------------------------------------------------------------------------------------------------------------

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.estatus_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstatus.adapter = adapter

        spinnerEstatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val estatus = when (position) {
                    0 -> -1 // Todas
                    1 -> 0 // Pendientes por Medico
                    2 -> 1 // Pendientes de Confirmar
                    3 -> 2 // Confirmadas
                    4 -> 3 // Cancelada por Médico
                    5 -> 4 // Cancelada por Paciente
                    6 -> 5 // Citas Terminadas

                    else -> -1
                }
                usuarioId?.let { id ->
                    obtenerCitasPacientePorEstatus(id.toInt(), estatus);
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Opcional: Manejar el caso cuando no se selecciona ningún ítem
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    companion object {
        @JvmStatic
        fun newInstance(usuarioId: String) =
            CitasListaFragment().apply {
                arguments = Bundle().apply {
                    putString(USUARIO_ID, usuarioId)
                }
            }
    }
}