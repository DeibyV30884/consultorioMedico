// funcion para hacer un preview de una imagen 
function mostrarImagen(input) {
    if (input.files && input.files[0]) {
        const imagen = input.files[0];
        const maximo = 512 * 1024; //Se limita el tamaño a 512 Kb las imágenes.
        if (imagen.size <= maximo) {
            var lector = new FileReader();
            lector.onload = function (e) {
                $('#blah').attr('src', e.target.result).height(200);
            };
            lector.readAsDataURL(input.files[0]);
        } else {
            alert("La imagen seleccionada es muy grande... no debe superar los 512 Kb!");
        }
    }
}

//Para insertar información en el modal según el registro...
document.addEventListener('DOMContentLoaded', function () {
    const confirmModal = document.getElementById('confirmModal');
    if (confirmModal) {
        confirmModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            document.getElementById('modalId').value = button.getAttribute('data-bs-id');
            document.getElementById('modalDescripcion').textContent = button.getAttribute('data-bs-descripcion');
        });
    }
    
    const modalCancelar = document.getElementById('modalCancelar');
    if (modalCancelar) {
        modalCancelar.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const citaId = button.getAttribute('data-bs-id');
            const descripcion = button.getAttribute('data-bs-descripcion');
            const inputId = modalCancelar.querySelector('input[name="idCita"]');
            const spanDescripcion = modalCancelar.querySelector('#modalDescripcion');
            
            if (inputId) {
                inputId.value = citaId;
            }
            if (spanDescripcion) {
                spanDescripcion.textContent = descripcion;
            }
        });
    }
});

//Para quitar toast
setTimeout(() => {
    document.querySelectorAll('.toast').forEach(t => t.classList.remove('show'));
}, 4000);