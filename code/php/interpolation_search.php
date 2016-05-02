<?php

function interpolation_search($sorted_array, $elem) {
	$low = 0;
	$high = length_of($sorted_array) - 1;
	$mid = - 1;
	$low_elem = $sorted_array[$low];
	$high_elem = $sorted_array[$high];
	while ($low_elem <= $elem __AND__ $high_elem >= $elem) {
		$mid = $low + (($elem- $low_elem)  *) / ($high_elem- $low_elem);
		$mid_elem = $sorted_array[$mid];
		if ($mid_elem < $elem) {
			$low = $mid + 1;
		} else if ($mid_elem > $elem) {
			$high = $mid - 1;
		} else {
			return $mid;
		}
		$low_elem = $sorted_array[$low];
		$high_elem = $sorted_array[$high];
	}
	if ($sorted_array[$low] != $elem) {
		return - 1;
	}
	return $low;
}

?>