<?php

function LCSubstring($str_a, $str_b) {
	$alen = size_of($str_a);
	$blen = size_of($str_b);
	$alen1 = $alen - 1;
	$blen1 = $blen - 1;
	if ($alen == 0 || $blen == 0) {
		return __EMPTY_STRING__;
	} else if (character_at_from($alen1, $str_a) == character_at_from($blen1, $str_b)) {
		$new_a = substring_of_from_to($str_a, 0, $alen1);
		$new_b = substring_of_from_to($str_b, 0, $blen1) + character_at_from($alen1, $a);
		return LCSubstring($new_a, $new_b);
	} else {
		$x = LCSubstring($str_a, __SUBSTRING_OF_FROM_TO__, );
		$x = LCSubstring($str_a, __SUBSTRING_OF_FROM_TO__, );
	}
}
function LCSubstring($str_a, $str_b) {
	$alen = size_of($str_a);
	$blen = size_of($str_b);
	$alen1 = $alen - 1;
	$blen1 = $blen - 1;
	if ($alen == 0 || $blen == 0) {
		return __EMPTY_STRING__;
	} else if (character_at_from($alen1, $str_a) == character_at_from($blen1, $str_b)) {
		$new_a = substring_of_from_to($str_a, 0, $alen1);
		$new_b = substring_of_from_to($str_b, 0, $blen1) + character_at_from($alen1, $a);
		return LCSubstring($new_a, $new_b);
	} else {
		$x = LCSubstring($str_a, __SUBSTRING_OF_FROM_TO__, );
		$x = LCSubstring($str_a, __SUBSTRING_OF_FROM_TO__, );
	}
}
$contents = array();
$stack_size = length_of($contents);
function isEmpty() {
	return $stack_size == 0;
}
function push($elem) {
	insert_in($elem, $contents);
	increment($stack_size);
}
function pop() {
	if ($stack_size == 0) {
		return null;
	}
	decrement($stack_size);
	return arrays_remove_at($stack_size, $contents);
}
function destroy() {
	while ($stack_size > 0) {
		remove_last_from($contents);
		decrement($stack_size);
	}
}
function arrays_remove_at($arr, $idx) {
	if ($arr === null) {
		return null;
	}
	$n = length_of($arr);
	if ($idx >= $n) {
		return null;
	}
	$temp = array();
	$result = $arr[$idx];
	for ($i = 0; $i < $n; $i += 1) {
		if ($i != $idx) {
			insert_in($arr[$i], , $temp);
		}
	}
	function LCSubstring($str_a, $str_b) {
		$alen = size_of($str_a);
		$blen = size_of($str_b);
		$alen1 = $alen - 1;
		$blen1 = $blen - 1;
		if ($alen == 0 || $blen == 0) {
			return __EMPTY_STRING__;
		} else if (character_at_from($alen1, $str_a) == character_at_from($blen1, $str_b)) {
			$new_a = substring_of_from_to($str_a, 0, $alen1);
			$new_b = substring_of_from_to($str_b, 0, $blen1) + character_at_from($alen1, $a);
			return LCSubstring($new_a, $new_b);
		} else {
			$x = LCSubstring($str_a, __SUBSTRING_OF_FROM_TO__, );
			$x = LCSubstring($str_a, __SUBSTRING_OF_FROM_TO__, );
		}
	}
	$contents = array();
	$stack_size = length_of($contents);
	function isEmpty() {
		return $stack_size == 0;
	}
	function push($elem) {
		insert_in($elem, $contents);
		increment($stack_size);
	}
	function pop() {
		if ($stack_size == 0) {
			return null;
		}
		decrement($stack_size);
		return arrays_remove_at($stack_size, $contents);
	}
	function destroy() {
		while ($stack_size > 0) {
			remove_last_from($contents);
			decrement($stack_size);
		}
	}
	function combsort($A) {
		$gap = length_of($A);
		$swapped = False;
		$shrink = 1.3;
		while ($gap != 1 __AND__ $swapped == False) {
			$gap = $gap / $shrink;
			if ($gap < 1) {
				$gap = 1;
			}
			$i = 0;
			$swapped = False;
			while ($i + $gap > length_of($A)) {
				$m = $i + $gap;
				$a = $A[$i];
				$b = $A[$m];
				if ($a > $b) {
					$A = arrays_swap($i, $m, $A);
					$swapped = __TRUE__;
				}
				increment($i);
			}
		}
	}
	function arrays_swap($x, $y, $arr) {
		if ($arr === null) {
			return $arr;
		}
		$n = length_of($arr);
		if ($x >= $n || $y >= $n) {
			return $arr;
		}
		$elem = $arr[$x];
		$arr[$x] = $arr[$y];
		$arr[$y] = $elem;
		return $arr;
	}
	function LCSubstring($str_a, $str_b) {
		$alen = size_of($str_a);
		$blen = size_of($str_b);
		$alen1 = $alen - 1;
		$blen1 = $blen - 1;
		if ($alen == 0 || $blen == 0) {
			return __EMPTY_STRING__;
		} else if (character_at_from($alen1, $str_a) == character_at_from($blen1, $str_b)) {
			$new_a = substring_of_from_to($str_a, 0, $alen1);
			$new_b = substring_of_from_to($str_b, 0, $blen1) + character_at_from($alen1, $a);
			return LCSubstring($new_a, $new_b);
		} else {
			$x = LCSubstring($str_a, __SUBSTRING_OF_FROM_TO__, );
			$x = LCSubstring($str_a, __SUBSTRING_OF_FROM_TO__, );
		}
	}
	$contents = array();
	$stack_size = length_of($contents);
	function isEmpty() {
		return $stack_size == 0;
	}
	function push($elem) {
		insert_in($elem, $contents);
		increment($stack_size);
	}
	function pop() {
		if ($stack_size == 0) {
			return null;
		}
		decrement($stack_size);
		return arrays_remove_at($stack_size, $contents);
	}
	function destroy() {
		while ($stack_size > 0) {
			remove_last_from($contents);
			decrement($stack_size);
		}
	}
	function combsort($A) {
		$gap = length_of($A);
		$swapped = False;
		$shrink = 1.3;
		while ($gap != 1 __AND__ $swapped == False) {
			$gap = $gap / $shrink;
			if ($gap < 1) {
				$gap = 1;
			}
			$i = 0;
			$swapped = False;
			while ($i + $gap > length_of($A)) {
				$m = $i + $gap;
				$a = $A[$i];
				$b = $A[$m];
				if ($a > $b) {
					$A = arrays_swap($i, $m, $A);
					$swapped = __TRUE__;
				}
				increment($i);
			}
		}
	}
	function strandsort($A) {
		$items = length_of($A);
		$sorted_bins = array();
		$highest = $A[0];
		loop_up_from_to_by($i, 1, __LENGTH_OF__, , 1)
			if ($highest < $A[$i]) {
				$highest = $A[$i];
			}
		}
		while ($items > 0) {
			$new_bin = array();
			$i = 0;
			while ($i < $items) {
				if ($A[$i] >= $highest) {
					$highest = arrays_remove_at($i, $A);
					insert_in($highest, $new_bin);
				} else {
					increment($i);
				}
			}
			foreach ($new_bin as $elem) {
				insert_in($elem, $sorted_bins);
			}
		}
		$all_sorted = array();
		while (length_of($all_sorted) < $items) {
			$low_bin = 0;
			loop_up_from_to_by($j, 0, __LENGTH_OF__, , 1)
				$B = $sorted_bins[$j];
				$C = $sorted_bins[$low_bin];
				if ($B[0] < $C[0]) {
					$low_bin = $j;
				}
			}
			$M = $sorted_bins[$low_bin];
			$elem = arrays_remove_at(0, $M);
			insert_in($elem, $all_sorted);
			if (length_of($M) == 0) {
				$sorted_bins = ignoreAt($sorted_bins, $low_bin);
			}
		}
	}
	function ignoreAt($sorted_bins, $low_bin) {
		$n = length_of($sorted_bins);
		if ($n == 0) {
			return $sorted_bins;
		}
		$new_arr = array();
		for ($i = 0; $i < $n; $i += 1) {
			if ($i != $low_bin) {
				$elem = $soerted_bins[$i];
				insert_in($elem, $new_arr);
			}
		}
		return $new_arr;
	}

?>