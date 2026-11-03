package com.pedro.warobotsafe

import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import org.json.JSONObject
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    private lateinit var web: WebView

    // Abre o seletor nativo diretamente na lista de números telefônicos.
    // O Android concede acesso somente ao item escolhido, sem READ_CONTACTS.
    private val phonePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) importSelectedPhone(uri)
            else Toast.makeText(this, "Nenhum telefone foi selecionado.", Toast.LENGTH_LONG).show()
        }
    }

    private fun importSelectedPhone(uri: Uri) {
        try {
            var displayName = ""
            var phone = ""
            contentResolver.query(
                uri,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null, null, null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    displayName = cursor.getString(0) ?: ""
                    phone = cursor.getString(1) ?: ""
                }
            }

            if (phone.isBlank()) {
                Toast.makeText(this, "O item escolhido não contém um número de telefone.", Toast.LENGTH_LONG).show()
                return
            }

            // O formulário já exibe +55, então removemos o código do país do campo numérico.
            var digits = phone.filter { it.isDigit() }
            if (digits.startsWith("55") && (digits.length == 12 || digits.length == 13)) {
                digits = digits.drop(2)
            }
            val nameArg = JSONObject.quote(displayName)
            val phoneArg = JSONObject.quote(digits)
            web.evaluateJavascript(
                "window.onNativeContactPicked && window.onNativeContactPicked($nameArg, $phoneArg);",
                null
            )
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Falha ao ler o telefone escolhido: ${e.localizedMessage ?: "erro de acesso"}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = true
        web.addJavascriptInterface(Bridge(), "AndroidBridge")
        setContentView(web)
        web.loadUrl("file:///android_asset/index.html")
    }

    inner class Bridge {
        @JavascriptInterface
        fun pickContact() {
            runOnUiThread {
                try {
                    val intent = Intent(
                        Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    )
                    phonePicker.launch(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Não foi possível abrir a agenda do aparelho.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        @JavascriptInterface
        fun openWhatsApp(rawPhone: String, message: String) {
            runOnUiThread {
                val phone = rawPhone.filter { it.isDigit() }
                if (phone.length !in 10..15) {
                    Toast.makeText(this@MainActivity, "Telefone inválido: inclua país e DDD.", Toast.LENGTH_LONG).show()
                    return@runOnUiThread
                }
                val text = URLEncoder.encode(message, "UTF-8")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phone?text=$text"))
                    .setPackage("com.whatsapp.w4b")
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "WhatsApp Business não encontrado. Instale/ative o WhatsApp Business neste aparelho.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        web.destroy()
        super.onDestroy()
    }
}
