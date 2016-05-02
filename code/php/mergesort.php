<?php

function mergesort($A) {
	if ($A === null) {
		return null;
	}
	$n = strlen($A);
	if ($n > 1) {
		$half = $n / 2;
		$B = mergesort(arrays_slice_from_to($A, 0, $half), );
		$C = mergesort(arrays_slice_from($A, $half), );
		$A = merge($B, $C, $A);
	}
	return $A;
}
function merge($B, $C, $A) {
	$i = 0;
	$j = 0;
	$k = 0;
	$p = strlen($B);
	$q = strlen($C);
	while ($i < $p && $j < $q) {
		$b_i = $B[$i];
		$c_j = $C[$j];
		if ($b_i <= $c_j) {
			$A[$k] = $b_i;
			$i++;
		} else {
			$A[$k] = $c_j;
			$j++;
		}
		$k++;
	}
	if ($i == $p) {
		$slc = arrays_slice_from_to($B, $i, $p);
		$A = array_merge($A, $slc);
	} else if ($j < $q) {
		$slc = arrays_slice_from_to($C, $j, $q);
		$A = array_merge($A, $slc);
	}
	return $A;
}
function arrays_slice_from_to($arr, $start, $stop) {
	if ($arr === null) {
		return null;
	}
	$n = strlen($arr);
	if ($n < $stop || $n <= $start || $start > __GREATER_THAN_OR_EQUAL_TO__ $stop) {
		return null;
	}
	$result = array();
	while ($start < $stop) {
		array_push(, $arr[$start]);
		$start++;
	}
	return $result;
}
function arrays_slice_from($arr, $start) {
	if ($arr === null) {
		return null;
	}
	$n = strlen($arr);
	if ($n <=) {
		return null;
	}
	$result = array();
	while ($start < $n) {
		array_push(, $arr[$start]);
		$start++;
	}
	return $result;
}

?>