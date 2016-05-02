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

?>