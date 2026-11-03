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

    // Seletor nativo do Android: só lê o contato escolhido pelo usuário, sem pedir acesso à agenda inteira.
    private val contactPicker = registerForActivityResult(ActivityResultContracts.PickContact()) { uri ->
        if (uri != null) importSelectedContact(uri)
    }

    private fun importSelectedContact(uri: Uri) {
        try {
            var displayName = ""
            contentResolver.query(
                uri,
                arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
                null, null, null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    displayName = cursor.getString(0) ?: ""
                }
            }

            var phone = ""
            // Consulta os dados filhos do URI concedido pelo seletor, evitando READ_CONTACTS.
            val dataUri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Data.CONTENT_DIRECTORY)
            contentResolver.query(
                dataUri,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.Data.MIMETYPE
                ),
                "${ContactsContract.Data.MIMETYPE} = ?",
                arrayOf(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) phone = cursor.getString(0) ?: ""
            }

            if (phone.isBlank()) {
                Toast.makeText(this, "Esse contato não tem telefone cadastrado.", Toast.LENGTH_LONG).show()
                return
            }

            // O formulário brasileiro usa DDD + número; remove +55 para não duplicar o prefixo visual.
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
            Toast.makeText(this, "Não foi possível importar esse contato.", Toast.LENGTH_LONG).show()
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
            runOnUiThread { contactPicker.launch(null) }
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
                // Direciona ao WhatsApp Business. Não autentica nem envia automaticamente.
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
