<?php

function floyd($W) {
	$n = strlen($W);
	$D = arrays_copy($W);
	for ($k = 0 ; $k < $n ; $k++) {
		$D_k = $D[$k];
		for ($i = 0 ; $i < $n ; $i++) {
			$D_i = $D[$i];
			$D_ik = $D_i[$k];
			for ($j = 0 ; $j < $n ; $j++) {
				$sm = $D_ik + $D_k[$j];
				if ($sm < $D_i[$j]) {
					$D_i[$j] = $sm;
				}
			}
			$D[$i] = $D_i;
		}
	}
	return $D;
}
function arrays_copy($A) {
	$res = array();
	foreach ($A as $a_i) {
		array_push($res, $a_i);
	}
	return $res;
}

?>