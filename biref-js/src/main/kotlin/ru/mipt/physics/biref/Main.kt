package ru.mipt.physics.biref

import kotlin.browser.document
import kotlin.dom.hasClass


fun main(args: Array<String>) {
    var application: Application? = null

    val state: dynamic = module.hot?.let { hot ->
        hot.accept()

        hot.dispose { data ->
            data.appState = application?.dispose()
            application = null
        }

        hot.data
    }

    if (document.body != null) {
        application = start(state)
    } else {
        application = null
        document.addEventListener("DOMContentLoaded", { application = start(state) })
    }
}

fun start(state: dynamic): Application? {
    return if (document.body?.hasClass("app") == true) {
        val application = BirefJSApp()

        @Suppress("UnsafeCastFromDynamic")
        application.start(state?.appState ?: emptyMap())

        application
    } else {
        println("Application body not found")
        null
    }
}

/**
 * Format number using 2 digits precision
 * @param num
 * @return
 */
actual fun format(num: Number, digits: Int): String {
    return num.asDynamic().toFixed(digits) as String
}

