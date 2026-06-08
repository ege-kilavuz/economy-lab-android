package com.egekilavuz.economylab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun UserNameDialog(
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {},
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("👤 Hoş Geldin!", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text("Liderlik tablosunda görünecek adını gir:")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        if (it.length <= 20) {
                            name = it
                            error = false
                        }
                    },
                    label = { Text("Adın") },
                    singleLine = true,
                    isError = error,
                    supportingText = if (error) {{ Text("Lütfen bir ad gir") }} else null,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (name.isNotBlank()) {
                            onSave(name.trim())
                        } else {
                            error = true
                        }
                    }),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name.trim())
                    } else {
                        error = true
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Kaydet", fontWeight = FontWeight.Bold)
            }
        }
    )
}
