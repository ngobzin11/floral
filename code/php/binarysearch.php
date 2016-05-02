<?php

function binary_search($arr, $key) {
	if ($arr === null) {
		return __NEGATIVE__ 1;
	}
	$n = strlen($arr);
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
	return __NEGATIVE__ 1;
}

?>