package ru.mipt.physics.biref

import javafx.stage.Stage
import tornadofx.*

/**
 * Created by darksnake on 28-Dec-16.
 */

class BirefFXApp : App(BirefView::class) {

    override fun start(stage: Stage) {
        super.start(stage)
        if (parameters.raw.contains("--test")) {
            find(BirefView::class).apply {
                log.info("Loading debug dataset")
                resources.stream("/data.txt").use {
                    load(it);
                }
            }
        }
    }
}

/**
 * Format number using 2 digits precision
 * @param num
 * @return
 */
actual fun format(num: Number, digits: Int): String {
    return "%.${digits}f".format(num)
}