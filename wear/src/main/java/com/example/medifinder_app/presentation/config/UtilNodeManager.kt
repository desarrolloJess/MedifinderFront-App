package com.example.medifinder_app.presentation.config


import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
class UtilNodeManager(private val nodeClient: NodeClient) {
    fun getDispositivoNodeId(onNodeIdListener: (String?) -> Unit) {
        nodeClient.connectedNodes.addOnCompleteListener { task: Task<List<Node>> ->
            if (task.isSuccessful) {
                val nodes = task.result
                for (node in nodes) {
                    if (node.isNearby) {
                        onNodeIdListener(node.id) // Si se encuentra un nodo conectado lo retornará
                        return@addOnCompleteListener
                    }
                }
            }
            onNodeIdListener(null) // Si no se encuentra un nodo cercano retorna null
        }
    }
}