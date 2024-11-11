<?php
    // Establecer conexión con la base de datos
    $conexion = mysqli_connect('127.0.0.1', 'root', '', 'agenda');
    
    if (!$conexion) {
        die('La conexión no se ha podido realizar.' . mysqli_error());
    } else {
        // Obtener el idContacto desde la URL
        $idContacto = isset($_GET['idContacto']) ? $_GET['idContacto'] : '';

        if (empty($idContacto)) {
            // Si no se pasa idContacto, devolver un error
            echo "No se ha proporcionado un idContacto válido.";
        } else {
            // Instrucción SQL para seleccionar el contacto por idContacto
            $consulta = "SELECT idContacto, Nombre, Apellidos, Telefono, Email FROM Contactos WHERE idContacto = '$idContacto'";
            
            // Ejecutar la consulta
            $resultado = mysqli_query($conexion, $consulta) or die(mysqli_error($conexion));
            
            // Obtener el número de registros
            $registros = mysqli_num_rows($resultado);
            
            // Si existen registros, devolver el contacto
            if ($registros > 0) {
                $fila = mysqli_fetch_object($resultado);
                // Convertir el resultado a JSON
                echo json_encode($fila);
            } else {
                // Si no se encuentra el contacto, devolver "0"
                echo "0";
            }

            // Cerrar la conexión con la base de datos
            mysqli_close($conexion);
        }
    }
?>
