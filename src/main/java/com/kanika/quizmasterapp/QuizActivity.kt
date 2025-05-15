package com.kanika.quizmasterapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.kanika.quizmasterapp.data.QuestionBank
import com.kanika.quizmasterapp.model.Question

class QuizActivity : AppCompatActivity() {

    private var index = 0
    private var score = 0
    private lateinit var currentQuestion: Question
    private val questions = QuestionBank.questions

    private lateinit var timerTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var countDownTimer: CountDownTimer? = null
    private val timePerQuestion = 120_000L // 30 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        timerTextView = findViewById(R.id.timerText)
        progressBar = findViewById(R.id.progressBar)

        loadQuestion()

        findViewById<Button>(R.id.nextBtn).setOnClickListener {
            val group = findViewById<RadioGroup>(R.id.optionsGroup)
            val selectedId = group.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedId)
                val selectedIndex = group.indexOfChild(selectedRadioButton)

                if (selectedIndex == currentQuestion.correctAnswer) {
                    score++
                }

                index++
                if (index < questions.size) {
                    loadQuestion()
                } else {
                    val intent = Intent(this, ResultActivity::class.java).apply {
                        putExtra("SCORE", score)
                        putExtra("TOTAL", questions.size)
                    }
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadQuestion() {
        currentQuestion = questions[index]
        findViewById<TextView>(R.id.questionText).text = currentQuestion.questionText

        val group = findViewById<RadioGroup>(R.id.optionsGroup)
        group.removeAllViews()

        currentQuestion.options.forEachIndexed { i, option ->
            val rb = RadioButton(this).apply {
                text = option
                id = View.generateViewId()
            }
            group.addView(rb)
        }

        countDownTimer?.cancel()
        startTimer()
    }

    private fun startTimer() {
        progressBar.max = (timePerQuestion / 1000).toInt()
        progressBar.progress = progressBar.max

        countDownTimer = object : CountDownTimer(timePerQuestion, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                progressBar.progress = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                Toast.makeText(this@QuizActivity, "Time's up!", Toast.LENGTH_SHORT).show()
                showCorrectAnswer()

                Handler().postDelayed({
                    index++
                    if (index < questions.size) {
                        loadQuestion()
                    } else {
                        val intent = Intent(this@QuizActivity, ResultActivity::class.java).apply {
                            putExtra("SCORE", score)
                            putExtra("TOTAL", questions.size)
                        }
                        startActivity(intent)
                        finish()
                    }
                }, 2000)
            }
        }.start()
    }

    private fun showCorrectAnswer() {
        val group = findViewById<RadioGroup>(R.id.optionsGroup)
        val correctOption = currentQuestion.correctAnswer
        val correctRadioButton = group.getChildAt(correctOption) as RadioButton
        correctRadioButton.setTextColor(resources.getColor(android.R.color.holo_green_dark))
        correctRadioButton.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
