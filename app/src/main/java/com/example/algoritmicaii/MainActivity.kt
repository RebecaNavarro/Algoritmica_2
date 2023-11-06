package com.example.algoritmicaii

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import com.example.algoritmicaii.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.min


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val englishDictionary = HashSet<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadEnglishDictionary() //cargamos el diccionario
        binding.corrector.setOnClickListener {

            val texto :String = binding.textoEdit.text.toString() //recibe el texto del usuario
            val words = texto.split(Regex("\\s+|(?=[.,])|(?<=[.,])"))
            // Dividimos el texto en palabras y se las almacena en words

            val correctedText = StringBuilder()
            if(texto.isEmpty()){ // Ve si esta vacio el texto

                Toast.makeText(this,"Introduzca texto", Toast.LENGTH_SHORT).show()
            }
            else{
                for (word in words) {
                    // Se inicializa dos variables, para ver la distancia minina
                    // y guarda la palabra mas cercana encontrada

                    var minDistance = Int.MAX_VALUE
                    var closestWord = word

                    if(word.matches(Regex(".*[a-zA-Z].*")) && checkSpelling(word)){
                        // Verifica dos factores, si contiene al menos una letra y si paso el "checkspelling" para busca la palabra mas cercana

                        for (dictWord in englishDictionary) {

                            // Se calcula la distancia desde la palabra actual a la del diccionario
                            val distance = editDistance(word.toLowerCase(), dictWord)

                            // Si la distancia es minima a la actual actualizamos el "mindistance" y tenemos la palabra mas cercana en el "closestword"
                            if (distance < minDistance) {
                                minDistance = distance
                                closestWord = dictWord
                            }
                        }
                        correctedText.append("<font color='#FF0000'>$closestWord</font> ") // Inicia la tarea de cambio de color

                    }else {
                        correctedText.append("$word ")
                    }

                }
                binding.textoOculto.text = Html.fromHtml(correctedText.toString(), Html.FROM_HTML_MODE_LEGACY)
                binding.textoOculto.visibility = View.VISIBLE //que después de corregir salga el texto
            }

        }


    }

    // Calcular la distancia entre dos cadenas s1 y s2
    private fun editDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length

        // Lo hacemos para almacenar los resultados intermedios de la distancia
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) {
            for (j in 0..n) {
                if (i == 0) {
                    dp[i][j] = j
                } else if (j == 0) {
                    dp[i][j] = i
                    // Vemos si son iguales
                } else if (s1[i - 1] == s2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1]
                } else {
                    // Si son diferentes calculamos el valor
                    dp[i][j] = 1 + min(dp[i][j - 1], min(dp[i - 1][j], dp[i - 1][j - 1]))
                }
            }
        }
        return dp[m][n]
    }

    private fun loadEnglishDictionary() { //carga el diccionario
        try {
            val inputStream = resources.openRawResource(R.raw.english_dictionary)//en este txt está el diccionario
            // Nos permite leer lo que hay dentro del archivo

            val reader = BufferedReader(InputStreamReader(inputStream))
            // Aparte de poder leer el archivo linea por linea el add(it) nos permite cargar palabras al mismo

            reader.useLines { lines -> lines.forEach { englishDictionary.add(it) } } //lee todas las lineas del diccionario
        } catch (e: Exception) {// Para posibles excepciones
            e.printStackTrace()
        }
    }

    private fun checkSpelling(word: String) : Boolean{ // Nos dice si la palabra es incorrecta

        // Si la palabra esta en el diccionario dara true lo cual esta bien escrita pero sino dara false
        return !englishDictionary.contains(word.toLowerCase())
    }
}