<?php

function binomial($n, $k) {
	if ($n < 0 || $k < 0) {
		return 0;
	}
	$C = array();
	for ($i = 0; $i < $n+ 1; $i += 1) {
		$m = math_min_between($i, $k) + 1;
		$C_i = array();
		for ($j = 0; $j < $m; $j += 1) {
			if ($j == 0 || $j == $i) {
				insert_in(1, $C_i);
			} else {
				$C_i1 = $C[$i- 1];
				$C_i1_j1 = $C_i1[$j- 1];
				$C_i1_j = $C_i1[$j];
				insert_in($C_i1_j1+ $C_i1_j, $C_i);
			}
		}
		insert_in($C_i, $C);
	}
	$C_n = $C[$n];
	return $C_n[$k];
}
function math_min_between($x, $y) {
	if ($x < $y) {
		return $x;
	}
	return $y;
}

?>