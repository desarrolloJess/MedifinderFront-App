package com.example.medifinder_app.apiservice


import com.example.medifinder_app.models.CancelarCitaPacienteModel
import com.example.medifinder_app.models.CitasPacienteModel
import com.example.medifinder_app.models.EspecialidadModel
import com.example.medifinder_app.models.LoginModel
import com.example.medifinder_app.models.PromedioCalificacionMedicoModel
import com.example.medifinder_app.models.RegistroCalificacionMedico
import com.example.medifinder_app.models.RegistroCita
import com.example.medifinder_app.models.RegistroModel
import com.example.medifinder_app.models.UsuarioModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface AuthApiService {
    @POST("api/Pacientes/LoginWear")
    fun loginPaciente(@Body params: LoginModel): Call<ResponseBody>

    @POST("api/Pacientes/Registrar")
    fun registrarPaciente(@Body params: RegistroModel): Call<ResponseBody>

    @PUT("api/Pacientes/ModificarPaciente/{id}")
    fun modificarDatosPaciente(
        @Path("id") id: String,
        @Body params: RegistroModel
    ): Call<ResponseBody>

    @GET("api/Pacientes/ObtenerPacientePorId/{id}")
    fun obtenerDatosPaciente(
        @Path("id") id: String
    ): Call<ResponseBody>


    //MEDICOS
    @GET("api/Medicos/ObtenerMedicosRegistrados")
    fun obtenerMedicosRegistrados(): Call<ResponseBody>

    @GET("api/Medicos/ObtenerMedicosPorEspecialidad/{nombreEspecialidad}")
    fun obtenerMedicosPorEspecialidad(
        @Path("nombreEspecialidad") nombreEspecialidad: String
    ): Call<ResponseBody>

    @GET("api/Medicos/ObtenerHorasDeTrabajo/{idMedico}/{dia}/{fecha}")
    fun obtenerHorasDeTrabajo(
        @Path("idMedico") idMedico: Int,
        @Path("dia") dia: Int,
        @Path("fecha") fecha: String
    ): Call<ResponseBody>

    //Citas
    @POST("api/Citas/RegistrarCita")
    fun registrarCita(@Body params: RegistroCita): Call<ResponseBody>
    @GET("api/Citas/ObtenerCitasPacientes/{idPaciente}")
    fun obtenerCitasPacientes(@Path("idPaciente") idPaciente: Int): Call<List<CitasPacienteModel>>
    @GET("api/Citas/ObtenerCitasPorPacientesYEstatus/{idPaciente}/{estatus}")
    fun obtenerCitasPorPacientesYEstatus(
        @Path("idPaciente") idPaciente: Int,
        @Path("estatus") estatus: Int
    ): Call<List<CitasPacienteModel>>
    @PUT("api/Citas/ConfirmarCitaPaciente/{id}")
    fun confirmarCitaPaciente(@Path("id") id: Int): Call<ResponseBody>
    @PUT("api/Citas/CancelarCitaPaciente/{id}")
    fun cancelarCitaPaciente(
        @Path("id") id: Int,
        @Body cancelarCitaDTO: CancelarCitaPacienteModel
    ): Call<ResponseBody>


    //Calificaciones
    @POST("api/CalificacionMedicos/RegistrarCalificacionMedico")
    fun registrarCalificacionMedico(@Body params: RegistroCalificacionMedico): Call<ResponseBody>
    @GET("api/CalificacionMedicos/ExisteCalificacionParaCita/{idCita}")
    fun existeCalificacionParaCita(@Path("idCita") idCita: Int): Call<ResponseBody>
    @GET("api/CalificacionMedicos/ObtenerCalificacionPorCita/{idCita}")
    fun obtenerCalificacionPorCita(@Path("idCita") idCita: Int): Call<ResponseBody>
    @GET("api/CalificacionMedicos/PromedioCalificacionMedico/{idMedico}")
    fun promedioCalificacionMedico(@Path("idMedico") idMedico: Int): Call<List<PromedioCalificacionMedicoModel>>


    //Especialidades
    @GET("api/Especialidades/ConsultarEspecialidades")
    fun consultarEspecialidades(): Call<List<EspecialidadModel>>
}