let detailsForm;
let filterForm;

function makeEditable(datatableApi) {
    ctx.datatableApi = datatableApi;
    detailsForm = $('#detailsForm');
    filterForm = $('#filterForm');
    $(".checkbox").change(function () {
        changeState($(this).closest('tr'));
    });
    $(".delete").click(function () {
        if (confirm('Are you sure?')) {
            deleteRow($(this).closest('tr').attr("id"));
        }
    });

    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(jqXHR);
    });

    // solve problem with cache in IE: https://stackoverflow.com/a/4303862/548473
    $.ajaxSetup({cache: false});
}

function changeState(tr) {
    if (tr.attr("data-enabled") === "true") {
        tr.attr("data-enabled", "false");
        disable(tr.attr("id"));
    } else {
        tr.attr("data-enabled", "true");
        enable(tr.attr("id"));
    }
}

function enable(id) {
    $.ajax({
        url: ctx.ajaxUrl + "enable/" + id,
        type: "PUT"
    }).done(function () {
        successNoty("Enabled");
    });
}

function disable(id) {
    $.ajax({
        url: ctx.ajaxUrl + "disable/" + id,
        type: "PUT"
    }).done(function () {
        successNoty("Disabled");
    });
}

function add() {
    detailsForm.find(":input").val("");
    $("#editRow").modal();
}

function deleteRow(id) {
    $.ajax({
        url: ctx.ajaxUrl + id,
        type: "DELETE"
    }).done(function () {
        updateTable();
        successNoty("Deleted");
    });
}

function updateTable() {
    $.get(ctx.ajaxUrl, function (data) {
        ctx.datatableApi.clear().rows.add(data).draw();
    });
}

function save() {
    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl,
        data: detailsForm.serialize()
    }).done(function () {
        $("#editRow").modal("hide");
        updateTable();
        successNoty("Saved");
    });
}

function filter() {
    $.ajax({
        type: "GET",
        url: ctx.ajaxUrl + filterUrl()
    }).done(function (data) {
        ctx.datatableApi.clear().rows.add(data).draw();
        successNoty("Filtered");
    });
}

function filterUrl() {
    return "filter" + "?" + "startDate=" + filterForm.find("#startDate").val() + "&" +
        "endDate=" + filterForm.find("#endDate").val() + "&" +
        "startTime=" + filterForm.find("#startTime").val() + "&" +
        "endTime=" + filterForm.find("#endTime").val();
}

function resetFilter() {
    filterForm.find("#startDate").val(undefined);
    filterForm.find("#endDate").val(undefined);
    filterForm.find("#startTime").val(undefined);
    filterForm.find("#endTime").val(undefined);

    updateTable();
    successNoty("Reset");
}

let failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

function successNoty(text) {
    closeNoty();
    new Noty({
        text: "<span class='fa fa-lg fa-check'></span> &nbsp;" + text,
        type: 'success',
        layout: "bottomRight",
        timeout: 1000
    }).show();
}

function failNoty(jqXHR) {
    closeNoty();
    failedNote = new Noty({
        text: "<span class='fa fa-lg fa-exclamation-circle'></span> &nbsp;Error status: " + jqXHR.status,
        type: "error",
        layout: "bottomRight"
    });
    failedNote.show()
}