package com.example.medifinder_app

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.medifinder_app.models.LoginModel
import com.example.medifinder_app.models.UsuarioModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import okhttp3.ResponseBody
import com.example.medifinder_app.apiservice.RetrofitClient
import com.example.medifinder_app.config.SessionManager
import com.example.medifinder_app.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class  MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fab: FloatingActionButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navigationView: NavigationView

    private var isLoggedIn = true
    lateinit var sessionManager: SessionManager
    lateinit var contexto: Context

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        contexto = this
        sessionManager = SessionManager(this)

        setContentView(R.layout.acceso);
        navigationView = findViewById(R.id.nav_view)


        if (isLoggedIn){
            val email = intent.getStringExtra("email") ?: ""
            setContentView(R.layout.activity_main);
            asignarEventosInicioSesion(email)
        }

    }

    //OutSide onCreate
    private fun cargarFragmentoIniciarSesion() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun cargarFragmentoRegistrarse() {
        setContentView(R.layout.fragment_registrarse)
        supportFragmentManager.beginTransaction().replace(R.id.frameRegistrarse, RegistrarseFragment())
            .commit()
    }

    private fun asignarEventosInicioSesion(correo:String) {
        val buttonIniciarSesion = findViewById<Button>(R.id.buttonIniciarSesion)
        val email = findViewById<EditText>(R.id.editTextCorreo)
        val contrasena = findViewById<EditText>(R.id.editTextContrasena)

        email.setText(correo);

        val tengoCuenta = findViewById<TextView>(R.id.buttonRegistrarse)
        tengoCuenta.setOnClickListener {
            cargarFragmentoRegistrarse()
        }

        buttonIniciarSesion.setOnClickListener {
            if (formularioLoginValido(email, contrasena)) {
                val params = LoginModel(email.text.toString(), contrasena.text.toString())
                login(params)
            } else {
                Toast.makeText(this@MainActivity, "Existe información incorrecta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun formularioLoginValido(email: EditText, contraseña: EditText): Boolean {
        var valido = true
        if (contraseña.text.toString().isEmpty()) {
            valido = false
        }
        if (email.text.toString().isEmpty()) {
            valido = false
        }
        return valido
    }

    fun login(parametros: LoginModel) {
        Toast.makeText(this@MainActivity, "Iniciando sesión", Toast.LENGTH_SHORT).show()
        RetrofitClient.instance.loginPaciente(parametros).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val stringJson = response.body()?.string()
                        val gson = Gson()
                        val usuario = gson.fromJson(stringJson, UsuarioModel::class.java)
                        sessionManager.saveUser(usuario)
                        Toast.makeText(this@MainActivity, "Bienvenido ${usuario.nombreCompleto}", Toast.LENGTH_SHORT).show()

                        mostrarVistaAcceso(usuario);

                    } catch (e: Exception) {
                        Log.e("MainActivity", "Exception parsing response body: ${e.message}")
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
                Toast.makeText(this@MainActivity, "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarVistaAcceso(usuario:UsuarioModel) {
        setContentView(R.layout.acceso)

        fab = findViewById(R.id.fab)
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView = findViewById(R.id.nav_view)
        Log.d("MainActivity", "navigationView is null? ${navigationView == null}")

        navigationView.setOnClickListener{
            showToast("hola munso")
        }
        navigationView.setNavigationItemSelectedListener(this)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.background = null
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.citas -> {
                    replaceFragment(CitasListaFragment.newInstance( usuario.id.toString() ))
                }
                R.id.buscar -> {
                    replaceFragment(BuscarFragment.newInstance( usuario.id.toString() ))
                }
                R.id.perfil -> {
                    replaceFragment(PerfilFragment.newInstance( usuario.id.toString() ))
                }
            }
            true
        }


        //Permite mandar el nombre del usuario que accedio al sistema - Jessy
        val headerView = navigationView.getHeaderView(0)
        val nombreUsuarioConectado = headerView.findViewById<TextView>(R.id.nombreUsuarioConectado)
        nombreUsuarioConectado.text = usuario.nombreCompleto
        nombreUsuarioConectado.setOnClickListener{
            showToast("hola mundo")
        }

        //Permite mostrar la vista de home, sin esta línea muestra la pantalla en blanco - Jessy
        //supportFragmentManager.beginTransaction().replace(R.id.frame_layout, HomeFragment()).commit()
        replaceFragment(HomeFragment())

        fab.setOnClickListener {
            showBottomDialog(usuario)
        }
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showBottomDialog(usuario :UsuarioModel) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlayout)

        val sobreNosotrosOpcion = dialog.findViewById<LinearLayout>(R.id.layoutVideo)
        val avisoPrivacidadOpcion = dialog.findViewById<LinearLayout>(R.id.layoutShorts)
        val cerrarSesionOpcion = dialog.findViewById<LinearLayout>(R.id.layoutLive)
        val conectarWearOpcion = dialog.findViewById<LinearLayout>(R.id.layoutWear)

        val msjUsuario = dialog.findViewById<TextView>(R.id.txtUsuarioLogeado)
        msjUsuario.setText("Bienvenid@ ${usuario.nombreCompleto}")

        sobreNosotrosOpcion.setOnClickListener {
            dialog.dismiss()
            replaceFragment(SobreNosotrosFragment())
        }

        avisoPrivacidadOpcion.setOnClickListener {
            dialog.dismiss()
            replaceFragment(AvisoPrivacidadFragment())
        }

        cerrarSesionOpcion.setOnClickListener {
            dialog.dismiss()
            cargarFragmentoIniciarSesion()
        }

        conectarWearOpcion.setOnClickListener {
            dialog.dismiss()
            replaceFragment(WearConnectFragment())
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

}