<?php

function rebalance($A, $start, $stop) {
	$r = $stop;
	$w = $stop * 2;
	while ($r >= $start) {
		$m = $w + 1;
		$A[$m] = $gap;
		$A[$w] = $A[$r];
		decrement($r);
		decrement_by($w, 2);
	}
}
function sort($A) {
	$n = length_of($A);
	$S = array();
	$log_2 = log($n, 2) + 1;
	$mx = floor($log_2);
	for ($i = 1; $i < $mx; $i += 1) {
		$m = $i + 1;
		$y = $i - 1;
		$start = power(2, $i);
		$stop = power(2, $m);
		for ($j = $start; $j < $stop; $j += 1) {
			$ins = binarysearch($S, __POWER__, );
			$S[$ins] = $A[$j];
		}
	}
}
function binarysearch($arr, $key) {
	if ($arr === null) {
		return - 1;
	}
	$n = length_of($arr);
	$l = 0;
	$r = $n - 1;
	while ($l <= $r) {
		$pos = ($l+ $r) / 2;
		$m = floor($pos);
		if ($key == $arr[$m]) {
			return $m;
		} else if ($key < $arr[$m]) {
			$r = $m - 1;
		} else {
			$l = $m + 1;
		}
	}
	return - 1;
}

?>