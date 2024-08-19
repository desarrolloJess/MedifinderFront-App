package com.example.medifinder_app.presentation.apiservice

import com.example.medifinder_app.presentation.models.LoginModel
import com.example.medifinder_app.presentation.models.PacienteModel
import com.example.medifinder_app.presentation.requests.CancelacionRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApiService {
    @GET("Citas/ObtenerCitasPacientesWear/{idpaciente}")
    fun obtenerCitasPacientes(@Path("idpaciente") idPaciente: Int): Call<List<PacienteModel>>

    @POST("Pacientes/LoginWear")
    fun postLogin(@Body params: LoginModel): Call<ResponseBody>

    @PUT("Citas/ConfirmarCitaPaciente/{id}")
    fun confirmarCita(@Path("id") id: Int): Call<ResponseBody>

    @PUT("Citas/CancelarCitaPaciente/{id}")
    fun cancelarCita(
        @Path("id") id: Int,
        @Body motivoCancelacion: CancelacionRequest
    ): Call<ResponseBody>
}