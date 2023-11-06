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
            val words = texto.split(Regex("\\s+|(?=[.,])|(?<=[.,])")) //analiza por partes
            val correctedText = StringBuilder()
            if(texto.isEmpty()){
                Toast.makeText(this,"Introduzca texto", Toast.LENGTH_SHORT).show()
            }
            else{
                for (word in words) {

                    var minDistance = Int.MAX_VALUE
                    var closestWord = word

                    if(word.matches(Regex(".*[a-zA-Z].*")) && checkSpelling(word)){
                        for (dictWord in englishDictionary) {
                            val distance = editDistance(word.toLowerCase(), dictWord)
                            if (distance < minDistance) {
                                minDistance = distance
                                closestWord = dictWord
                            }
                        }
                        correctedText.append("<font color='#FF0000'>$closestWord</font> ")
                    }else {
                        correctedText.append("$word ")
                    }

                }
                binding.textoOculto.text = Html.fromHtml(correctedText.toString(), Html.FROM_HTML_MODE_LEGACY)
                binding.textoOculto.visibility = View.VISIBLE //que después de corregir salga el texto
            }

        }


    }

    private fun editDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) {
            for (j in 0..n) {
                if (i == 0) {
                    dp[i][j] = j
                } else if (j == 0) {
                    dp[i][j] = i
                } else if (s1[i - 1] == s2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1]
                } else {
                    dp[i][j] = 1 + min(dp[i][j - 1], min(dp[i - 1][j], dp[i - 1][j - 1]))
                }
            }
        }
        return dp[m][n]
    }

    private fun loadEnglishDictionary() { //carga el diccionario
        try {
            val inputStream = resources.openRawResource(R.raw.english_dictionary)//en este txt está el diccionario
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.useLines { lines -> lines.forEach { englishDictionary.add(it) } } //lee todas las lineas del diccionario
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkSpelling(word: String) : Boolean{
        return !englishDictionary.contains(word.toLowerCase())
    }
}