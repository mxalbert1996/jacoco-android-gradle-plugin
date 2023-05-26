package com.mxalbert.example.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mxalbert.example.android.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textView.text = NumberProvider().provideNumber().toString()
    }
}
