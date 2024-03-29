package com.anb.offlinetranslator.speechEngine
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

import java.util.*


class SpeechToTextConverter(private val conversionCallback: ConversionCallback)  : TranslatorFactory.IConverter {

    private val TAG = "SpeechToTextConverter"

    override fun initialize(message: String, appContext: Activity): SpeechToTextConverter {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
            message)
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
            appContext.packageName)

        //Add listeners
        val listener = CustomRecognitionListener()
        val sr = SpeechRecognizer.createSpeechRecognizer(appContext)
        sr.setRecognitionListener(listener)
        sr.startListening(intent)
        return this
    }

    internal inner class CustomRecognitionListener : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle) {
            Log.d(TAG, "onReadyForSpeech")
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech")
        }

        override fun onRmsChanged(rmsdB: Float) {
            Log.d(TAG, "onRmsChanged")
        }

        override fun onBufferReceived(buffer: ByteArray) {
            Log.d(TAG, "onBufferReceived")
        }

        override fun onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech")
        }

        override fun onError(error: Int) {
            Log.e(TAG, "error $error")
            conversionCallback.onErrorOccurred(getErrorText(error))
        }

        override fun onResults(results: Bundle) {
            var translateResults = String()
            val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (data != null) {
                for ( result in  data) {
                    translateResults += result + "\n"
                }
            }
            conversionCallback.onSuccess(translateResults)
        }

        override fun onPartialResults(partialResults: Bundle) {
            Log.d(TAG, "onPartialResults")
        }

        override  fun onEvent(eventType: Int, params: Bundle) {
            Log.d(TAG, "onEvent $eventType")
        }
    }

    override fun  getErrorText(errorCode: Int): String {
        val message: String = when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again."
        }
        Log.d(TAG, "getErrorText: $message")
        return message
    }



}
