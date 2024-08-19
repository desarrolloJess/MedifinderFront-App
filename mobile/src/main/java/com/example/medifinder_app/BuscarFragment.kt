package com.example.medifinder_app

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.medifinder_app.apiservice.RetrofitClient
import com.example.medifinder_app.modelos.Doctor
import com.example.medifinder_app.models.DoctoresModel
import com.example.medifinder_app.models.EspecialidadModel
import com.example.medifinder_app.models.PromedioCalificacionMedicoModel
import com.example.medifinder_app.models.RegistroCita
import com.example.medifinder_app.models.UsuarioModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val USUARIO_ID = "usuarioId"


class BuscarFragment : Fragment() {
    private var usuarioId: String? = null

    private lateinit var doctorContainer: LinearLayout
    private lateinit var availableHours: List<String>
    private lateinit var spinnerEspecialidades: Spinner

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
        val view = inflater.inflate(R.layout.fragment_buscar, container, false)
        doctorContainer = view.findViewById(R.id.doctorContainer)
        spinnerEspecialidades = view.findViewById(R.id.spinnerEspecialidades)
        cargarEspecialidades()

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                obtenerListaDoctoresEspecialidad(newText.orEmpty())
                return true
            }
        })


        return view
    }

    private fun obtenerListaDoctoresEspecialidad(nombreEspecialidad:String){
        if (nombreEspecialidad.isEmpty()){
            doctorContainer.removeAllViews()
        }else{
            RetrofitClient.instance.obtenerMedicosPorEspecialidad(nombreEspecialidad).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        try {
                            val stringJson = response.body()?.string()
                            val gson = Gson()
                            val typeToken = object : TypeToken<List<DoctoresModel>>() {}.type
                            val lstDoctores: List<DoctoresModel> = gson.fromJson(stringJson, typeToken)

                            mostrarDoctores(lstDoctores)

                        } catch (e: Exception) {
                            Log.e("BuscarFragment", "Exception parsing response body: ${e.message}")
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("BuscarFragment", "Failure: ${t.message}")
                    Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun mostrarDoctores(doctorsList: List<DoctoresModel>) {
        doctorContainer.removeAllViews()

        for (doctor in doctorsList) {
            val doctorView = layoutInflater.inflate(R.layout.item_doctor, null)
            doctorView.findViewById<TextView>(R.id.textViewNombre).text = doctor.nombreCompleto
            doctorView.findViewById<TextView>(R.id.textViewEspecialidad).text = doctor.especialidades.get(0)
            doctorView.findViewById<TextView>(R.id.textViewDireccion).text = doctor.direccion

            val starContainer = doctorView.findViewById<LinearLayout>(R.id.starContainer)
            val stars = arrayOf(
                starContainer.findViewById<ImageView>(R.id.star1),
                starContainer.findViewById<ImageView>(R.id.star2),
                starContainer.findViewById<ImageView>(R.id.star3),
                starContainer.findViewById<ImageView>(R.id.star4),
                starContainer.findViewById<ImageView>(R.id.star5)
            )
            obtenerCalificacionPorMedico(doctor.idDoctor) { calificacion ->
                if (calificacion > 0) {
                    setStarRating(stars, calificacion)
                } else {
                    clearStarRating(stars)
                }
            }


            doctorView.setOnClickListener {
                showToast("Doctor seleccionado: ${doctor.nombreCompleto}")
                verCalendarioCita(doctor);
            }

            doctorContainer.addView(doctorView)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun verCalendarioCita(objDoctor: DoctoresModel) {
        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val day = today.get(Calendar.DAY_OF_MONTH)

        val nextMonth = today.clone() as Calendar
        nextMonth.add(Calendar.MONTH, 2) // Agregar 2 meses para asegurar que sea el mes siguiente
        nextMonth.set(Calendar.DAY_OF_MONTH, 1) // Establecer el día 1 para moverse al siguiente mes
        nextMonth.add(Calendar.DATE, -1) // Restar 1 día para obtener el último día del mes

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val fechaFormateada = sdf.format(selectedDate.time)

                val dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK)
                val numDiaSemana = (dayOfWeek - 1)

                llenarHorasDisponibles(objDoctor,numDiaSemana,fechaFormateada)
            },
            year,
            month,
            day
        )

        // Configuración adicional para restringir las fechas desde hoy
        datePickerDialog.datePicker.minDate = today.timeInMillis
        datePickerDialog.datePicker.maxDate = nextMonth.timeInMillis

        datePickerDialog.show()
    }

    private fun llenarHorasDisponibles(objDoctor:DoctoresModel,dia:Int,fecha:String) {
        val lstHorarios: List<String>;
        RetrofitClient.instance.obtenerHorasDeTrabajo(objDoctor.idDoctor,dia,fecha).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val stringJson = response.body()?.string()
                        val gson = Gson()
                        val typeToken = object : TypeToken<List<String>>() {}.type
                        val lstHorarios: List<String> = gson.fromJson(stringJson, typeToken)
                        if (lstHorarios.isNotEmpty()) {
                            mostrarHoras(lstHorarios,objDoctor,fecha)
                        } else {
                            showToast("No hay horas disponibles.")
                        }
                    } catch (e: Exception) {
                        Log.e("BuscarFragment", "Exception parsing response body: ${e.message}")
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("BuscarFragment", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun mostrarHoras(lstHorasDoctor: List<String>,objDoctor: DoctoresModel,fecha:String ) {
        availableHours = lstHorasDoctor;
        val hourOptions = availableHours.map { it.toString() }.toTypedArray()

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Selecciona una hora")

        builder.setItems(hourOptions) { dialog, which ->
            val selectedHour = hourOptions[which]
            showToast("Hora seleccionada: $selectedHour")
            showConfirmationDialog(objDoctor, fecha, selectedHour)
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showConfirmationDialog(objDoctor: DoctoresModel, formattedDate: String, selectedHour: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmación")
        builder.setMessage("Doctor seleccionado: ${objDoctor.nombreCompleto}\nFecha seleccionada: $formattedDate\nHora seleccionada: $selectedHour")
        builder.setPositiveButton("Confirmar") { dialog, _ ->
            registrarCita(objDoctor, formattedDate, selectedHour);
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            showToast("Cancelaste la selección de fecha para $objDoctor.nombreCompleto")
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun registrarCita(objDoctor: DoctoresModel, fecha: String, selectedHour: String) {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        // Combina la fecha y la hora en una cadena y cambiarla a Date
        val dateTime = inputFormat.parse("$fecha $selectedHour")
        val fechaInicio = outputFormat.format(dateTime)

        // Asumiendo que la duración de la cita es de una hora
        val calendar = Calendar.getInstance().apply {
            time = dateTime
            add(Calendar.HOUR_OF_DAY, 1)
        }
        val fechaFin = outputFormat.format(calendar.time)

        val idPaciente = usuarioId ?: run {
            showToast("Error: Usuario no disponible")
            return
        }
            // Crea el objeto RegistroCita con las fechas formateadas
            val objRegistrarCita = RegistroCita(
                idPaciente = idPaciente.toInt(),
                idMedico = objDoctor.idDoctor.toInt(),
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                descripcion = null,
                fechaCancelacion = null,
                motivoCancelacion = null
            )

        RetrofitClient.instance.registrarCita(objRegistrarCita).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val stringJson = response.body()?.string()
                        showToast("Cita registrada, pendiente de confirmación por el doctor.");
                    } catch (e: Exception) {
                        Log.e("BuscarFragment", "Exception parsing response body: ${e.message}")
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("BuscarFragment", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarEspecialidades() {
        RetrofitClient.instance.consultarEspecialidades().enqueue(object : Callback<List<EspecialidadModel>> {
            override fun onResponse(call: Call<List<EspecialidadModel>>, response: Response<List<EspecialidadModel>>) {
                if (response.isSuccessful) {
                    val especialidades = response.body() ?: emptyList()

                    val nombresEspecialidades = mutableListOf("Selecciona una especialidad")
                    val idsEspecialidades = mutableMapOf<String, Int>()

                    especialidades.forEach {
                        nombresEspecialidades.add(it.nombre)
                        idsEspecialidades[it.nombre] = it.id
                    }

                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombresEspecialidades)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerEspecialidades.adapter = adapter

                    spinnerEspecialidades.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val nombreSeleccionado = parent?.getItemAtPosition(position) as String
                            obtenerListaDoctoresEspecialidad(nombreSeleccionado);
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            obtenerListaDoctoresEspecialidad("");
                        }
                    }

                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<EspecialidadModel>>, t: Throwable) {
                Log.e("BuscarFragment", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun obtenerCalificacionPorMedico(idMedico: Int, callback: (Int) -> Unit) {
        RetrofitClient.instance.promedioCalificacionMedico(idMedico).enqueue(object : Callback<List<PromedioCalificacionMedicoModel>> {
            override fun onResponse(call: Call<List<PromedioCalificacionMedicoModel>>, response: Response<List<PromedioCalificacionMedicoModel>>) {
                if (response.isSuccessful) {
                    try {
                        val promedioList = response.body() ?: emptyList()

                        val promedioPuntuacion = if (promedioList.isNotEmpty()) {
                            promedioList[0].promedioPuntuacion.toInt()
                        } else {
                            0
                        }

                        callback(promedioPuntuacion)
                    } catch (e: Exception) {
                        Log.e("BuscarFragment", "Exception parsing response body: ${e.message}")
                        Toast.makeText(requireContext(), "Error al procesar la respuesta.", Toast.LENGTH_SHORT).show()
                        callback(0)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    callback(0)
                }
            }

            override fun onFailure(call: Call<List<PromedioCalificacionMedicoModel>>, t: Throwable) {
                Log.e("BuscarFragment", "Failure: ${t.message}")
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

    companion object {
        @JvmStatic
        fun newInstance(usuarioId: String) =
            BuscarFragment().apply {
                arguments = Bundle().apply {
                    putString(USUARIO_ID, usuarioId)
                }
            }
    }


}

