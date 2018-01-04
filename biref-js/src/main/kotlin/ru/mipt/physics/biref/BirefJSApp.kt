package ru.mipt.physics.biref

import jquery.jq
import kotlinx.html.dom.append
import kotlinx.html.js.hr
import kotlinx.html.js.p
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document
import kotlin.js.Json
import kotlin.js.json

class BirefJSApp : Application, BirefUI {

    val frame = document.getElementById("plot")!!
    val table = document.getElementById("table")!!

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


    data class MutableDataPoint(var phi1: Double, var psio: Double, var psie: Double)

    /**
     * Internal data in degrees
     */
    private val _data: MutableList<MutableDataPoint> = ArrayList()

    /**
     * Data for calculation in radians
     */
    override var data: List<DataPoint>
        get() = _data.map { DataPoint(it.phi1 * D2R, it.psio * D2R, it.psie * D2R) }
        set(value) {
            _data.clear()
            _data.addAll(value.map { MutableDataPoint(it.phi1 / D2R, it.psio / D2R, it.psie / D2R) })
            refresh()
        }

    override var alpha: Double
        get() {
            return (document.getElementById("aField") as HTMLInputElement).valueAsNumber * D2R
        }
        set(value) {
            (document.getElementById("aField") as HTMLInputElement).valueAsNumber = value / D2R
        }
    override var phiErr: Double
        get() {
            return (document.getElementById("errField") as HTMLInputElement).valueAsNumber * D2R
        }
        set(value) {
            (document.getElementById("errField") as HTMLInputElement).valueAsNumber = value / D2R
        }
    override var psiErr: Double
        get() {
            return (document.getElementById("errField") as HTMLInputElement).valueAsNumber * D2R
        }
        set(value) {
            (document.getElementById("errField") as HTMLInputElement).valueAsNumber = value / D2R
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
            set("x", arrayOf(a, b))
            set("y", arrayOf(const, const))
            set("visible", true)
        }
        refresh()
    }

    override fun plotOData(data: List<XYErrPoint>) {
        println("plotting o data")
        oData.apply {
            this["x"] = data.map { it.x }.toTypedArray()
            this["y"] = data.map { it.y }.toTypedArray()
            (this["error_x"] as Json)["array"] = data.map { it.xErr }.toTypedArray()
            (this["error_y"] as Json)["array"] = data.map { it.yErr }.toTypedArray()
        }
        refresh()
    }

    override fun plotEData(data: List<XYErrPoint>) {
        println("plotting e data")
        eData.apply {
            this["x"] = data.map { it.x }.toTypedArray()
            this["y"] = data.map { it.y }.toTypedArray()
            (this["error_x"] as Json)["array"] = data.map { it.xErr }.toTypedArray()
            (this["error_y"] as Json)["array"] = data.map { it.yErr }.toTypedArray()
        }
        refresh()
    }

    override fun plotOFit(a: Double, b: Double, func: (Double) -> Double) {
        val xs = (0..500).map { a + it * (b - a) / 499.0 }
        val ys = xs.map(func)
//        println("displaying o fit")
        oFit.apply {
            set("x", xs.toTypedArray())
            set("y", ys.toTypedArray())
            set("visible", true)
        }
        refresh()
    }

    override fun plotEFit(a: Double, b: Double, func: (Double) -> Double) {
        val xs = (0..500).map { a + it * (b - a) / 499 }
        val ys = xs.map(func)
        eFit.apply {
            set("x", xs.toTypedArray())
            set("y", ys.toTypedArray())
            set("visible", true)
        }
        refresh()
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

    private fun refresh() {
        redraw(frame)
    }

    private fun updateTable() {
        jq(table).asDynamic().jsGrid("render")
    }

    private fun updatePlot() {
        updateTable()
        updateDataPlot(this)
    }

    // js states
    override fun start(state: Map<String, Any>) {

        message("Starting application...")
        ln()
        val layout = js {
            title = "Графики"
            showlegend = true
            xaxis = js {
                title = "cos^2(theta)"
                rangemode = "tozero"
            }
            yaxis = js {
                title = "n"
            }
            legend = js {
                orientation = "h"
                xanchor = "auto"
                y = -0.2
                yanchor = "top"
            }
            margin = js {
                t = 50 //top margin
                l = 30 //left margin
                r = 30 //right margin
                b = 40 //bottom margin
            }
        }
        newPlot(frame, arrayOf(oData, eData, oConst, oFit, eFit), layout)

        js("""

        function FloatField(config) {
            jsGrid.NumberField.call(this, config);
        }

        FloatField.prototype = new jsGrid.NumberField({

            filterValue: function() {
                return parseFloat(this.filterControl.val() || 0);
            },

            insertValue: function() {
                return parseFloat(this.insertControl.val() || 0);
            },

            editValue: function() {
                return parseFloat(this.editControl.val() || 0);
            },

            _createTextBox: function() {
                return ${'$'}("<input>")
                .attr("type", "number")
                .attr("step", 0.1)
                .prop("readonly", !!this.readOnly);
            }

        });

        jsGrid.fields.float = FloatField;
        """)



        jq(table).asDynamic().jsGrid(js
        {
            width = "100%"
            height = "400px"

            /*
             * look here: {@link http://js-grid.com/docs/#grid-controller} for reference
             */
            controller = js {
                loadData = { filter: dynamic ->
                    println("loading items with filter: $filter")
                    _data.toTypedArray()
                }
                insertItem = { item: dynamic ->
                    println("inserting item: $item")
                    val point = MutableDataPoint(
                            phi1 = item.phi1,
                            psio = item.psio,
                            psie = item.psie
                    )
                    _data.add(point)
                    point
                }

                deleteItem = { item: dynamic ->
                    println("removing item: $item.")
                    _data.remove(item as MutableDataPoint)
                    item
                }
            }

            autoload = true
            confirmDeleting = false

            inserting = true
            editing = true
            sorting = true

            fields = arrayOf(
                    js { name = "phi1"; width = 20; validate = "required"; type = "float" },
                    js { name = "psio"; width = 20; type = "float" },
                    js { name = "psie"; width = 20; type = "float" },
                    js { type = "control"; width = 20; editButton = false }
            )
        })

        (document.getElementById("saveButton") as? HTMLButtonElement)?.onclick = {
            println(_data.joinToString("\n", "[", "]") { it.toString() })
        }

        (document.getElementById("loadButton") as? HTMLButtonElement)?.onclick = {
            val data = "\t10\t32.5\t22\t\n" +
                    "\t15\t30\t21\t    \n" +
                    "\t20\t28\t20.5    \n" +
                    "\t25\t27\t20\t\n" +
                    "\t27.5\t26.7\t20\t        \n" +
                    "\t30\t26.5\t20\t        \n" +
                    "\t32.5\t26.5\t20.5    \n" +
                    "\t35\t27\t21\t    \n" +
                    "\t40 \t27\t21.5\n" +
                    "\t45\t28\t22.5\n" +
                    "\t50\t29\t24\t\n" +
                    "\t55\t30.5\t25.5  \n" +
                    "\t60\t32\t27\t\n" +
                    "\t65\t34.5\t30\t\n" +
                    "\t70\t37\t32.5"

            data.lines().forEach {
                val values = it.trim().split(Regex("\\s+"))
                _data.add(MutableDataPoint(phi1 = values[0].toDouble(), psio = values[1].toDouble(), psie = values[2].toDouble()));
            }
            updateTable()
            updatePlot()
        }


        (document.getElementById("calibrateButton") as? HTMLButtonElement)?.onclick = {
            calibrate(this)
        }

        (document.getElementById("analyzeButton") as? HTMLButtonElement)?.onclick = {
            analyze(this)
        }

        (document.getElementById("aField") as? HTMLInputElement)?.onchange = {
            updatePlot()
        }

        (document.getElementById("errField") as? HTMLInputElement)?.onchange = {
            updatePlot()
        }

    }

    override fun dispose(): Map<String, Any> {
        return emptyMap()
    }

    override val stateKeys: List<String> = emptyList()

}