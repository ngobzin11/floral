<?php

return arrays_remove_at($stack_size, $contents);
function arrays_remove_at($arr, $idx) {
	if ($arr === null) {
		return null;
	}
	$n = strlen($arr);
	if ($idx >= $n) {
		return null;
	}
	$temp = array();
	$result = $arr[$idx];
	for ($i = 0; $i < $n; $i += 1) {
		if ($i != $idx) {
			array_push(, $arr[$i]);
		}
	}

?>