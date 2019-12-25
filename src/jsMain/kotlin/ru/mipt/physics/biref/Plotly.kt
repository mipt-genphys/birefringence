package ru.mipt.physics.biref

import org.w3c.dom.Element

private val plotly = js("Plotly")

fun newPlot(div: Element, data: dynamic, layout: dynamic = null): dynamic {
    return plotly.newPlot(div, data, layout)
}

fun deleteTraces(div: Element, vararg num: Int): dynamic {
    return plotly.deleteTraces(div, num);
}

fun redraw(div: Element){
    plotly.redraw(div)
}