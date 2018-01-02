package ru.mipt.physics.biref

import kotlinx.html.dom.append
import kotlinx.html.js.hr
import kotlinx.html.js.p
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document
import kotlin.js.json

class BirefJSApp : Application, BirefUI {

    val frame = document.getElementById("plot") ?: throw RuntimeException("The plot element not found")
    val log = document.getElementById("log") as HTMLDivElement
    val oConst = json(
            "name" to "Калибровка",
            "legendgroup" to "Анализ:",
            "mode" to "lines",
            "type" to "scatter"
    )

    val oFit = json(
            "name" to "Фит (О)",
            "legendgroup" to "Анализ:",
            "mode" to "lines",
            "type" to "scatter"
    )

    val eFit = json(
            "name" to "Фит (Н)",
            "legendgroup" to "Анализ:",
            "mode" to "lines",
            "type" to "scatter"
    )

    val oData = json(
            "name" to "Данные (О)",
            "legendgroup" to "Данные:",
            "mode" to "markers",
            "type" to "scatter",
            "error_x" to json(
                    "type" to "data",
                    "visible" to true
            ),
            "error_y" to json(
                    "type" to "data",
                    "visible" to true
            )
    )

    val eData = json(
            "name" to "Данные (Н)",
            "legendgroup" to "Данные:",
            "mode" to "markers",
            "type" to "scatter",
            "error_x" to json(
                    "type" to "data",
                    "visible" to true
            ),
            "error_y" to json(
                    "type" to "data",
                    "visible" to true
            )
    )


    override var data: List<DataPoint>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override var alpha: Double
        get() {
            return (document.getElementById("aField") as HTMLInputElement).valueAsNumber
        }
        set(value) {
            (document.getElementById("aField") as HTMLInputElement).valueAsNumber = value
        }
    override var phiErr: Double
        get() {
            return (document.getElementById("errField") as HTMLInputElement).valueAsNumber
        }
        set(value) {
            (document.getElementById("errField") as HTMLInputElement).valueAsNumber = value
        }
    override var psiErr: Double
        get() {
            return (document.getElementById("errField") as HTMLInputElement).valueAsNumber
        }
        set(value) {
            (document.getElementById("errField") as HTMLInputElement).valueAsNumber = value
        }

    override fun message(m: String) {
        log.append {
            p {
                +m
            }
        }
    }

    override fun ln() {
        log.append {
            hr {}
        }
    }


    override fun plotOConst(const: Double, a: Double, b: Double) {
        oConst.apply {
            set("x", arrayOf(0, 1))
            set("y", arrayOf(const, const))
            set("visible", true)
            refresh()
        }
    }

    override fun plotOData(data: List<XYErrPoint>) {
        oData.apply {
            this["x"] = data.map { it.x }
            this["y"] = data.map { it.y }
            this["error_x.array"] = data.map { it.xErr }
            this["error_y.array"] = data.map { it.yErr }
        }
    }

    override fun plotEData(data: List<XYErrPoint>) {
        eData.apply {
            this["x"] = data.map { it.x }
            this["y"] = data.map { it.y }
            this["error_x.array"] = data.map { it.xErr }
            this["error_y.array"] = data.map { it.yErr }
        }
    }

    override fun plotOFit(a: Double, b: Double, func: (Double) -> Double) {
        val xs = (0..500).map { a + it * (b - a) / 499.0 }
        val ys = xs.map(func)
//        println("displaying o fit")
        oFit.apply {
            set("x", xs.toTypedArray())
            set("y", ys.toTypedArray())
            set("visible", true)
            refresh()
        }
    }

    override fun plotEFit(a: Double, b: Double, func: (Double) -> Double) {
        val xs = (0..500).map { a + it * (b - a) / 499 }
        val ys = xs.map(func)
        eFit.apply {
            set("x", xs.toTypedArray())
            set("y", ys.toTypedArray())
            set("visible", true)
            refresh()
        }
    }

    override fun clearFits() {
        oConst["visible"] = false
        oFit["visible"] = false
        eFit["visible"] = false
        refresh()
    }

    override fun cleatPlots() {
        oData["visible"] = false
        eData["visible"] = false
        refresh()
    }
//
//    private fun setVisible(trace: String, visible: Boolean) {
//        traces[trace]?.set("visible", visible)
//    }

    private fun refresh() {
        redraw(frame)
    }


    // js states
    override fun start(state: Map<String, Any>) {

        message("Starting application...")
        ln()

//        val test = arrayOf(json(
//                "x" to arrayOf(0, 1, 2),
//                "y" to arrayOf(6, 10, 2),
//                "name" to "test",
//                "mode" to "markers",
//                "error_y" to json(
//                        "type" to "data",
//                        "array" to arrayOf(1, 2, 3),
//                        "visible" to true
//                ),
//                "type" to "scatter"
//        ))
        val layout = json(
                "title" to "Графики",
                "showlegend" to true
        )
        newPlot(frame, arrayOf(oData, eData, oConst, oFit, eFit), layout)
        //plotOConst(1.0)
        plotOFit { it }
    }

    override fun dispose(): Map<String, Any> {
        return emptyMap()
    }

    override val stateKeys: List<String> = emptyList()
}