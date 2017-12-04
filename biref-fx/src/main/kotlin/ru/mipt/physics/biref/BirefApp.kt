package ru.mipt.physics.biref

import javafx.stage.Stage
import tornadofx.*

/**
 * Created by darksnake on 28-Dec-16.
 */

class BirefApp : App(BirefView::class) {

    override fun start(stage: Stage) {
        if (parameters.raw.contains("--test")) {
            System.setProperty("testRun", "true");
        }
        super.start(stage)
    }
}