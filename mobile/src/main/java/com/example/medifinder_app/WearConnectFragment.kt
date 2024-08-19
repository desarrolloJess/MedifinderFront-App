package com.example.medifinder_app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.medifinder_app.config.SessionManager
import com.example.medifinder_app.config.UtilNodeManager

import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.wearable.MessageEvent
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class WearConnectFragment: Fragment(), MessageClient.OnMessageReceivedListener {
    private var param1: String? = null
    private var param2: String? = null
    private var nodoIdWearable = ""
    private lateinit var txtNodo: TextView
    private lateinit var txtEstatus: TextView
    private lateinit var btnEnviarCredenciales: Button
    private lateinit var btnConectarWear: Button
    private var activityContext: Context? = null
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
        Log.i("DANIEL", "Usuario: ${sessionManager.getUser()}")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_wear, container, false)

        // Inicializar vistas
        txtNodo = view.findViewById(R.id.txtNodo)
        txtEstatus = view.findViewById(R.id.txtEstatus)
        btnConectarWear = view.findViewById(R.id.btnConectarWear)
        btnEnviarCredenciales = view.findViewById(R.id.btnEnviarCredenciales)

        btnEnviarCredenciales.isEnabled = false

        // Configurar botones y listeners
        btnConectarWear.setOnClickListener {
            verificarNodosConectados()
        }

        btnEnviarCredenciales.setOnClickListener {
            enviarCredenciales()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WearConnectFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun verificarNodosConectados() {
        val nodeClient = Wearable.getNodeClient(requireContext())
        val phoneNodeManager = UtilNodeManager(nodeClient)
        phoneNodeManager.getDispositivoNodeId { dispositivoNodeId ->
            if (dispositivoNodeId != null) {
                nodoIdWearable = dispositivoNodeId
                txtNodo.text = "Nodo del wearable: $dispositivoNodeId"
                txtEstatus.text = "Estatus: Conectado"

                btnEnviarCredenciales.isEnabled = true
            } else {
                Log.e("DANIEL", "No se encontró un nodo cercano")
            }
        }
    }

    private fun enviarCredenciales() {
        val informacion = Gson().toJson(sessionManager.getUser())
        if (nodoIdWearable.isEmpty()) {
            Toast.makeText(requireContext(), "Wearable no conectado", Toast.LENGTH_SHORT).show()
            return
        }
        val payload: ByteArray = informacion.toByteArray()
        val sendMessageTask = Wearable.getMessageClient(requireContext())
            .sendMessage(nodoIdWearable, "/del-telefono-al-wearable", payload)
        sendMessageTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Mensaje enviado", Toast.LENGTH_SHORT).show()
                Log.i("DANIEL", "Mensaje enviado: ${informacion}")
            } else {
                Toast.makeText(requireContext(), "No se logró enviar el mensaje", Toast.LENGTH_SHORT).show()
                Log.e("DANIEL", "Error al enviar mensaje", task.exception)
            }
        }
    }

    override fun onMessageReceived(p0: MessageEvent) {

    }

    override fun onPause() {
        super.onPause()
        try {
            Wearable.getMessageClient(activityContext!!).removeListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onResume() {
        super.onResume()
        try {
            Wearable.getMessageClient(activityContext!!).addListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}