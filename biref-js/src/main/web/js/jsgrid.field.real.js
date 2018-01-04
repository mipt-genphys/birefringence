(function(jsGrid, $, undefined) {

    var TextField = jsGrid.TextField;

    function RealField(config) {
        TextField.call(this, config);
    }

    RealField.prototype = new RealField({

        sorter: "number",
        align: "right",
        readOnly: false,

        filterValue: function() {
            return this.filterControl.val()
                ? parseFloat(this.filterControl.val() || 0)
                : undefined;
        },

        insertValue: function() {
            return this.insertControl.val()
                ? parseFloat(this.insertControl.val() || 0)
                : undefined;
        },

        editValue: function() {
            return this.editControl.val()
                ? parseFloat(this.editControl.val() || 0)
                : undefined;
        },

        _createTextBox: function() {
            return $("<input>").attr("type", "real")
                .prop("readonly", !!this.readOnly);
        }
    });

    jsGrid.fields.real = jsGrid.RealField = RealField;

}(jsGrid, jQuery));