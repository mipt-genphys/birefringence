@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")

import org.w3c.dom.HTMLElement

class Partial<T>
external interface StaticPlots {
    fun resize(root: String)
    fun resize(root: HTMLElement)
}

external interface Point {
    var x: Number
    var y: Number
    var z: Number
}

external interface `T$0` {
    var pointNumber: Number
    var curveNumber: Number
    var data: ScatterData
}

external interface `T$1` {
    var points: Array<`T$0`>
}

external interface `T$2` {
    var x: Number
    var y: Number
    var pointNumber: Number
}

external interface `T$3` {
    var points: Array<`T$2`>
}

external interface `T$4` {
    var range: dynamic /* JsTuple<Number, Number> */
    var autorange: Boolean
}

external interface `T$5` {
    var center: Point
    var eye: Point
    var up: Point
}

external interface `T$6` {
    var xaxis: `T$4`
    var yaxis: `T$4`
    var scene: `T$5`
}

external interface PlotlyHTMLElement : HTMLElement {
    fun on(event: String /* "plotly_click" */, callback: (data: `T$1`) -> Unit)
    fun on(event: String /* "plotly_hover" */, callback: (data: `T$1`) -> Unit)
    fun on(event: String /* "plotly_unhover" */, callback: (data: `T$1`) -> Unit)
    fun on(event: String /* "plotly_selecting" */, callback: (data: `T$3`) -> Unit)
    fun on(event: String /* "plotly_selected" */, callback: (data: `T$3`) -> Unit)
    fun on(event: String /* "plotly_restyle" */, callback: (data: dynamic /* JsTuple<Any, Array<Number>> */) -> Unit)
    fun on(event: String /* "plotly_relayout" */, callback: (data: `T$6`) -> Unit)
    fun on(event: String /* "plotly_afterplot" */, callback: () -> Unit)
    fun on(event: String /* "plotly_autosize" */, callback: () -> Unit)
    fun on(event: String /* "plotly_deselect" */, callback: () -> Unit)
    fun on(event: String /* "plotly_doubleclick" */, callback: () -> Unit)
    fun on(event: String /* "plotly_redraw" */, callback: () -> Unit)
    fun on(event: String /* "plotly_animated" */, callback: () -> Unit)
}

external interface ToImgopts {
    var format: dynamic /* String /* "jpeg" */ | String /* "png" */ | String /* "webp" */ | String /* "svg" */ */
    var width: Number
    var height: Number
}

external interface DownloadImgopts {
    var format: dynamic /* String /* "jpeg" */ | String /* "png" */ | String /* "webp" */ | String /* "svg" */ */
    var width: Number
    var height: Number
    var filename: String
}

external interface Layout {
    var title: String
    var titlefont: Partial<Font>
    var autosize: Boolean
    var showlegend: Boolean
    var paper_bgcolor: dynamic /* String | Array<String> | Array<Array<String>> */
    var plot_bgcolor: dynamic /* String | Array<String> | Array<Array<String>> */
    var separators: String
    var hidesources: Boolean
    var xaxis: Partial<LayoutAxis>
    var yaxis: Partial<LayoutAxis>
    var yaxis2: Partial<LayoutAxis>
    var yaxis3: Partial<LayoutAxis>
    var yaxis4: Partial<LayoutAxis>
    var yaxis5: Partial<LayoutAxis>
    var yaxis6: Partial<LayoutAxis>
    var yaxis7: Partial<LayoutAxis>
    var yaxis8: Partial<LayoutAxis>
    var yaxis9: Partial<LayoutAxis>
    var margin: Partial<Margin>
    var height: Number
    var width: Number
    var hovermode: dynamic /* Boolean | String /* "closest" */ | String /* "x" */ | String /* "y" */ */
    var hoverlabel: Partial<Label>
    var calendar: dynamic /* String /* "gregorian" */ | String /* "chinese" */ | String /* "coptic" */ | String /* "discworld" */ | String /* "ethiopian" */ | String /* "hebrew" */ | String /* "islamic" */ | String /* "julian" */ | String /* "mayan" */ | String /* "nanakshahi" */ | String /* "nepali" */ | String /* "persian" */ | String /* "jalali" */ | String /* "taiwan" */ | String /* "thai" */ | String /* "ummalqura" */ */
    var ternary: Any
    var geo: Any
    var mapbox: Any
    var radialaxis: Any
    var angularaxis: Any
    var direction: dynamic /* String /* "clockwise" */ | String /* "counterclockwise" */ */
    var dragmode: dynamic /* String /* "zoom" */ | String /* "pan" */ | String /* "select" */ | String /* "lasso" */ | String /* "orbit" */ | String /* "turntable" */ */
    var orientation: Number
    var annotations: Any
    var shapes: Array<Partial<Shape>>
    var images: Any
    var updatemenus: Any
    var sliders: Any
    var legend: Partial<Legend>
    var font: Partial<Font>
    var scene: Partial<Scene>
}

external interface Legend : Label {
    var traceorder: dynamic /* String /* "grouped" */ | String /* "normal" */ | String /* "reversed" */ */
    var x: Number
    var y: Number
    var borderwidth: Number
    var orientation: dynamic /* String /* "v" */ | String /* "h" */ */
    var tracegroupgap: Number
    var xanchor: dynamic /* String /* "auto" */ | String /* "left" */ | String /* "center" */ | String /* "right" */ */
    var yanchor: dynamic /* String /* "auto" */ | String /* "top" */ | String /* "middle" */ | String /* "bottom" */ */
}

external interface Axis {
    var visible: Boolean
    var color: dynamic /* String | Array<String> | Array<Array<String>> */
    var title: String
    var titlefont: Partial<Font>
    var type: dynamic /* String /* "-" */ | String /* "linear" */ | String /* "log" */ | String /* "date" */ | String /* "category" */ */
    var autorange: dynamic /* Boolean | String /* "reversed" */ */
    var rangemode: dynamic /* String /* "normal" */ | String /* "tozero" */ | String /* "nonnegative" */ */
    var range: Array<Any>
    var tickmode: dynamic /* String /* "auto" */ | String /* "linear" */ | String /* "array" */ */
    var nticks: Number
    var tick0: dynamic /* String | Number */
    var dtick: dynamic /* String | Number */
    var tickvals: Array<Any>
    var ticktext: Array<String>
    var ticks: dynamic /* String /* "" */ | String /* "outside" */ | String /* "inside" */ */
    var mirror: dynamic /* Boolean | String /* "ticks" */ | String /* "all" */ | String /* "allticks" */ */
    var ticklen: Number
    var tickwidth: Number
    var tickcolor: dynamic /* String | Array<String> | Array<Array<String>> */
    var showticklabels: Boolean
    var showspikes: Boolean
    var spikecolor: dynamic /* String | Array<String> | Array<Array<String>> */
    var spikethickness: Number
    var categoryorder: dynamic /* String /* "array" */ | String /* "trace" */ | String /* "category ascending" */ | String /* "category descending" */ */
    var categoryarray: Array<Any>
    var tickfont: Partial<Font>
    var tickangle: Number
    var tickprefix: String
    var showtickprefix: dynamic /* String /* "all" */ | String /* "first" */ | String /* "last" */ | String /* "none" */ */
    var ticksuffix: String
    var showticksuffix: dynamic /* String /* "all" */ | String /* "first" */ | String /* "last" */ | String /* "none" */ */
    var showexponent: dynamic /* String /* "all" */ | String /* "first" */ | String /* "last" */ | String /* "none" */ */
    var exponentformat: dynamic /* String /* "none" */ | String /* "e" */ | String /* "E" */ | String /* "power" */ | String /* "SI" */ | String /* "B" */ */
    var separatethousands: Boolean
    var tickformat: String
    var hoverformat: String
    var showline: Boolean
    var linecolor: dynamic /* String | Array<String> | Array<Array<String>> */
    var linewidth: Number
    var showgrid: Boolean
    var gridcolor: dynamic /* String | Array<String> | Array<Array<String>> */
    var gridwidth: Number
    var zeroline: Boolean
    var zerolinecolor: dynamic /* String | Array<String> | Array<Array<String>> */
    var zerolinewidth: Number
    var calendar: dynamic /* String /* "gregorian" */ | String /* "chinese" */ | String /* "coptic" */ | String /* "discworld" */ | String /* "ethiopian" */ | String /* "hebrew" */ | String /* "islamic" */ | String /* "julian" */ | String /* "mayan" */ | String /* "nanakshahi" */ | String /* "nepali" */ | String /* "persian" */ | String /* "jalali" */ | String /* "taiwan" */ | String /* "thai" */ | String /* "ummalqura" */ */
}

external interface LayoutAxis : Axis {
    var fixedrange: Boolean
    var scaleanchor: dynamic /* String /* "/^x([2-9]|[1-9][0-9]+)?$/" */ | String /* "/^y([2-9]|[1-9][0-9]+)?$/" */ */
    var scaleratio: Number
    var constrain: dynamic /* String /* "range" */ | String /* "domain" */ */
    var constraintoward: dynamic /* String /* "left" */ | String /* "center" */ | String /* "right" */ | String /* "top" */ | String /* "middle" */ | String /* "bottom" */ */
    var spikedash: String
    var spikemode: String
    var anchor: dynamic /* String /* "/^x([2-9]|[1-9][0-9]+)?$/" */ | String /* "/^y([2-9]|[1-9][0-9]+)?$/" */ | String /* "free" */ */
    var side: dynamic /* String /* "left" */ | String /* "right" */ | String /* "top" */ | String /* "bottom" */ */
    var overlaying: dynamic /* String /* "/^x([2-9]|[1-9][0-9]+)?$/" */ | String /* "/^y([2-9]|[1-9][0-9]+)?$/" */ | String /* "free" */ */
    var layer: dynamic /* String /* "above traces" */ | String /* "below traces" */ */
    var domain: Array<Number>
    var position: Number
    var rangeslider: Partial<RangeSlider>
    var rangeselector: Partial<RangeSelector>
}

external interface SceneAxis : Axis {
    var spikesides: Boolean
    var showbackground: Boolean
    var backgroundcolor: dynamic /* String | Array<String> | Array<Array<String>> */
    var showaxeslabels: Boolean
}

external interface ShapeLine {
    var color: String
    var width: Number
    var dash: dynamic /* String /* "solid" */ | String /* "dot" */ | String /* "dash" */ | String /* "longdash" */ | String /* "dashdot" */ | String /* "longdashdot" */ */
}

external interface Shape {
    var visible: Boolean
    var layer: dynamic /* String /* "below" */ | String /* "above" */ */
    var type: dynamic /* String /* "rect" */ | String /* "circle" */ | String /* "line" */ | String /* "path" */ */
    var path: String
    var xref: dynamic /* String /* "x" */ | String /* "paper" */ */
    var yref: dynamic /* String /* "y" */ | String /* "paper" */ */
    var x0: dynamic /* String | Number | Date */
    var y0: dynamic /* String | Number | Date */
    var x1: dynamic /* String | Number | Date */
    var y1: dynamic /* String | Number | Date */
    var fillcolor: String
    var opacity: Number
    var line: Partial<ShapeLine>
}

external interface Margin {
    var t: Number
    var b: Number
    var l: Number
    var r: Number
}

external interface ScatterData {
    var type: dynamic /* String /* "bar" */ | String /* "scatter" */ | String /* "scattergl" */ | String /* "scatter3d" */ */
    var x: dynamic /* Array<dynamic /* String | Number | Date */> | Array<Array<dynamic /* String | Number | Date */>> */
    var y: dynamic /* Array<dynamic /* String | Number | Date */> | Array<Array<dynamic /* String | Number | Date */>> */
    var z: dynamic /* Array<dynamic /* String | Number | Date */> | Array<Array<dynamic /* String | Number | Date */>> | Array<Array<Array<dynamic /* String | Number | Date */>>> */
    var xaxis: String
    var yaxis: String
    var text: dynamic /* String | Array<String> */
    var line: Partial<ScatterLine>
    var marker: Partial<ScatterMarker>
    var mode: dynamic /* String /* "none" */ | String /* "lines" */ | String /* "markers" */ | String /* "text" */ | String /* "lines+markers" */ | String /* "text+markers" */ | String /* "text+lines" */ | String /* "text+lines+markers" */ */
    var hoveron: dynamic /* String /* "points" */ | String /* "fills" */ */
    var hoverinfo: String /* "text" */
    var fill: dynamic /* String /* "none" */ | String /* "tozeroy" */ | String /* "tozerox" */ | String /* "tonexty" */ | String /* "tonextx" */ | String /* "toself" */ | String /* "tonext" */ */
    var fillcolor: String
    var legendgroup: String
    var name: String
    var connectgaps: Boolean
}

external interface ScatterMarker {
    var symbol: dynamic /* String | Array<String> */
    var color: dynamic /* String | Array<Number> | Array<String> | Array<Array<String>> */
    var colorscale: dynamic /* String | Array<String> */
    var cauto: Boolean
    var cmax: Boolean
    var cmin: Boolean
    var autocolorscale: Boolean
    var reversescale: Boolean
    var opacity: dynamic /* Number | Array<Number> */
    var size: dynamic /* Number | Array<Number> */
    var maxdisplayed: Number
    var sizeref: Number
    var sizemin: Number
    var sizemode: dynamic /* String /* "diameter" */ | String /* "area" */ */
    var showscale: Boolean
    var line: Partial<ScatterMarkerLine>
    var colorbar: Any
    var gradient: Any
}

external interface ScatterMarkerLine {
    var width: dynamic /* Number | Array<Number> */
    var color: dynamic /* String | Array<String> | Array<Array<String>> */
    var colorscale: dynamic /* String | Array<String> */
    var cauto: Boolean
    var cmax: Number
    var cmin: Number
    var autocolorscale: Boolean
    var reversescale: Boolean
}

external interface ScatterLine {
    var color: dynamic /* String | Array<String> | Array<Array<String>> */
    var width: Number
    var dash: dynamic /* String /* "solid" */ | String /* "dot" */ | String /* "dash" */ | String /* "longdash" */ | String /* "dashdot" */ | String /* "longdashdot" */ */
    var shape: dynamic /* String /* "linear" */ | String /* "spline" */ | String /* "hv" */ | String /* "vh" */ | String /* "hvh" */ | String /* "vhv" */ */
    var smoothing: Number
    var simplify: Boolean
}

external interface Font {
    var family: String
    var size: Number
    var color: String
}

external interface Config {
    var staticPlot: Boolean
    var editable: Boolean
    var autosizable: Boolean
    var queueLength: Number
    var fillFrame: Boolean
    var frameMargins: Number
    var scrollZoom: Boolean
    var doubleClick: dynamic /* Boolean | String /* "reset+autosize" */ | String /* "reset" */ | String /* "autosize" */ */
    var showTips: Boolean
    var showLink: Boolean
    var sendData: Boolean
    var linkText: String
    var showSources: Boolean
    var displayModeBar: dynamic /* Boolean | String /* "hover" */ */
    var modeBarButtonsToRemove: Array<dynamic /* String /* "lasso2d" */ | String /* "select2d" */ | String /* "sendDataToCloud" */ | String /* "autoScale2d" */ | String /* "zoom2d" */ | String /* "pan2d" */ | String /* "zoomIn2d" */ | String /* "zoomOut2d" */ | String /* "resetScale2d" */ | String /* "hoverClosestCartesian" */ | String /* "hoverCompareCartesian" */ | String /* "zoom3d" */ | String /* "pan3d" */ | String /* "orbitRotation" */ | String /* "tableRotation" */ | String /* "resetCameraDefault3d" */ | String /* "resetCameraLastSave3d" */ | String /* "hoverClosest3d" */ | String /* "zoomInGeo" */ | String /* "zoomOutGeo" */ | String /* "resetGeo" */ | String /* "hoverClosestGeo" */ | String /* "hoverClosestGl2d" */ | String /* "hoverClosestPie" */ | String /* "toggleHover" */ | String /* "toImage" */ | String /* "resetViews" */ | String /* "toggleSpikelines" */ */>
    var modeBarButtonsToAdd: Array<dynamic /* String /* "lasso2d" */ | String /* "select2d" */ | String /* "sendDataToCloud" */ | String /* "autoScale2d" */ | String /* "zoom2d" */ | String /* "pan2d" */ | String /* "zoomIn2d" */ | String /* "zoomOut2d" */ | String /* "resetScale2d" */ | String /* "hoverClosestCartesian" */ | String /* "hoverCompareCartesian" */ | String /* "zoom3d" */ | String /* "pan3d" */ | String /* "orbitRotation" */ | String /* "tableRotation" */ | String /* "resetCameraDefault3d" */ | String /* "resetCameraLastSave3d" */ | String /* "hoverClosest3d" */ | String /* "zoomInGeo" */ | String /* "zoomOutGeo" */ | String /* "resetGeo" */ | String /* "hoverClosestGeo" */ | String /* "hoverClosestGl2d" */ | String /* "hoverClosestPie" */ | String /* "toggleHover" */ | String /* "toImage" */ | String /* "resetViews" */ | String /* "toggleSpikelines" */ */>
    var modeBarButtons: Array<Array<dynamic /* String /* "lasso2d" */ | String /* "select2d" */ | String /* "sendDataToCloud" */ | String /* "autoScale2d" */ | String /* "zoom2d" */ | String /* "pan2d" */ | String /* "zoomIn2d" */ | String /* "zoomOut2d" */ | String /* "resetScale2d" */ | String /* "hoverClosestCartesian" */ | String /* "hoverCompareCartesian" */ | String /* "zoom3d" */ | String /* "pan3d" */ | String /* "orbitRotation" */ | String /* "tableRotation" */ | String /* "resetCameraDefault3d" */ | String /* "resetCameraLastSave3d" */ | String /* "hoverClosest3d" */ | String /* "zoomInGeo" */ | String /* "zoomOutGeo" */ | String /* "resetGeo" */ | String /* "hoverClosestGeo" */ | String /* "hoverClosestGl2d" */ | String /* "hoverClosestPie" */ | String /* "toggleHover" */ | String /* "toImage" */ | String /* "resetViews" */ | String /* "toggleSpikelines" */ */>>
    var displaylogo: Boolean
    var plotGlPixelRatio: Number
    var setBackground: dynamic /* String | String /* "opaque" */ */
    var topojsonURL: String
    var mapboxAccessToken: String
    var logging: Boolean
    var globalTransforms: Array<Any>
}

external interface RangeSlider {
    var visible: Boolean
    var thickness: Number
    var range: dynamic /* JsTuple<dynamic /* String | Number | Date */, dynamic /* String | Number | Date */> */
    var borderwidth: Number
    var bordercolor: String
    var bgcolor: String
}

external interface RangeSelectorButton {
    var step: dynamic /* String /* "all" */ | String /* "second" */ | String /* "minute" */ | String /* "hour" */ | String /* "day" */ | String /* "month" */ | String /* "year" */ */
    var stepmode: dynamic /* String /* "backward" */ | String /* "todate" */ */
    var count: Number
    var label: String
}

external interface RangeSelector : Label {
    var buttons: Array<Partial<RangeSelectorButton>>
    var visible: Boolean
    var x: Number
    var xanchor: dynamic /* String /* "auto" */ | String /* "left" */ | String /* "center" */ | String /* "right" */ */
    var y: Number
    var yanchor: dynamic /* String /* "auto" */ | String /* "top" */ | String /* "middle" */ | String /* "bottom" */ */
    var activecolor: String
    var borderwidth: Number
}

external interface Camera {
    var up: Partial<Point>
    var center: Partial<Point>
    var eye: Partial<Point>
}

external interface Label {
    var bgcolor: String
    var bordercolor: String
    var font: Partial<Font>
}

external interface Annotations : Point, Label {
    var visible: Boolean
    var ax: Number
    var ay: Number
    var xanchor: dynamic /* String /* "auto" */ | String /* "left" */ | String /* "center" */ | String /* "right" */ */
    var xshift: Number
    var yanchor: dynamic /* String /* "auto" */ | String /* "top" */ | String /* "middle" */ | String /* "bottom" */ */
    var yshift: Number
    var text: String
    var textangle: String
    var width: Number
    var height: Number
    var opacity: Number
    var align: dynamic /* String /* "left" */ | String /* "center" */ | String /* "right" */ */
    var valign: dynamic /* String /* "top" */ | String /* "middle" */ | String /* "bottom" */ */
    var borderpad: Number
    var borderwidth: Number
    var showarrow: Boolean
    var arrowcolor: String
    var arrowhead: Number
    var arrowsize: Number
    var arrowwidth: Number
    var standoff: Number
    var hovertext: String
    var hoverlabel: Partial<Label>
    var captureevents: Boolean
}

external interface Scene {
    var bgcolor: String
    var camera: Partial<Camera>
    var domain: Partial<Domain>
    var aspectmode: dynamic /* String /* "auto" */ | String /* "cube" */ | String /* "data" */ | String /* "manual" */ */
    var aspectratio: Partial<Point>
    var xaxis: Partial<SceneAxis>
    var yaxis: Partial<SceneAxis>
    var zaxis: Partial<SceneAxis>
    var dragmode: dynamic /* Boolean | String /* "zoom" */ | String /* "pan" */ | String /* "orbit" */ | String /* "turntable" */ */
    var hovermode: dynamic /* Boolean | String /* "closest" */ */
    var annotations: dynamic /* Partial<Annotations> | Array<Partial<Annotations>> */
    var captureevents: Boolean
}

external interface Domain {
    var x: Array<Number>
    var y: Array<Number>
}
