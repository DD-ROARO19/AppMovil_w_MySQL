<?php
    // Se define la conexión con la base de datos MySQL
    $conexion = mysqli_connect('127.0.0.1', 'root', '', 'agenda');
    
    if (!$conexion) {
        die('La conexion no se ha podido realizar.' . mysqli_error());
    } else {
        // Seleccionar la base de datos
        mysqli_select_db($conexion, "Agenda");
        
        // Instrucción SQL de selección para obtener todos los contactos, incluidos sus idContacto
        $consulta = "SELECT idContacto, Nombre, Apellidos, Telefono, Email FROM Contactos";
        
        // Ejecutar la consulta
        $resultado = mysqli_query($conexion, $consulta) or die(mysqli_error());
        
        // Obtener el número de registros
        $registros = mysqli_num_rows($resultado);
        
        // Arreglo para almacenar la información
        $datos = array();

        // Si existen registros, se colocan en el arreglo
        if ($registros > 0) {
            while ($fila = mysqli_fetch_object($resultado)) {
                $datos[] = $fila;
            }
            // Convertir el arreglo a JSON y enviarlo como respuesta
            $arreglo = json_encode($datos);
            echo $arreglo;
        } else {
            // Si no se encuentran registros, enviar "0"
            echo "0";
        }

        // Cerrar la conexión con la base de datos
        mysqli_close($conexion);
    }
?>
