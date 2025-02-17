package com.oneglobal.esim.sdk.example

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.oneglobal.esim.sdk.EsimEventType
import com.oneglobal.esim.sdk.EsimManager
import com.oneglobal.esim.sdk.TitleAPN
import com.oneglobal.esim.sdk.example.ui.theme.EsimSdkExampleTheme


class MainActivity : ComponentActivity() {
    private val logsState = mutableStateOf(listOf<String>())
    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        fun addLog(message: String) {
            logsState.value = logsState.value + message
            Log.d("EsimManager", message)
        }

        val esimManager = EsimManager(this) { eventType ->
            addLog("$eventType")
            if (eventType == EsimEventType.SETUP_ESIM_SHOW_PROMPT) {
                isLoading = true
            }
            if (eventType == EsimEventType.SETUP_ESIM_SUCCESS || eventType == EsimEventType.SETUP_ESIM_FAILED || eventType == EsimEventType.SETUP_ESIM_CANCELLED) {
                isLoading = false
            }
        }
        val isEsimSupported = esimManager.isEsimSupported().toString()

        setContent {
            EsimSdkExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        esimManager,
                        isEsimSupported,
                        logs = logsState.value,
                        isLoading = isLoading,
                        setLoading = { isLoading = it },
                        clearLogs = { logsState.value = emptyList() },
                        Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    esimManager: EsimManager,
    esimStatus: String,
    logs: List<String>,
    isLoading: Boolean,
    setLoading: (Boolean) -> Unit,
    clearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "ESIM support: $esimStatus", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val privileges = esimManager.haveCarrierPrivileges()
            Toast.makeText(
                context,
                "Have carrier privileges: $privileges",
                Toast.LENGTH_SHORT
            ).show()
        }) {
            Text("Have carrier privileges")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            val esimSupport = esimManager.isEsimSupported()
            Toast.makeText(context, "Esim support: $esimSupport", Toast.LENGTH_SHORT).show()
        }) {
            Text("Check eSIM Support")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            setLoading(true)
            val payload = "LPA:1\$rsp.truphone.com\$QRF-BETTERROAMING-PMRDGIR2EARDEIT5"
            val future = esimManager.setupEsim(payload)
            future.thenAccept {
                Toast.makeText(context, "Esim setup: $it", Toast.LENGTH_SHORT).show()
            }.exceptionally {
                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                null
            }.thenRun {
                setLoading(false)
            }
        }) {
            Text("Install eSIM")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            try {
                val success = esimManager.setAPN(TitleAPN.ONE_GLOBAL)
                Toast.makeText(context, "Esim APN fix: $success", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }) {
            Text("Fix APN")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            try {
                val iccids = esimManager.iccids
                Toast.makeText(context, "Esim list: $iccids", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }) {
            Text("Get ICCIDs")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show loading spinner when `isLoading` is true
        if (isLoading) {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable Logs Section
        Text("Logs:", style = MaterialTheme.typography.titleMedium)
        Button(onClick = { clearLogs() }) {
            Text("Clear Logs")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(logs.reversed()) { log ->
                    Text(log, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}