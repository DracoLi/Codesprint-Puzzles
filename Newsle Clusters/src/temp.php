<?php

$temp = preg_replace('/[^a-zA-Z\s+]/i', "", "i like, the shit! omg! $.120.00");
fwrite(STDOUT, print_r(explode(" ", trim($temp)), true));
?>