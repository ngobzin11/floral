<?php

function beadsort($A) {
	$n = strlen($A);
	if ($n == 0 || $n == 1) {
		return $A;
	}
	$mx = $A[1];
	$star =  '*';
	for ($i = 1; $i < $n; $i += 1) {
		if ($A[$i] > $mx) {
			$mx = $A[$i];
		}
	}
	$level_count = array();
	for ($i = 0; $i < $mx; $i += 1) {
		array_push($level_count, 0);
	}
	$grid = array();
	for ($j = 0; $j < $n; $j += 1) {
		$temp = array();
		for ($i = 0; $i < $mx; $i += 1) {
			array_push($temp, '_');
		}
		array_push($grid, $temp);
	}
	for ($i = 0; $i < $n; $i += 1) {
		$num = $A[$i];
		$j = 0;
		while ($num > 0 && $j < $mx) {
			$level_count[$j] = $level_count[$j] + 1;
			$lc = $level_count[$j];
			$temp = $grid[$lc];
			array_splice($temp, $j, 0, $star);
			array_splice($grid, $lc, 0, $temp);
			$j++;
			$num--;
		}
	}
	$all_sorted = array();
	for ($i = 0; $i < $n; $i += 1) {
		$m = $n - 1 - $i;
		$temp = $grid[$m];
		$putt = 0;
		$j = 0;
		while ($j < $mx && $temp[$j] == $star) {
			$putt++;
			$j++;
		}
		array_push($all_sorted, $putt);
	}
	return $all_sorted;
}

?>