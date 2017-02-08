package ru.mipt.physics.birefringence.app

import javafx.stage.Stage
import tornadofx.App

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