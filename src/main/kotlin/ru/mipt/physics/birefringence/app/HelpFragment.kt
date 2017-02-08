package ru.mipt.physics.birefringence.app

import javafx.scene.web.WebView
import tornadofx.Fragment
import tornadofx.anchorpane

/**
 * Created by darksnake on 08-Feb-17.
 */
class HelpFragment : Fragment("Справка") {
    override val root = anchorpane {
        val browser = WebView();
        browser.engine.load(resources.url("/info.html").toString())
        children.add(browser)
    }
}
