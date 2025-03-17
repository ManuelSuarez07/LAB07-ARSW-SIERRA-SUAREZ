const app = (function() {
    let selectedAuthor = "";
    let currentBlueprint = null;

    function setSelectedAuthor(author) {
        selectedAuthor = author;
    }

    function updateBlueprintsList(author) {
        $.get(`/blueprints/${author}`, function(data) {
            const blueprintsList = data.map(bp => ({
                name: bp.name,
                points: bp.points.length
            }));
            renderBlueprintsTable(blueprintsList);
            updateTotalPoints(blueprintsList);
        }).fail(function() {
            alert("Error al obtener los blueprints del autor.");
        });
    }

    function renderBlueprintsTable(blueprintsList) {
        const tableBody = $('#blueprintsTableBody');
        tableBody.empty();
        blueprintsList.forEach(bp => {
            tableBody.append(`
                <tr>
                    <td>${bp.name}</td>
                    <td>${bp.points}</td>
                    <td><button class="btn btn-info" onclick="app.viewBlueprint('${selectedAuthor}', '${bp.name}')">View</button></td>
                </tr>
            `);
        });
    }

    function updateTotalPoints(blueprintsList) {
        const totalPoints = blueprintsList.reduce((sum, bp) => sum + bp.points, 0);
        $('#totalPoints').text(totalPoints);
    }

    function viewBlueprint(author, bpname) {
        $.get(`/blueprints/${author}/${bpname}`, function(blueprint) {
            currentBlueprint = blueprint;
            drawBlueprint(blueprint);
            $('#currentBlueprintName').text(blueprint.name);
        }).fail(function() {
            alert("Error al obtener el blueprint.");
        });
    }

    function drawBlueprint(blueprint) {
        const canvas = document.getElementById('blueprintCanvas');
        const ctx = canvas.getContext('2d');
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        const points = blueprint.points;
        if (points.length > 0) {
            ctx.beginPath();
            ctx.moveTo(points[0].x, points[0].y);
            for (let i = 1; i < points.length; i++) {
                ctx.lineTo(points[i].x, points[i].y);
            }
            ctx.stroke();
        }
    }

    function setupCanvasClickHandler() {
        const canvas = document.getElementById('blueprintCanvas');
        canvas.addEventListener('pointerdown', function(event) {
            if (currentBlueprint) {
                const rect = canvas.getBoundingClientRect();
                const x = event.clientX - rect.left;
                const y = event.clientY - rect.top;

                // Enviar el nuevo punto al backend
                $.ajax({
                    url: `/blueprints/${selectedAuthor}/${currentBlueprint.name}/points`,
                    type: 'POST',
                    data: JSON.stringify({ x, y }),
                    contentType: "application/json",
                    success: function(updatedBlueprint) {
                        currentBlueprint = updatedBlueprint;
                        drawBlueprint(updatedBlueprint);
                    }
                }).fail(function() {
                    alert("Error al agregar el punto.");
                });
            }
        });
    }

    function saveUpdateBlueprint() {
        if (currentBlueprint) {
            $.ajax({
                url: `/blueprints/${selectedAuthor}/${currentBlueprint.name}`,
                type: 'PUT',
                data: JSON.stringify(currentBlueprint),
                contentType: "application/json",
                success: function() {
                    updateBlueprintsList(selectedAuthor);
                }
            }).fail(function() {
                alert("Error al actualizar el blueprint.");
            });
        }
    }

    function createNewBlueprint() {
        const blueprintName = prompt("Enter the name of the new blueprint:");
        if (blueprintName) {
            const newBlueprint = {
                author: selectedAuthor,
                name: blueprintName,
                points: []
            };
            $.ajax({
                url: `/blueprints`,
                type: 'POST',
                data: JSON.stringify(newBlueprint),
                contentType: "application/json",
                success: function() {
                    currentBlueprint = newBlueprint;
                    $('#currentBlueprintName').text(blueprintName);
                    drawBlueprint(currentBlueprint);
                    updateBlueprintsList(selectedAuthor);
                }
            }).fail(function() {
                alert("Error al crear el blueprint.");
            });
        }
    }

    function deleteBlueprint() {
        if (currentBlueprint) {
            $.ajax({
                url: `/blueprints/${selectedAuthor}/${currentBlueprint.name}`,
                type: 'DELETE',
                success: function() {
                    currentBlueprint = null;
                    $('#currentBlueprintName').text('');
                    const canvas = document.getElementById('blueprintCanvas');
                    const ctx = canvas.getContext('2d');
                    ctx.clearRect(0, 0, canvas.width, canvas.height);
                    updateBlueprintsList(selectedAuthor);
                }
            }).fail(function() {
                alert("Error al eliminar el blueprint.");
            });
        }
    }

    return {
        setSelectedAuthor,
        updateBlueprintsList,
        viewBlueprint,
        setupCanvasClickHandler,
        saveUpdateBlueprint,
        createNewBlueprint,
        deleteBlueprint
    };
})();

$(document).ready(function() {
    $('#getBlueprintsBtn').click(function() {
        const author = $('#authorInput').val();
        if (author) {
            app.setSelectedAuthor(author);
            app.updateBlueprintsList(author);
        } else {
            alert("Please enter an author name.");
        }
    });

    $('#createNewBtn').click(function() {
        app.createNewBlueprint();
    });

    $('#saveUpdateBtn').click(function() {
        app.saveUpdateBlueprint();
    });

    $('#deleteBtn').click(function() {
        app.deleteBlueprint();
    });

    app.setupCanvasClickHandler();
});