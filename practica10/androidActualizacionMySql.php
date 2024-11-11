<?php
    //Variables que reciben la información para almacenar
    $nombre = $_GET['nombre'];
    $apellidos = $_GET['apellidos'];
    $telefono = $_GET['telefono'];
    $correo = $_GET['email'];

    //Se define una variable para establecer la conexion con el servidor de base de datos MySQL.
        //La función mysqli_connect() recibe como parámetros el nombre del servidor, nombre de usuario y contraseña para acceder al gestor de BD.
        //$conexion = mysqli_connect('192.168.0.8:8889','llopez','12345'); FO6)w2iphnLzjZa_
        $conexion = mysqli_connect('127.0.0.1','root','','agenda');
        
        if(!$conexion){
            //La función die finaliza la ejecución de conexión y la función mysqli_error() muestra el tipo de error detectado. 
            die('La conexion no se ha podido realizar.'.mysqli_error());
        } else{
                    
            //Se selecciona la base de datos
            //La instruccción mysqli_select_db() recibe como parámetros la conexión y el nombre de la base de datos.
            mysqli_select_db($conexion,"Agenda");
            
            //Instrucción SQL de actualización
            $actualizar = "UPDATE Contactos SET Telefono = '$telefono', Email = '$correo' WHERE Nombre = '$nombre' and Apellidos = '$apellidos'";

            //ejecución de inserción
            mysqli_query($conexion,$actualizar) or die (mysqli_error());

            //Cerrar la conexion con la base de datos 
            mysqli_close($conexion);
        }//else
    
?>