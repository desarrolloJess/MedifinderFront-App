package com.example.medifinder_app.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.medifinder_app.presentation.apiservice.RetrofitClient
import com.example.medifinder_app.presentation.config.AppDatabase
import com.example.medifinder_app.presentation.config.SessionManager
import com.example.medifinder_app.presentation.config.UtilNodeManager
import com.example.medifinder_app.presentation.entity.Credencial
import com.example.medifinder_app.presentation.models.CitaModel
import com.example.medifinder_app.presentation.models.LoginModel
import com.example.medifinder_app.presentation.models.PacienteModel
import com.example.medifinder_app.presentation.models.UsuarioModel
import com.example.medifinder_app.presentation.theme.Medifinder_AppTheme
import com.example.medifinder_app.presentation.ui.CartaCitaComposable
import com.example.medifinder_app.presentation.ui.EncabezadoComposable
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener{
    lateinit var sessionManager: SessionManager
    private val citasState = mutableStateListOf<CitaModel>()
    private val isLoading = mutableStateOf(false)
    private var activityContext: Context? = null
    var nodoIdTelefono = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        setContent {
            Medifinder_AppTheme {
                val navController = rememberNavController()

                // Verifica credenciales y realiza el login antes de cargar la pantalla principal
                val userState = remember { mutableStateOf<UsuarioModel?>(null) }
                val isLoadingState = remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {


                    val user = sessionManager.getUser()
                    if (user == null) {
                        obtenerCredencialesYLogin(this@MainActivity) { userLoggedIn ->
                            userState.value = userLoggedIn
                            isLoadingState.value = false
                        }
                    } else {
                        userState.value = user
                        obtenerCitasPacientes(user.idPaciente)
                        isLoadingState.value = false
                    }
                }

                if (isLoadingState.value) {
                    // Mostrar un indicador de carga o algo similar mientras se realiza la autenticación
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Cargar el contenido principal
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(navController, citasState, isLoading)
                        }
                        composable(
                            route = "appointmentDetails/{citaId}",
                            arguments = listOf(navArgument("citaId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val citaId = backStackEntry.arguments?.getInt("citaId") ?: return@composable
                            val cita = citasState.find { it.id == citaId }
                            cita?.let { AppointmentDetailsActivity(it, navController) }
                        }
                        composable(
                            route = "appointmentConfirm/{citaId}",
                            arguments = listOf(navArgument("citaId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val citaId = backStackEntry.arguments?.getInt("citaId") ?: return@composable
                            val cita = citasState.find { it.id == citaId } // Suponiendo que cada cita tiene un ID único
                            cita?.let { AppointmentConfirmActivity(it, navController) }
                        }
                        composable(
                            route = "appointmentCancel/{citaId}",
                            arguments = listOf(navArgument("citaId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val citaId = backStackEntry.arguments?.getInt("citaId") ?: return@composable
                            val cita = citasState.find { it.id == citaId } // Suponiendo que cada cita tiene un ID único
                            cita?.let { AppointmentCancelActivity(it, navController) }
                        }
                        composable("appointmentConfirmed") {
                            AppointmentConfirmedActivity(navController)
                        }
                        composable("appointmentCanceled") {
                            AppointmentCanceledActivity(navController)
                        }
                        composable("appointmentConfirmedError") {
                            AppointmentConfirmedErrorActivity(navController)
                        }
                        composable("appointmentCanceledError") {
                            AppointmentCanceledErrorActivity(navController)
                        }

                    }
                }
            }
        }
    }

    @Composable
    fun HomeScreen(navController: NavHostController, citas: List<CitaModel>, isLoading: State<Boolean>) {
        val scrollState = rememberScrollState()

        LaunchedEffect(Unit) {
            val user = sessionManager.getUser()
            if (user != null) {
                obtenerCitasPacientes(user.idPaciente)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .clickable {
                    val user = sessionManager.getUser()
                    if (user == null) {
                        Toast
                            .makeText(this, "Necesitas iniciar sesión", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        obtenerCitasPacientes(user.idPaciente)
                        Log.i("DANIEL", "ID PACIENTE: ${user.idPaciente}")
                    }
                }
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                EncabezadoComposable()
                if (sessionManager.getUser() != null) {
                    Text(
                        text = "Tus Citas",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(vertical = 3.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    if (isLoading.value) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(align = Alignment.Center)
                        ) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "Obteniendo citas...",
                                    fontSize = 8.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    } else {
                        if (citas.isNotEmpty()) {
                            citas.forEach { cita ->
                                CartaCitaComposable(cita, navController)
                            }
                        } else {
                            Text(
                                text = "No tienes citas pendientes",
                                fontSize = 14.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Text(
                                text = "Toca para refrescar",
                                fontSize = 8.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Tu dispositivo no está conectado, es necesario que te conectes desde tu dispositivo móvil.",
                        fontSize = 12.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(vertical = 3.dp, horizontal = 5.dp)
                    )
                }
            }
        }
    }

    fun verificarNodosConectados(){
        var nodeClient = Wearable.getNodeClient(this)
        val phoneNodeManager = UtilNodeManager(nodeClient);
        phoneNodeManager.getDispositivoNodeId { dispositivoNodeId ->
            if (dispositivoNodeId != null) {
                nodoIdTelefono = dispositivoNodeId
                Log.i("DANIEL", "DISPOSITIVO ${dispositivoNodeId}")
            } else { Log.e("WearActivity", "No se encontró un nodo cercano") }
        }
    }

    //funcion para obtener las citas
    fun obtenerCitasPacientes(idPaciente: Int) {
        isLoading.value = true

        RetrofitClient.instance.obtenerCitasPacientes(idPaciente).enqueue(object : Callback<List<PacienteModel>> {
            override fun onResponse(call: Call<List<PacienteModel>>, response: Response<List<PacienteModel>>) {
                if (response.isSuccessful) {
                    val pacientes = response.body()
                    pacientes?.let { listaPacientes ->
                        citasState.clear()  // Limpiar la lista actual
                        listaPacientes.forEach { paciente ->
                            val citasFiltradas = paciente.citas.filter { it.estatus == "0" || it.estatus == "1" || it.estatus == "2" }
                            citasState.addAll(citasFiltradas)
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    val errorCode = response.code()
                    Toast.makeText(this@MainActivity, "Error ${errorCode}: $errorBody", Toast.LENGTH_SHORT).show()
                    Log.e("DANIEL", "Error code: $errorCode, Error body: $errorBody")
                }
                isLoading.value = false
            }

            override fun onFailure(call: Call<List<PacienteModel>>, t: Throwable) {
                Log.e("DANIEL", "Failure: ${t.message}")
                //Toast.makeText(this@MainActivity, "Error al obtener citas", Toast.LENGTH_SHORT).show()
                isLoading.value = false
            }
        })
    }

    private fun obtenerCredencialesYLogin(context: Context, onLoginComplete: (UsuarioModel?) -> Unit) {
        lifecycleScope.launch {
            lateinit var db: AppDatabase
            db = AppDatabase.getDatabase(context)
            val credenciales = db.credencialDao().getCredenciales()

            if (credenciales != null) {
                login(context, credenciales.email, credenciales.password) { user ->
                    onLoginComplete(user)
                }
            } else {
                Toast.makeText(context, "Necesitas iniciar sesión", Toast.LENGTH_SHORT).show()
                onLoginComplete(null)
            }
            db.close()
        }
    }

    private fun login(context: Context, email: String, contrasena: String, onLoginSuccess: (UsuarioModel?) -> Unit) {
        val loginModel = LoginModel(email, contrasena)

        RetrofitClient.instance.postLogin(loginModel).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    val gson = Gson()
                    val user = gson.fromJson(responseBody, UsuarioModel::class.java)
                    sessionManager.saveUser(user)
                    Toast.makeText(context, "Bienvenido ${user.email}", Toast.LENGTH_SHORT).show()
                    obtenerCitasPacientes(sessionManager.getUser()?.idPaciente ?: 0)
                    onLoginSuccess(user)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    val errorCode = response.code()
                    Toast.makeText(context, "Error ${errorCode}: $errorBody", Toast.LENGTH_SHORT).show()
                    Log.e("DANIEL", "Error code: $errorCode, Error body: $errorBody")
                    onLoginSuccess(null)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("DANIEL", "Failure: ${t.message}")
                Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                onLoginSuccess(null)
            }
        })
    }


    fun obtenerCredenciales(){
        lifecycleScope.launch {
            lateinit var db: AppDatabase
            db = AppDatabase.getDatabase(this@MainActivity)
            var crdenciales = db.credencialDao().getCredenciales()
            Log.i("Daniel", "Email: ${crdenciales?.email}, pass: ${crdenciales?.password}")
            db.close()
        }
    }
    fun insertarCredenciales(){
        lifecycleScope.launch{
            lateinit var db: AppDatabase
            db = AppDatabase.getDatabase(this@MainActivity)
            val credencial = Credencial(email = "daniel@gmail.com", password = "Password123@")
            db.credencialDao().insertCredencial(credencial)
            db.close()
        }
    }

    fun borrarCredenciales(){
        lifecycleScope.launch {
            lateinit var db: AppDatabase
            db = AppDatabase.getDatabase(this@MainActivity)
            db.credencialDao().deleteAll()
            db.close()
        }
    }


    override fun onResume() {
        super.onResume()
        Wearable.getMessageClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getMessageClient(this).removeListener(this)
    }

    override fun onMessageReceived(p0: MessageEvent) {
        Toast.makeText(this, "Iniciando Sesión", Toast.LENGTH_SHORT).show()
        try {
            val s1 = String(p0.data, StandardCharsets.UTF_8)
            val messageEventPath: String = p0.path
            nodoIdTelefono = p0.sourceNodeId

            val gson = Gson()
            val usuario: UsuarioModel = gson.fromJson(s1, UsuarioModel::class.java)

            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(this@MainActivity)
                db.credencialDao().deleteAll()

                // Guardar las nuevas credenciales
                val credencial = Credencial(email = usuario.email, password = usuario.contrasena)
                db.credencialDao().insertCredencial(credencial)

                // Cierra la base de datos después de terminar las operaciones
                //db.close()
            }

            // Iniciar sesión automáticamente
            login(this, usuario.email, usuario.contrasena) { user -> }

            Log.i("DANIEL", "Mensaje: ${s1}")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}